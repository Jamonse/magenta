package com.jsoft.magenta.projects;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.events.accounts.AccountAssociatedEntityEvent;
import com.jsoft.magenta.events.accounts.AccountAssociationUpdateEvent;
import com.jsoft.magenta.events.projects.ProjectAssociationCreationEvent;
import com.jsoft.magenta.events.projects.ProjectAssociationRemovalEvent;
import com.jsoft.magenta.events.projects.ProjectAssociationUpdateEvent;
import com.jsoft.magenta.events.projects.ProjectRelatedEntityEvent;
import com.jsoft.magenta.events.subprojects.SubProjectAssociationCreationEvent;
import com.jsoft.magenta.events.subprojects.SubProjectAssociationRemovalEvent;
import com.jsoft.magenta.exceptions.AuthorizationException;
import com.jsoft.magenta.exceptions.DuplicationException;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.exceptions.RedundantAssociationException;
import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.projects.domain.ProjectAssociation;
import com.jsoft.magenta.projects.domain.ProjectAssociationId;
import com.jsoft.magenta.projects.domain.ProjectSearchResult;
import com.jsoft.magenta.security.SecurityService;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.subprojects.SubProject;
import com.jsoft.magenta.subprojects.SubProjectSearchResult;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.util.AppDefaults;
import com.jsoft.magenta.util.pagination.PageRequestBuilder;
import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService {

  private final ProjectRepository projectRepository;
  private final ProjectAssociationRepository projectAssociationRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final SecurityService securityService;

  public Project createProject(Long accountId,
      Project project) { // Validate if creator is admin or has write
    // permission with account
    this.eventPublisher.publishEvent(new AccountAssociatedEntityEvent(accountId));
    validateProjectUniqueName(accountId, project.getName()); // Validate name uniqueness
    project.setAvailable(true);
    project.setCreatedAt(LocalDate.now());
    project.setAccount(new Account(accountId));
    return this.projectRepository.save(project);
  }

  public void createAssociation(Long userId, Long projectId,
      AccessPermission accessPermission) { // Validate if
    // creator is admin or has write permission with project
    validateProjectAssociationPermission(projectId);
    isProjectExists(projectId);
    isAssociationExists(userId, projectId);
    if (accessPermission
        == AccessPermission.READ) // Association is redundant if access permission is READ
    {
      throw new RedundantAssociationException( // - No sub projects
          "Read association with project while no sub project exist is redundant");
    }
    // Verify that the user has a valid permission level with corresponding entity
    // Verify that association exists with the project account by publishing creation event
    Long accountId = findAccountId(projectId);
    this.eventPublisher
        .publishEvent(new ProjectAssociationCreationEvent(accountId, userId, accessPermission));
    // Create association and save
    ProjectAssociation projectAssociation = new ProjectAssociation(userId, projectId,
        accessPermission);
    this.projectAssociationRepository.save(projectAssociation);
  }

  public Project updateProject(Project project) {
    // Validate if updater is admin or has write permission with project
    Project projectToUpdate = findProject(project.getId());
    validateProjectAssociationPermission(projectToUpdate.getId());
    Long accountId = findAccountId(project.getId());
    if (!project.getName().equalsIgnoreCase(projectToUpdate.getName())) {
      validateProjectUniqueName(accountId, project.getName()); // Validate name uniqueness
    }
    projectToUpdate.setName(project.getName()); // Update allowed fields
    projectToUpdate.setAvailable(project.isAvailable());
    return this.projectRepository.save(project);
  }

  public Project updateProjectName(Long projectId,
      String newName) { // Validate if updater is admin or has write
    // permission with project
    Project projectToUpdate = findProject(projectId);
    if (!newName.equalsIgnoreCase(projectToUpdate.getName())) {
      validateProjectUniqueName(projectToUpdate.getAccount().getId(), newName);
    }
    projectToUpdate.setName(newName);
    return this.projectRepository.save(projectToUpdate);
  }

  public void updateAssociation(Long userId, Long projectId,
      AccessPermission newPermission) {// Validate if
    // updater is admin or has write permission with project
    validateProjectAssociationPermission(projectId);
    //Verify that association exists
    ProjectAssociation projectAssociation = findProjectAssociation(userId, projectId);
    // Verify Redundant association in case of READ permission and permission level allowance by publishing event
    this.eventPublisher
        .publishEvent(new ProjectAssociationUpdateEvent(projectId, userId, newPermission));
    // Update association
    projectAssociation.setPermission(newPermission);
    this.projectAssociationRepository.save(projectAssociation);
  }

  public Project getProject(Long projectId) { // Find project permission of caller
    AccessPermission accessPermission = getProjectPermission();
    switch (accessPermission) {
      case READ: // Cannot get project information as reader
        throw new AuthorizationException(
            "Cannot access project information with specified permission");
      case MANAGE: // Validate association with specified project
      case WRITE:
        AccessPermission projectPermission = findProjectAssociation(projectId);
        if (projectPermission == AccessPermission.READ) {
          throw new AuthorizationException("User is not allowed to retreive project data");
        }
      case ADMIN: // Return the project
        return findProject(projectId);
      default:
        throw new UnsupportedOperationException(
            "Accessing project information with specified permission is not supported");
    }
  }

  public Page<Project> getAllProjects(int pageIndex, int pageSize, String sortBy,
      boolean asc) { // Get user id and
    // corresponding permission level with projects
    Long userId = securityService.currentUserId();
    AccessPermission accessPermission = getProjectPermission();
    switch (accessPermission) {
      case READ: // Get only projects with read permission
        return getAllProjectsByUserIdAndPermission(userId, accessPermission, pageIndex, pageSize,
            sortBy, asc);
      case MANAGE:
      case WRITE: // Get all projects that are manage or write permission
        return getAllProjectsByUserIdAndPermissionGreaterThanEqual(
            userId, AccessPermission.MANAGE, pageIndex, pageSize, sortBy, asc);
      case ADMIN: // Get all projects in the system
        return findAllProjects(pageIndex, pageSize, sortBy, asc);
      default:
        return Page.empty();
    }
  }

  public Page<Project> getAllProjectsByUserIdAndPermission(
      Long userId, AccessPermission accessPermission, int pageIndex, int pageSize, String sortBy,
      boolean asc) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
    Page<Project> results = this.projectRepository
        .findAllByAssociationsUserIdAndAssociationsPermission(
            userId, accessPermission, pageRequest);
    return new PageImpl<>(results.getContent(), pageRequest, results.getTotalElements());
  }

  public Page<Project> getAllProjectsByUserIdAndPermissionGreaterThanEqual(
      Long userId, AccessPermission accessPermission, int pageIndex, int pageSize, String sortBy,
      boolean asc) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
    Page<Project> results =
        this.projectRepository.findAllByAssociationsUserIdAndAssociationsPermissionGreaterThanEqual(
            userId, accessPermission, pageRequest);
    return new PageImpl<>(results.getContent(), pageRequest, results.getTotalElements());
  }

  public List<ProjectSearchResult> getAllProjectsResultsByNameExample(String nameExample,
      int resultsCount) { //
    // Collect user information and prepare page request
    AccessPermission accessPermission = getProjectPermission();
    Long userId = securityService.currentUserId();
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(
        0, resultsCount, AppDefaults.PROJECTS_DEFAULT_SORT, false);
    switch (accessPermission) {
      case READ: // Return only read permission results
        return this.projectRepository
            .findAllResultsByAssociationsUserIdAndNameContainingIgnoreCaseAndAssociationsPermission(
                userId, nameExample, accessPermission, pageRequest);
      case MANAGE: // Return manage permission results and up
      case WRITE:
        return this.projectRepository
            .findAllResultsByAssociationsUserIdAndNameContainingIgnoreCaseAndAssociationsPermissionGreaterThanEqual(
                userId, nameExample, accessPermission, pageRequest);
      case ADMIN: // Return all results
        return this.projectRepository
            .findAllResultsByNameContainingIgnoreCase(nameExample, pageRequest);
      default:
        return List.of();
    }
  }

  public Page<SubProject> getAllProjectSubProjects(
      Long projectId, int pageIndex, int pageSize, String sortBy, boolean asc) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(
        pageIndex, pageSize, sortBy, asc);
    User user = securityService.currentUser();
    AccessPermission accessPermission = user.getProjectPermission();
    Page<SubProject> results;
    if (accessPermission == AccessPermission.ADMIN) {
      results = this.projectRepository.findAllSubProjectsById(projectId, pageRequest);
    } else {
      AccessPermission associationPermission = findProjectAssociationPermission(user.getId(),
          projectId);
      if (associationPermission == AccessPermission.READ) {
        results = this.projectRepository
            .findAllSubProjectsByIdAndAssociationsUserId(projectId, user.getId(),
                pageRequest);
      } else {
        results = this.projectRepository.findAllSubProjectsById(projectId, pageRequest);
      }
    }
    return new PageImpl<>(results.getContent(), pageRequest, results.getTotalElements());
  }

  public List<SubProjectSearchResult> getProjectSubProjectResults(Long projectId,
      int resultsCount) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(
        0, resultsCount, AppDefaults.PROJECTS_DEFAULT_SORT, false);
    User user = securityService.currentUser();
    AccessPermission accessPermission = user.getProjectPermission();
    if (accessPermission == AccessPermission.ADMIN) {
      return this.projectRepository.findAllSubProjectsResultsById(projectId, pageRequest);
    }
    AccessPermission associationPermission = findProjectAssociationPermission(user.getId(),
        projectId);
    if (associationPermission == AccessPermission.READ) {
      return this.projectRepository.findAllSubProjectsResultsByIdAndAssociationsUserId(
          projectId, user.getId(), pageRequest);
    }
    return this.projectRepository.findAllSubProjectsResultsById(projectId, pageRequest);
  }

  public List<SubProjectSearchResult> getProjectSubProjectResultsByNameExample(Long projectId,
      String nameExample,
      int resultsCount) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(
        0, resultsCount, AppDefaults.PROJECTS_DEFAULT_SORT, false);
    User user = securityService.currentUser();
    AccessPermission accessPermission = user.getProjectPermission();
    if (accessPermission == AccessPermission.ADMIN) {
      return this.projectRepository
          .findAllSubProjectsResultsByIdAndNameContainingIgnoreCase(projectId,
              nameExample, pageRequest);
    }
    AccessPermission associationPermission = findProjectAssociationPermission(user.getId(),
        projectId);
    if (associationPermission == AccessPermission.READ) {
      return this.projectRepository
          .findAllSubProjectsResultsByIdAndAssociationsUserIdAndNameContainingIgnoreCase(
              projectId, user.getId(), nameExample, pageRequest);
    }
    return this.projectRepository
        .findAllSubProjectsResultsByIdAndNameContainingIgnoreCase(projectId, nameExample
            , pageRequest);
  }

  public void removeAssociation(Long userId,
      Long projectId) {  // Validate if remover is admin or has write
    // permission with project
    validateProjectAssociationPermission(projectId);
    removeAssociationAfterValidation(userId, projectId);
  }

  public void removeAllAssociations(
      Long projectId) { // Validate if remover is admin or has write permission with
    // project
    validateProjectAssociationPermission(projectId);
    // Perform association removal on all associations of the specified project
    Project project = findProject(projectId);
    project.getAssociations()
        .forEach(projectAssociation -> removeAssociationAfterValidation(
            projectAssociation.getUser().getId(),
            projectAssociation.getProject().getId())
        );
  }

  public void deleteProject(
      Long projectId) { // Validate if remover is admin or has write permission with project
    validateProjectAssociationPermission(projectId);
    removeAllAssociations(projectId);
    this.projectRepository.deleteById(projectId);
  }

  @EventListener
  public void handleAccountAssociationUpdate(AccountAssociationUpdateEvent associationUpdateEvent) {
    boolean associatedWithProjects = this.projectAssociationRepository
        .existsByUserIdAndProjectAccountIdGreaterThanEqual(
            associationUpdateEvent.getAssociatedUserId(), 0L);
    if (!associatedWithProjects) {
      throw new RedundantAssociationException(
          "READ level association with account without any project is " +
              "redundant");
    }
  }

  @EventListener
  public void handleSubProjectAssociationCreationEvent(
      SubProjectAssociationCreationEvent associationCreationEvent) {
    Long projectId = associationCreationEvent.getPayload();
    Long userId = associationCreationEvent.getAssociatedUserId();
    createAssociationIfNotExist(userId, projectId);
  }

  @EventListener
  public void handleSubProjectAssociationRemoval(
      SubProjectAssociationRemovalEvent associationRemovalEvent) {
    Project project = associationRemovalEvent.getPayload().getProject();
    Long userId = associationRemovalEvent.getAssociatedUserId();
    removeRedundantAssociation(userId, project.getId());
  }

  @EventListener
  public void handleProjectRelatedEntityEvent(ProjectRelatedEntityEvent projectRelatedEntityEvent) {
    Long projectId = projectRelatedEntityEvent.getPayload();
    isProjectExists(projectId);
    AccessPermission accessPermission = getProjectPermission();
    switch (accessPermission) {
      case READ:
        throw new AuthorizationException("User is not authorized to perform such action");
      case MANAGE:
      case WRITE:
        AccessPermission associationPermission = findProjectAssociation(projectId);
        if (associationPermission == AccessPermission.READ) {
          throw new AuthorizationException("User is not authorized to perform such action");
        }
      case ADMIN:
        break;
      default:
        throw new UnsupportedOperationException("User is not associated with projects");
    }
  }

  private void removeRedundantAssociation(Long userId,
      Long projectId) { // Check if association exists
    this.projectAssociationRepository // If it is, check for redundancy
        .findByUserIdAndProjectId(userId, projectId)
        .ifPresent(this::checkAssociationRedundancy);
  }

  private void checkAssociationRedundancy(
      ProjectAssociation projectAssociation) { // Get association permission level
    AccessPermission accessPermission = projectAssociation.getPermission();
    Long userId = projectAssociation.getId().getUserId();
    if (accessPermission
        == AccessPermission.READ) { // For READ permission, check for associations with other
      // sub-projects of same project
      boolean associatedWithOtherSubProjects = this.projectRepository
          .existsByAssociationsUserIdAndSubProjectsIdGreaterThanEqual(
              userId, 0);
      if (!associatedWithOtherSubProjects) { // If there are no other associations
        Project project = projectAssociation
            .getProject(); // Publish removal event for project association
        this.eventPublisher.publishEvent(new ProjectAssociationRemovalEvent(project, userId));
        this.projectAssociationRepository // Perform association delete operation
            .delete(projectAssociation);
      }
    }
  }

  private void createAssociationIfNotExist(Long userId, Long projectId) {
    boolean exists = this.projectAssociationRepository
        .existsByUserIdAndProjectId(userId, projectId);
    if (!exists) {
      createReadAssociation(userId, projectId);
    }
  }

  private ProjectAssociation createReadAssociation(Long userId, Long projectId) {
    ProjectAssociation projectAssociation = new ProjectAssociation(userId, projectId,
        AccessPermission.READ);
    Long accountId = findAccountId(projectId);
    this.eventPublisher.publishEvent(
        new ProjectAssociationCreationEvent(accountId, userId, AccessPermission.READ));
    return this.projectAssociationRepository.save(projectAssociation);
  }

  private void removeAssociationAfterValidation(Long userId,
      Long projectId) { // Find if association with
    // specified user exists
    ProjectAssociationId projectAssociationId = new ProjectAssociationId(projectId, userId);
    boolean exist = this.projectAssociationRepository.existsById(projectAssociationId);
    if (!exist) {
      throw new NoSuchElementException("Association does not exist");
    }
    // TODO Complete
    this.projectAssociationRepository.deleteById(projectAssociationId);
    Project project = findProject(projectId);
    // Check for association redundancy by publishing removal event
    this.eventPublisher.publishEvent(new ProjectAssociationRemovalEvent(project, userId));
  }

  private Project findProject(Long projectId) {
    return this.projectRepository
        .findById(projectId)
        .orElseThrow(() -> new NoSuchElementException("Project not found"));
  }

  private void isProjectExists(Long projectId) {
    boolean exists = this.projectRepository.existsById(projectId);
    if (!exists) {
      throw new NoSuchElementException("Project not found");
    }
  }

  private Long findAccountId(Long projectId) {
    return this.projectRepository
        .findAccountIdById(projectId)
        .orElseThrow(() -> new NoSuchElementException("Project not found"));
  }

  private void isAssociationExists(Long userId, Long projectId) {
    boolean exists = this.projectAssociationRepository
        .existsByUserIdAndProjectId(userId, projectId);
    if (exists) {
      throw new DuplicationException("Association between user and project already exists");
    }
  }

  private Page<Project> findAllProjects(int pageIndex, int pageSize, String sortBy, boolean asc) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
    Page<Project> results = this.projectRepository.findAll(pageRequest);
    return new PageImpl<>(results.getContent(), pageRequest, results.getNumberOfElements());
  }

  private AccessPermission getProjectPermission() {
    User user = securityService.currentUser();
    return user.getProjectPermission();
  }

  private AccessPermission findProjectAssociation(Long projectId) {
    Long userId = securityService.currentUserId();
    return this.projectAssociationRepository
        .findAccessPermissionByUserIdAndProjectId(userId,
            projectId) // Otherwise find if associated with
        // account
        .orElseThrow(() -> new AuthorizationException("User is not associated with project"));
  }

  private AccessPermission findProjectAssociationPermission(Long userId, Long projectId) {
    return findProjectAssociation(userId, projectId).getPermission();
  }

  private ProjectAssociation findProjectAssociation(Long userId, Long projectId) {
    ProjectAssociation projectAssociation = this.projectAssociationRepository
        .findByUserIdAndProjectId(userId, projectId)
        .orElseThrow(() -> new AuthorizationException("User is not associated with project"));
    return projectAssociation;
  }

  private void validateProjectAssociationPermission(Long projectId) {
    boolean hasPermission = hasValidProjectEditPermissionPermission(projectId);
    if (!hasPermission) {
      throw new AuthorizationException("User is not authorized to update specified project");
    }
  }

  private boolean hasValidProjectEditPermissionPermission(Long projectId) {
    AccessPermission projectPermission = getProjectPermission();
    if (projectPermission == AccessPermission.ADMIN) {
      return true; // User is accounts admin
    }
    Long userId = securityService.currentUserId();
    AccessPermission associationPermission = findProjectAssociationPermission(userId, projectId);
    return associationPermission
        == AccessPermission.WRITE; // User has write permission association with account
  }

  private void validateProjectUniqueName(Long accountId, String projectName) {
    boolean exist = this.projectRepository // Searches for project with same name existent for account
        .existsByAccountIdAndName(accountId, projectName);
    if (exist) {
      throw new DuplicationException(
          String.format("Project with name %s already exist for account", projectName)
      );
    }
  }

}
