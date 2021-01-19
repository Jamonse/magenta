package com.jsoft.magenta.projects;

import com.jsoft.magenta.accounts.AccountAssociationRepository;
import com.jsoft.magenta.accounts.AccountRepository;
import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.accounts.domain.AccountAssociation;
import com.jsoft.magenta.exceptions.DuplicationException;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.exceptions.RedundantAssociationException;
import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.projects.domain.ProjectAssociation;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProjectService
{
    private final ProjectRepository projectRepository;
    private final AccountRepository accountRepository;
    private final ProjectAssociationRepository projectAssociationRepository;
    private final AccountAssociationRepository accountAssociationRepository;
    private final UserRepository userRepository;

    public Project createProject(Long accountId, Project project)
    {
        Account account = findAccount(accountId);
        this.projectRepository // Searches for project with same name existent for account
                .findByAccountIdAndName(accountId, project.getName())
                .ifPresent(this::throwProjectNameExistsException);
        project.setAvailable(true);
        project.setCreatedAt(LocalDate.now());
        project.setAccount(account);
        return this.projectRepository.save(project);
    }

    public void createAssociation(Long userId, Long projectId, AccessPermission accessPermission)
    {
        User user = findUser(userId);
        Project project = findProject(projectId); // Get user and project for the association
        this.projectAssociationRepository // Find weather such association exists
                .findByUserIdAndProjectId(userId, projectId)
                .ifPresent(this::throwAssociationExistException); // Throw exception if exists
        if(accessPermission == AccessPermission.READ) // Association is redundant if access permission is READ
            throw new RedundantAssociationException( // - No sub projects
                    "Read association with project while no sub project exist is redundant");
        else
        { // Verify that association exists with the project account
            Account account = project.getAccount();
            this.accountAssociationRepository
                    .findByUserIdAndAccountId(userId, account.getId())
                    .orElseGet(() -> createAccountAssociation(user, account)); // Create READ association if does not exist
        } // Create association and save
        ProjectAssociation projectAssociation = new ProjectAssociation(user, project, accessPermission);
        this.projectAssociationRepository.save(projectAssociation);
    }

    public Project updateProject(Project project)
    {
        Project projectToUpdate = findProject(project.getId());
        this.projectRepository // Searches for project with same name existent for account
                .findByAccountIdAndName(projectToUpdate.getAccount().getId(), project.getName())
                .ifPresent(this::throwProjectNameExistsException);
        projectToUpdate.setName(project.getName());
        projectToUpdate.setAvailable(project.isAvailable());
        projectToUpdate.setSubProjects(projectToUpdate.getSubProjects());
        projectToUpdate.setAssociations(project.getAssociations());
        projectToUpdate.setOrders(project.getOrders());
        return this.projectRepository.save(project);
    }

    public Project updateProjectName(Long projectId, String newName)
    {
        Project projectToUpdate = findProject(projectId);
        this.projectRepository // Searches for project with same name existent for account
                .findByAccountIdAndName(projectToUpdate.getAccount().getId(), newName)
                .ifPresent(this::throwProjectNameExistsException);
        projectToUpdate.setName(newName);
        return this.projectRepository.save(projectToUpdate);
    }

    private Project findProject(Long projectId)
    {
        return this.projectRepository
                .findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("Project not found"));
    }

    private Account findAccount(Long accountId)
    {
        return this.accountRepository
                .findById(accountId)
                .orElseThrow(() -> new NoSuchElementException("Account not found"));
    }

    private User findUser(Long userId)
    {
        return this.userRepository
                .findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
    }

    private AccountAssociation createAccountAssociation(User user, Account account)
    {
        AccountAssociation accountAssociation = new AccountAssociation(user, account, AccessPermission.READ);
        return this.accountAssociationRepository.save(accountAssociation);
    }

    private void throwProjectNameExistsException(Project project)
    {
        throw new DuplicationException(
                String.format("Project with name %s already exist for account", project.getName())
        );
    }

    private void throwAssociationExistException(ProjectAssociation projectAssociation)
    {
        String userName = projectAssociation.getUser().getName();
        String projectName = projectAssociation.getProject().getName();
        throw new DuplicationException(
                String.format("Association between user %s and project %s already exist", userName, projectName)
        );
    }

}
