package com.jsoft.magenta.subprojects;

import com.jsoft.magenta.events.projects.ProjectAssociationUpdateEvent;
import com.jsoft.magenta.events.projects.ProjectRelatedEntityEvent;
import com.jsoft.magenta.events.subprojects.SubProjectAssociationCreationEvent;
import com.jsoft.magenta.events.subprojects.SubProjectAssociationRemovalEvent;
import com.jsoft.magenta.events.subprojects.SubProjectRelatedEntityEvent;
import com.jsoft.magenta.exceptions.AuthorizationException;
import com.jsoft.magenta.exceptions.DuplicationException;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.exceptions.RedundantAssociationException;
import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.security.SecurityService;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;

@Service
@Transactional
@RequiredArgsConstructor
public class SubProjectService {
    private final SubProjectRepository subProjectRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final SecurityService securityService;

    public SubProject createSubProject(Long projectId, SubProject subProject) { // Validate creator has valid
        // permission with project
        this.eventPublisher.publishEvent(new ProjectRelatedEntityEvent(projectId));
        validateProjectUniqueName(projectId, subProject.getName()); // Validate name uniqueness
        subProject.setAvailable(true);
        subProject.setProject(new Project(projectId));
        return this.subProjectRepository.save(subProject);
    }

    public SubProject createAssociation(Long userId, Long subProjectId) { // Find sub-project
        SubProject subProject = findSubProject(subProjectId);
        Long projectId = findProjectId(subProjectId);
        // Handle association check with project and account
        this.eventPublisher.publishEvent(new SubProjectAssociationCreationEvent(projectId, userId));
        subProject.setUsers(Set.of(new User(userId))); // Add the specified user and save sub-project
        return this.subProjectRepository.save(subProject);
    }

    private Long findProjectId(Long subProjectId) {
        return this.subProjectRepository
                .findProjectIdById(subProjectId)
                .orElseThrow(() -> new NoSuchElementException("Project not found"));
    }

    public SubProject updateSubProject(SubProject subProject) {
        SubProject subProjectToUpdate = findSubProject(subProject.getId());
        Long projectId = findProjectId(subProject.getId());
        this.eventPublisher.publishEvent(new ProjectRelatedEntityEvent(projectId));
        if (!subProject.getName().equalsIgnoreCase(subProjectToUpdate.getName()))
            validateProjectUniqueName(projectId, subProject.getName()); // Validate name uniqueness
        subProjectToUpdate.setName(subProject.getName()); // Update allowed fields
        subProjectToUpdate.setAvailable(subProject.isAvailable());
        subProjectToUpdate.setAmountOfHours(subProject.getAmountOfHours());
        return this.subProjectRepository.save(subProjectToUpdate);
    }

    public SubProject updateSubProjectName(Long subProjectId, String newName) {
        Long projectId = findProjectId(subProjectId);
        this.eventPublisher.publishEvent(new ProjectRelatedEntityEvent(projectId));
        SubProject subProjectToUpdate = findSubProject(subProjectId);
        validateProjectUniqueName(projectId, newName); // Validate name uniqueness
        subProjectToUpdate.setName(newName);
        return this.subProjectRepository.save(subProjectToUpdate);
    }

    public SubProject updateSubProjectHours(Long subProjectId, double newAmount) {
        Long projectId = findProjectId(subProjectId);
        this.eventPublisher.publishEvent(new ProjectRelatedEntityEvent(projectId));
        SubProject subProjectToUpdate = findSubProject(subProjectId);
        subProjectToUpdate.setAmountOfHours(newAmount);
        return this.subProjectRepository.save(subProjectToUpdate);
    }

    public SubProject increaseSubProjectHours(Long subProjectId, double amountToAdd) {
        Long projectId = findProjectId(subProjectId);
        this.eventPublisher.publishEvent(new ProjectRelatedEntityEvent(projectId));
        SubProject subProjectToUpdate = findSubProject(subProjectId);
        subProjectToUpdate.setAmountOfHours(subProjectToUpdate.getAmountOfHours() + amountToAdd);
        return this.subProjectRepository.save(subProjectToUpdate);
    }

    public SubProject decreaseSubProjectHours(Long subProjectId, double amountToRemove) {
        Long projectId = findProjectId(subProjectId);
        this.eventPublisher.publishEvent(new ProjectRelatedEntityEvent(projectId));
        SubProject subProjectToUpdate = findSubProject(subProjectId);
        subProjectToUpdate.setAmountOfHours(subProjectToUpdate.getAmountOfHours() - amountToRemove);
        return this.subProjectRepository.save(subProjectToUpdate);
    }

    public void removeAssociation(Long userId, Long subProjectId) { // Find sub-project
        SubProject subProject = this.subProjectRepository
                .findByIdAndUsersId(subProjectId, userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Association between user and specified sub-project does not exists"));
        User user = securityService.currentUser();
        AccessPermission accessPermission = // Find user project permission
                user.getProjectPermission();
        switch (accessPermission) {
            case READ: // User is not an editor or an admin
            case MANAGE:
                throw new AuthorizationException("User is not authorized to perform such operation");
            case WRITE: // Find permission level of association with sub-project's project
                Long projectId = findProjectId(subProjectId);
                this.eventPublisher.publishEvent(new ProjectRelatedEntityEvent(projectId));
            case ADMIN: // Remove the association and handle any backward redundant associations
                subProject.removeAssociation(userId);
                this.eventPublisher.publishEvent(new SubProjectAssociationRemovalEvent(subProject, userId));
        }
    }

    public void removeAllAssociations(Long subProjectId) {
        SubProject subProject = findSubProject(subProjectId);
        User user = securityService.currentUser();
        AccessPermission accessPermission = user.getProjectPermission();
        switch (accessPermission) {
            case READ: // User is not an editor or an admin
            case MANAGE:
                throw new AuthorizationException("User is not authorized to perform such operation");
            case WRITE: // Find permission level of association with sub-project's project
                Long projectId = findProjectId(subProjectId);
                this.eventPublisher.publishEvent(new ProjectRelatedEntityEvent(projectId));
            case ADMIN: // Remove all associations and handle any backward redundant associations
                removeAllAssociations(subProject);
        }
    }

    public void deleteSubProject(Long subProjectId) {
        removeAllAssociations(subProjectId);
        this.subProjectRepository.deleteById(subProjectId);
    }

    private void removeAllAssociations(SubProject subProject) {
        subProject.getUsers().forEach(user -> {
            user.removeSubProject(subProject);
            this.eventPublisher.publishEvent(new SubProjectAssociationRemovalEvent(subProject, user.getId()));
        });
    }

    @EventListener
    public void handleProjectAssociationEvent(ProjectAssociationUpdateEvent projectAssociationEvent) {
        AccessPermission accessPermission = projectAssociationEvent.getPermission();
        if (accessPermission == AccessPermission.READ)
            this.subProjectRepository // Verify that at least one sub-project association exists
                    .findFirstByUsersId(projectAssociationEvent.getAssociatedUserId()) // in case of update to READ
                    // permission
                    .orElseThrow(() -> new RedundantAssociationException(
                            "Read association with project while no sub project exist is redundant"));
    }

    @EventListener
    public void handleSubProjectRelatedEntityEvent(SubProjectRelatedEntityEvent relatedEntityEvent) {
        Long subProjectId = relatedEntityEvent.getPayload();
        isSubProjectExists(subProjectId);
    }

    private void isSubProjectExists(Long subProjectId) {
        boolean exists = this.subProjectRepository.existsById(subProjectId);
        if (!exists)
            throw new NoSuchElementException("Sub-project not found");
    }

    private SubProject findSubProject(Long subProjectId) {
        return this.subProjectRepository
                .findById(subProjectId)
                .orElseThrow(() -> new NoSuchElementException("Sub-project not found"));
    }

    private void validateProjectUniqueName(Long projectId, String subProjectName) {
        boolean exist = this.subProjectRepository // Searches for project with same name existent for account
                .existsByProjectIdAndName(projectId, subProjectName);
        if (exist)
            throw new DuplicationException(String.format("Sub-project with name %s already exists for project",
                    subProjectName));
    }

    private void throwSubProjectNameExistsException(SubProject subProject) {
        throw new DuplicationException(
                String.format("Sub-project with name %s already exist for project", subProject.getName())
        );
    }

}
