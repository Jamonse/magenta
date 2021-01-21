package com.jsoft.magenta.projects;

import com.jsoft.magenta.accounts.AccountAssociationRepository;
import com.jsoft.magenta.accounts.AccountRepository;
import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.accounts.domain.AccountAssociation;
import com.jsoft.magenta.exceptions.AuthorizationException;
import com.jsoft.magenta.exceptions.DuplicationException;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.exceptions.RedundantAssociationException;
import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.projects.domain.ProjectAssociation;
import com.jsoft.magenta.projects.domain.ProjectSearchResult;
import com.jsoft.magenta.security.UserEvaluator;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.users.UserRepository;
import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.util.AppDefaults;
import com.jsoft.magenta.util.PageRequestBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

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
    private final SubProjectRepository subProjectRepository;

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
        { // Verify that the user has a valid permission level with corresponding entity
            boolean hasPermission = hasValidPermission(user, accessPermission);
            if(!hasPermission)
                throw new AuthorizationException("User is unauthorized to handle such association level");
        } // Verify that association exists with the project account
        Account account = project.getAccount();
        this.accountAssociationRepository
                .findByUserIdAndAccountId(userId, account.getId())
                .orElseGet(() -> createAccountAssociation(user, account)); // Create READ association if does not exist
        // Create association and save
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

    public void updateAssociation(Long userId, Long projectId, AccessPermission newPermission)
    { // Verify that association exists
        ProjectAssociation projectAssociation = this.projectAssociationRepository
                .findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new NoSuchElementException("Association not found"));
        if(newPermission == AccessPermission.READ)
            this.subProjectRepository // Verify that at least one sub-project association exists
                    .findFirstByUsersId(userId) // in case of update to READ permission
                    .orElseThrow(() -> new RedundantAssociationException(
                            "Read association with project while no sub project exist is redundant"));
        else
        { // For other permission levels, verify that the user has a valid permission level with corresponding entity
            User user = findUser(userId);
            boolean hasPermission = hasValidPermission(user, newPermission);
            if(!hasPermission)
                throw new AuthorizationException("User is unauthorized to handle such association level");
        } // Update association
        projectAssociation.setPermission(newPermission);
        this.projectAssociationRepository.save(projectAssociation);
    }

    public Page<Project> getAllProjects(int pageIndex, int pageSize, String sortBy, boolean asc)
    { // Get user id and corresponding permission level with projects
        Pair<Long, AccessPermission> userIdAndPermission = findAccountPermission();
        Long userId = userIdAndPermission.getFirst();
        AccessPermission accessPermission = userIdAndPermission.getSecond();
        switch(accessPermission)
        {
            case READ: // Get only projects with read permission
                return getAllProjectsByUserIdAndPermission(userId, accessPermission, pageIndex, pageSize, sortBy, asc);
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

    public Page<Project> getAllProjectsByAccountId(
            Long accountId, int pageIndex, int pageSize, String sortBy, boolean asc)
    {
        // Get user id and corresponding permission level with projects
        Pair<Long, AccessPermission> userIdAndPermission = findAccountPermission();
        Long userId = userIdAndPermission.getFirst();
        AccessPermission accessPermission = userIdAndPermission.getSecond();
        switch(accessPermission)
        {
            case READ: // Get only projects with read permission
                return getAllProjectsByAccountIdAndUserIdAndPermission(accountId, userId, accessPermission, pageIndex, pageSize, sortBy, asc);
            case MANAGE:
            case WRITE: // Get all projects that are manage or write permission
                return getAllProjectsByAccountIdAndUserIdAndPermissionGreaterThanEqual(
                        accountId, userId, AccessPermission.MANAGE, pageIndex, pageSize, sortBy, asc);
            case ADMIN: // Get all projects in the system
                return findAllProjectsByAccountId(accountId, pageIndex, pageSize, sortBy, asc);
            default:
                return Page.empty();
        }
    }

    public Page<Project> getAllProjectsByUserIdAndPermission(
            Long userId, AccessPermission accessPermission, int pageIndex, int pageSize, String sortBy, boolean asc)
    {
        PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
        Page<Project> results = this.projectRepository.findAllByAssociationsUserIdAndAssociationsPermission(
                userId, accessPermission, pageRequest);
        return new PageImpl<>(results.getContent(), pageRequest, results.getTotalElements());
    }

    public Page<Project> getAllProjectsByUserIdAndPermissionGreaterThanEqual(
            Long userId, AccessPermission accessPermission, int pageIndex, int pageSize, String sortBy, boolean asc)
    {
        PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
        Page<Project> results = this.projectRepository.findAllByAssociationsUserIdAndAssociationsPermissionGreaterThanEqual(
                userId, accessPermission, pageRequest);
        return new PageImpl<>(results.getContent(), pageRequest, results.getTotalElements());
    }

    public Page<Project> getAllProjectsByAccountIdAndUserIdAndPermission(
            Long accountId, Long userId, AccessPermission accessPermission,
            int pageIndex, int pageSize, String sortBy, boolean asc)
    {
        PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
        Page<Project> results = this.projectRepository.findAllByAccountIdAndAssociationsUserIdAndAssociationsPermission(
                accountId, userId, accessPermission, pageRequest);
        return new PageImpl<>(results.getContent(), pageRequest, results.getTotalElements());
    }

    private Page<Project> getAllProjectsByAccountIdAndUserIdAndPermissionGreaterThanEqual(
            Long accountId, Long userId, AccessPermission accessPermission, int pageIndex, int pageSize, String sortBy, boolean asc)
    {
        PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
        Page<Project> results = this.projectRepository
                .findAllByAccountIdAndAssociationsUserIdAndAssociationsPermissionGreaterThanEqual(
                accountId, userId, accessPermission, pageRequest);
        return new PageImpl<>(results.getContent(), pageRequest, results.getTotalElements());
    }

    public List<ProjectSearchResult> getAllProjectResultsByAccountIdAndUserId(
            Long accountId, Long userId, int resultsCount)
    {
        Sort sort = Sort.by(AppDefaults.PROJECTS_DEFAULT_SORT).descending();
        PageRequest pageRequest = PageRequest.of(0, resultsCount, sort);
        return this.projectRepository.findAllResultsByAccountIdAndAssociationsUserId(accountId, userId, pageRequest);
    }

    private Project findProject(Long projectId)
    {
        return this.projectRepository
                .findById(projectId)
                .orElseThrow(() -> new NoSuchElementException("Project not found"));
    }

    private Page<Project> findAllProjects(int pageIndex, int pageSize, String sortBy, boolean asc)
    {
        PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
        Page<Project> results = this.projectRepository.findAll(pageRequest);
        return new PageImpl<>(results.getContent(), pageRequest, results.getNumberOfElements());
    }

    private Page<Project> findAllProjectsByAccountId(
            Long accountId, int pageIndex, int pageSize, String sortBy, boolean asc)
    {
        PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
        Page<Project> results = this.projectRepository.findAllByAccountId(accountId, pageRequest);
        return new PageImpl<>(results.getContent(), pageRequest, results.getNumberOfElements());
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

    public Pair<Long, AccessPermission> findAccountPermission()
    {
        User user = UserEvaluator.currentUser();
        AccessPermission accessPermission =  user.getPrivileges().stream()
                .filter(privilege -> privilege.getName().equals(AppConstants.PROJECT_PERMISSION))
                .map(Privilege::getLevel)
                .findFirst()
                .orElseThrow(() -> new AuthorizationException("User is unauthorized with accounts"));
        return Pair.of(user.getId(), accessPermission);
    }

    private AccountAssociation createAccountAssociation(User user, Account account)
    {
        AccountAssociation accountAssociation = new AccountAssociation(user, account, AccessPermission.READ);
        return this.accountAssociationRepository.save(accountAssociation);
    }

    private boolean hasValidPermission(User user, AccessPermission accessPermission)
    {
        Privilege privilege = new Privilege();
        privilege.setName(AppConstants.ACCOUNT_PERMISSION);
        privilege.setLevel(accessPermission);
        return user.hasPermissionGreaterThanEqual(privilege);
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
