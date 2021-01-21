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
import com.jsoft.magenta.projects.domain.SubProject;
import com.jsoft.magenta.security.UserEvaluator;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.users.UserRepository;
import com.jsoft.magenta.util.AppConstants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.mockStatic;

public class ProjectServiceTest
{
    @InjectMocks
    private ProjectService projectService;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProjectAssociationRepository projectAssociationRepository;

    @Mock
    private AccountAssociationRepository accountAssociationRepository;

    @Mock
    private SubProjectRepository subProjectRepository;

    private static MockedStatic<UserEvaluator> mockedStatic;

    @BeforeAll
    private static void initStaticMock()
    {
        mockedStatic = mockStatic(UserEvaluator.class);
    }

    @BeforeEach
    public void init()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Project creation tests")
    class ProjectCreationTests
    {
        @Test
        @DisplayName("Create project")
        public void createProject()
        {
            Project project = new Project();
            project.setName("project");

            SubProject subProject = new SubProject();
            subProject.setName("sp");
            subProject.setAvailable(true);

            SubProject returnedSP = new SubProject();
            returnedSP.setName("sp");
            returnedSP.setAvailable(true);
            returnedSP.setId(1L);
            Set<SubProject> subProjects = new HashSet<>();
            subProjects.add(subProject);
            project.setSubProjects(subProjects);

            Project returnedProject = new Project();
            returnedProject.setName("project");
            returnedProject.setId(1L);
            Set<SubProject> returnedSPs = new HashSet<>();
            returnedSPs.add(returnedSP);
            project.setSubProjects(returnedSPs);

            Mockito.when(projectRepository.save(project)).thenReturn(returnedProject);
            Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.of(new Account()));

            projectService.createProject(1L, project);

            Assertions.assertThat(project)
                    .extracting("createdAt")
                    .isNotNull();

            Mockito.verify(projectRepository).save(project);
            Mockito.verify(accountRepository).findById(1L);
        }

        @Test
        @DisplayName("Create project of account that does not exist - should throw exception")
        public void createProjectOfAccountThatDoesNotExist()
        {
            Project project = new Project();
            project.setName("project");

            SubProject subProject = new SubProject();
            subProject.setName("sp");
            subProject.setAvailable(true);

            SubProject returnedSP = new SubProject();
            returnedSP.setName("sp");
            returnedSP.setAvailable(true);
            returnedSP.setId(1L);
            Set<SubProject> subProjects = new HashSet<>();
            subProjects.add(subProject);
            project.setSubProjects(subProjects);

            Project returnedProject = new Project();
            returnedProject.setName("project");
            returnedProject.setId(1L);
            Set<SubProject> returnedSPs = new HashSet<>();
            returnedSPs.add(returnedSP);
            project.setSubProjects(returnedSPs);

            Mockito.when(projectRepository.save(project)).thenReturn(returnedProject);
            Mockito.when(accountRepository.findById(1L)).thenReturn(Optional.empty());

            Assertions.assertThatThrownBy(() -> projectService.createProject(1L, project))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Account not found");

            Mockito.verify(projectRepository, Mockito.never()).save(project);
            Mockito.verify(accountRepository).findById(1L);
        }
    }

    @Nested
    @DisplayName("Project update tests")
    class ProjectUpdateTests
    {
        @Test
        @DisplayName("Update project")
        public void updateProject()
        {
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            Account account = new Account();
            account.setId(1L);
            project.setAccount(account);

            Mockito.when(projectRepository.save(project)).thenReturn(project);
            Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            Mockito.when(projectRepository.findByAccountIdAndName(1L, project.getName()))
                    .thenReturn(Optional.empty());

            projectService.updateProject(project);

            Assertions.assertThat(project)
                    .extracting("name")
                    .isEqualTo("project");

            Mockito.verify(projectRepository).save(project);
            Mockito.verify(projectRepository).findById(1L);
            Mockito.verify(projectRepository).findByAccountIdAndName(1L, project.getName());
        }

        @Test
        @DisplayName("Update project with existing name - should throw exception")
        public void updateProjectWithExistingName()
        {
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            Account account = new Account();
            account.setId(1L);
            project.setAccount(account);

            Mockito.when(projectRepository.save(project)).thenReturn(project);
            Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            Mockito.when(projectRepository.findByAccountIdAndName(1L, project.getName()))
                    .thenReturn(Optional.of(project));

            Assertions.assertThatThrownBy(() -> projectService.updateProject(project))
                    .isInstanceOf(DuplicationException.class)
                    .hasMessage(String.format("Project with name %s already exist for account", project.getName()));

            Mockito.verify(projectRepository, Mockito.never()).save(project);
            Mockito.verify(projectRepository).findById(1L);
            Mockito.verify(projectRepository).findByAccountIdAndName(1L, project.getName());
        }

        @Test
        @DisplayName("Update project that does not exist")
        public void updateProjectThatDoesNotExist()
        {
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            Account account = new Account();
            account.setId(1L);
            project.setAccount(account);

            Mockito.when(projectRepository.save(project)).thenReturn(project);
            Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.empty());
            Mockito.when(projectRepository.findByAccountIdAndName(1L, project.getName()))
                    .thenReturn(Optional.of(project));

            Assertions.assertThatThrownBy(() -> projectService.updateProject(project))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Project not found");

            Mockito.verify(projectRepository).findById(1L);
            Mockito.verify(projectRepository, Mockito.never()).save(project);
            Mockito.verify(projectRepository, Mockito.never()).findByAccountIdAndName(1L, project.getName());
        }

        @Test
        @DisplayName("Update project name")
        public void updateProjectName()
        {
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            Account account = new Account();
            account.setId(1L);
            project.setAccount(account);
            String newName = "project";

            Mockito.when(projectRepository.save(project)).thenReturn(project);
            Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            Mockito.when(projectRepository.findByAccountIdAndName(1L, newName))
                    .thenReturn(Optional.empty());

            projectService.updateProject(project);

            Assertions.assertThat(project)
                    .extracting("name")
                    .isEqualTo("project");

            Mockito.verify(projectRepository).save(project);
            Mockito.verify(projectRepository).findById(1L);
            Mockito.verify(projectRepository).findByAccountIdAndName(1L, project.getName());
        }

        @Test
        @DisplayName("Update project name with existing name - should throw exception")
        public void updateProjectNameWithExistingName()
        {
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            Account account = new Account();
            account.setId(1L);
            project.setAccount(account);

            Mockito.when(projectRepository.save(project)).thenReturn(project);
            Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.empty());
            Mockito.when(projectRepository.findByAccountIdAndName(1L, project.getName()))
                    .thenReturn(Optional.of(project));

            Assertions.assertThatThrownBy(() -> projectService.updateProjectName(project.getId(), "project"))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Project not found");

            Mockito.verify(projectRepository).findById(1L);
            Mockito.verify(projectRepository, Mockito.never()).save(project);
            Mockito.verify(projectRepository, Mockito.never()).findByAccountIdAndName(1L, project.getName());
        }

        @Test
        @DisplayName("Create association between user and project")
        public void createAssociation()
        {
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            Account account = new Account();
            account.setId(1L);
            project.setAccount(account);
            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.ACCOUNT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            user.setPrivileges(Set.of(privilege));

            ProjectAssociation projectAssociation = new ProjectAssociation(user, project, AccessPermission.ADMIN);
            AccountAssociation accountAssociation = new AccountAssociation(user, account, AccessPermission.ADMIN);

            Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            Mockito.when(accountAssociationRepository.findByUserIdAndAccountId(user.getId(), account.getId()))
                    .thenReturn(Optional.of(accountAssociation));
            Mockito.when(accountAssociationRepository.save(accountAssociation))
                    .thenReturn(accountAssociation);
            Mockito.when(projectAssociationRepository.findByUserIdAndProjectId(user.getId(), project.getId()))
                    .thenReturn(Optional.empty());
            Mockito.when(projectAssociationRepository.save(projectAssociation))
                    .thenReturn(projectAssociation);

            projectService.createAssociation(user.getId(), project.getId(), AccessPermission.ADMIN);

            Mockito.verify(projectRepository).findById(1L);
            Mockito.verify(userRepository).findById(1L);
            Mockito.verify(accountAssociationRepository).findByUserIdAndAccountId(1L, 1L);
            Mockito.verify(accountAssociationRepository, Mockito.never()).save(accountAssociation);
            Mockito.verify(projectAssociationRepository).findByUserIdAndProjectId(user.getId(), project.getId());
            Mockito.verify(projectAssociationRepository).save(projectAssociation);
        }

        @Test
        @DisplayName("Create association between user and project with READ permission - should throw exception")
        public void createAssociationWithReadPermission()
        {
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            Account account = new Account();
            account.setId(1L);
            project.setAccount(account);
            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.ACCOUNT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            user.setPrivileges(Set.of(privilege));

            ProjectAssociation projectAssociation = new ProjectAssociation(user, project, AccessPermission.ADMIN);
            AccountAssociation accountAssociation = new AccountAssociation(user, account, AccessPermission.ADMIN);

            Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            Mockito.when(accountAssociationRepository.findByUserIdAndAccountId(1L, 1L))
                    .thenReturn(Optional.of(accountAssociation));
            Mockito.when(accountAssociationRepository.save(accountAssociation))
                    .thenReturn(accountAssociation);
            Mockito.when(projectAssociationRepository.findByUserIdAndProjectId(user.getId(), project.getId()))
                    .thenReturn(Optional.empty());
            Mockito.when(projectAssociationRepository.save(projectAssociation))
                    .thenReturn(projectAssociation);

            Assertions.assertThatThrownBy(
                    () -> projectService.createAssociation(user.getId(), project.getId(), AccessPermission.READ))
                    .isInstanceOf(RedundantAssociationException.class)
                    .hasMessage("Read association with project while no sub project exist is redundant");

            Mockito.verify(projectRepository).findById(1L);
            Mockito.verify(userRepository).findById(1L);
            Mockito.verify(accountAssociationRepository, Mockito.never()).findByUserIdAndAccountId(1L, 1L);
            Mockito.verify(accountAssociationRepository, Mockito.never()).save(accountAssociation);
            Mockito.verify(projectAssociationRepository).findByUserIdAndProjectId(user.getId(), project.getId());
            Mockito.verify(projectAssociationRepository, Mockito.never()).save(projectAssociation);
        }

        @Test
        @DisplayName("Update association between user and project to MANAGE permission")
        public void updateAssociationToManagePermission()
        {
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            Account account = new Account();
            account.setId(1L);
            project.setAccount(account);
            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.ACCOUNT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            user.setPrivileges(Set.of(privilege));

            ProjectAssociation projectAssociation = new ProjectAssociation(user, project, AccessPermission.ADMIN);

            Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            Mockito.when(projectAssociationRepository.findByUserIdAndProjectId(user.getId(), project.getId()))
                    .thenReturn(Optional.of(projectAssociation));
            Mockito.when(projectAssociationRepository.save(projectAssociation))
                    .thenReturn(projectAssociation);

            projectService.updateAssociation(user.getId(), project.getId(), AccessPermission.MANAGE);

            Mockito.verify(userRepository).findById(1L);
            Mockito.verify(projectAssociationRepository).findByUserIdAndProjectId(user.getId(), project.getId());
            Mockito.verify(projectAssociationRepository).save(projectAssociation);
        }

        @Test
        @DisplayName("Update association to READ permission without sub projects - should throw exception")
        public void updateAssociationToReadPermissionWithoutSubProjects()
        {
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            Account account = new Account();
            account.setId(1L);
            project.setAccount(account);
            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.ACCOUNT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            user.setPrivileges(Set.of(privilege));

            ProjectAssociation projectAssociation = new ProjectAssociation(user, project, AccessPermission.ADMIN);

            Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            Mockito.when(projectAssociationRepository.findByUserIdAndProjectId(user.getId(), project.getId()))
                    .thenReturn(Optional.of(projectAssociation));
            Mockito.when(projectAssociationRepository.save(projectAssociation))
                    .thenReturn(projectAssociation);
            Mockito.when(subProjectRepository.findFirstByUsersId(user.getId()))
                    .thenReturn(Optional.empty());

            Assertions.assertThatThrownBy(
                    () -> projectService.updateAssociation(user.getId(), project.getId(), AccessPermission.READ))
            .isInstanceOf(RedundantAssociationException.class)
            .hasMessage("Read association with project while no sub project exist is redundant");

            Mockito.verify(userRepository, Mockito.never()).findById(1L);
            Mockito.verify(subProjectRepository).findFirstByUsersId(user.getId());
            Mockito.verify(projectAssociationRepository).findByUserIdAndProjectId(user.getId(), project.getId());
            Mockito.verify(projectAssociationRepository, Mockito.never()).save(projectAssociation);
        }

        @Test
        @DisplayName("Update association to MANAGE permission without valid permission - should throw exception")
        public void updateAssociationToReadPermissionWithoutValidPermission()
        {
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            Account account = new Account();
            account.setId(1L);
            project.setAccount(account);
            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.ACCOUNT_PERMISSION);
            privilege.setLevel(AccessPermission.READ);
            user.setPrivileges(Set.of(privilege));

            ProjectAssociation projectAssociation = new ProjectAssociation(user, project, AccessPermission.MANAGE);

            Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            Mockito.when(projectAssociationRepository.findByUserIdAndProjectId(user.getId(), project.getId()))
                    .thenReturn(Optional.of(projectAssociation));
            Mockito.when(projectAssociationRepository.save(projectAssociation))
                    .thenReturn(projectAssociation);
            Mockito.when(subProjectRepository.findFirstByUsersId(user.getId()))
                    .thenReturn(Optional.empty());

            Assertions.assertThatThrownBy(
                    () -> projectService.updateAssociation(user.getId(), project.getId(), AccessPermission.MANAGE))
                    .isInstanceOf(AuthorizationException.class)
                    .hasMessage("User is unauthorized to handle such association level");

            Mockito.verify(userRepository).findById(1L);
            Mockito.verify(subProjectRepository, Mockito.never()).findFirstByUsersId(user.getId());
            Mockito.verify(projectAssociationRepository).findByUserIdAndProjectId(user.getId(), project.getId());
            Mockito.verify(projectAssociationRepository, Mockito.never()).save(projectAssociation);
        }
    }

    @Nested
    @DisplayName("Project get tests")
    class ProjectGetTests
    {
        @Test
        @DisplayName("Get all projects as reader")
        public void getAllProjectsAsReader()
        {
            Sort sort =  Sort.by("name").ascending();
            PageRequest pageRequest = PageRequest.of(0, 5, sort);
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.READ);
            user.setPrivileges(Set.of(privilege));
            ProjectAssociation projectAssociation = new ProjectAssociation(user, project, AccessPermission.READ);
            project.setAssociations(Set.of(projectAssociation));
            List<Project> projectList = List.of(project);
            Page<Project> projects = new PageImpl<>(projectList, pageRequest, 1);

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);
            Mockito.when(projectRepository.findAllByAssociationsUserIdAndAssociationsPermission(
                    user.getId(), AccessPermission.READ, pageRequest))
                    .thenReturn(projects);

            Page<Project> results = projectService.getAllProjects(0, 5, "name", true);

            Assertions.assertThat(results)
                    .isNotNull()
                    .isNotEmpty()
                    .hasSameSizeAs(projectList)
                    .containsAll(projectList);

            Mockito.verify(projectRepository).findAllByAssociationsUserIdAndAssociationsPermission(
                    user.getId(), AccessPermission.READ, pageRequest);
        }

        @Test
        @DisplayName("Get all projects as manager")
        public void getAllProjectsAsManager()
        {
            Sort sort =  Sort.by("name").ascending();
            PageRequest pageRequest = PageRequest.of(0, 5, sort);
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.MANAGE);
            user.setPrivileges(Set.of(privilege));
            ProjectAssociation projectAssociation = new ProjectAssociation(user, project, AccessPermission.MANAGE);
            project.setAssociations(Set.of(projectAssociation));
            List<Project> projectList = List.of(project);
            Page<Project> projects = new PageImpl<>(projectList, pageRequest, 1);

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);
            Mockito.when(projectRepository.findAllByAssociationsUserIdAndAssociationsPermissionGreaterThanEqual(
                    user.getId(), AccessPermission.MANAGE, pageRequest))
                    .thenReturn(projects);

            Page<Project> results = projectService.getAllProjects(0, 5, "name", true);

            Assertions.assertThat(results)
                    .isNotNull()
                    .isNotEmpty()
                    .hasSameSizeAs(projectList)
                    .containsAll(projectList);

            Mockito.verify(projectRepository).findAllByAssociationsUserIdAndAssociationsPermissionGreaterThanEqual(
                    user.getId(), AccessPermission.MANAGE, pageRequest);
        }

        @Test
        @DisplayName("Get all projects as admin")
        public void getAllProjectsAsAdmin()
        {
            Sort sort =  Sort.by("name").ascending();
            PageRequest pageRequest = PageRequest.of(0, 5, sort);
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            user.setPrivileges(Set.of(privilege));
            ProjectAssociation projectAssociation = new ProjectAssociation(user, project, AccessPermission.READ);
            project.setAssociations(Set.of(projectAssociation));
            List<Project> projectList = List.of(project);
            Page<Project> projects = new PageImpl<>(projectList, pageRequest, 1);

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);
            Mockito.when(projectRepository.findAll(pageRequest))
                    .thenReturn(projects);

            Page<Project> results = projectService.getAllProjects(0, 5, "name", true);

            Assertions.assertThat(results)
                    .isNotNull()
                    .isNotEmpty()
                    .hasSameSizeAs(projectList)
                    .containsAll(projectList);

            Mockito.verify(projectRepository).findAll(pageRequest);
        }

        @Test
        @DisplayName("Get all projects by account id as reader")
        public void getAllProjectsByAccountIdAsReader()
        {
            Sort sort =  Sort.by("name").ascending();
            PageRequest pageRequest = PageRequest.of(0, 5, sort);
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.READ);
            user.setPrivileges(Set.of(privilege));
            Account account = new Account();
            account.setId(1L);
            account.setName("account");
            project.setAccount(account);
            ProjectAssociation projectAssociation = new ProjectAssociation(user, project, AccessPermission.READ);
            project.setAssociations(Set.of(projectAssociation));
            List<Project> projectList = List.of(project);
            Page<Project> projects = new PageImpl<>(projectList, pageRequest, 1);

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);
            Mockito.when(projectRepository.findAllByAccountIdAndAssociationsUserIdAndAssociationsPermission(
                    account.getId(), user.getId(), AccessPermission.READ, pageRequest))
                    .thenReturn(projects);

            Page<Project> results = projectService.getAllProjectsByAccountId(account.getId(), 0, 5, "name", true);

            Assertions.assertThat(results)
                    .isNotNull()
                    .isNotEmpty()
                    .hasSameSizeAs(projectList)
                    .containsAll(projectList);

            Mockito.verify(projectRepository).findAllByAccountIdAndAssociationsUserIdAndAssociationsPermission(
                    account.getId(), user.getId(), AccessPermission.READ, pageRequest);
        }

        @Test
        @DisplayName("Get all projects by account id as manager")
        public void getAllProjectsByAccountIdAsManager()
        {
            Sort sort =  Sort.by("name").ascending();
            PageRequest pageRequest = PageRequest.of(0, 5, sort);
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.MANAGE);
            user.setPrivileges(Set.of(privilege));
            Account account = new Account();
            account.setId(1L);
            account.setName("account");
            project.setAccount(account);
            ProjectAssociation projectAssociation = new ProjectAssociation(user, project, AccessPermission.MANAGE);
            project.setAssociations(Set.of(projectAssociation));
            List<Project> projectList = List.of(project);
            Page<Project> projects = new PageImpl<>(projectList, pageRequest, 1);

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);
            Mockito.when(projectRepository.findAllByAccountIdAndAssociationsUserIdAndAssociationsPermissionGreaterThanEqual(
                    account.getId(), user.getId(), AccessPermission.MANAGE, pageRequest))
                    .thenReturn(projects);

            Page<Project> results = projectService.getAllProjectsByAccountId(account.getId(), 0, 5, "name", true);

            Assertions.assertThat(results)
                    .isNotNull()
                    .isNotEmpty()
                    .hasSameSizeAs(projectList)
                    .containsAll(projectList);

            Mockito.verify(projectRepository).findAllByAccountIdAndAssociationsUserIdAndAssociationsPermissionGreaterThanEqual(
                    account.getId(), user.getId(), AccessPermission.MANAGE, pageRequest);
        }

        @Test
        @DisplayName("Get all projects by account id as admin")
        public void getAllProjectsByAccountIdAsAdmin()
        {
            Sort sort =  Sort.by("name").ascending();
            PageRequest pageRequest = PageRequest.of(0, 5, sort);
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            user.setPrivileges(Set.of(privilege));
            Account account = new Account();
            account.setId(1L);
            account.setName("account");
            project.setAccount(account);
            ProjectAssociation projectAssociation = new ProjectAssociation(user, project, AccessPermission.READ);
            project.setAssociations(Set.of(projectAssociation));
            List<Project> projectList = List.of(project);
            Page<Project> projects = new PageImpl<>(projectList, pageRequest, 1);

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);
            Mockito.when(projectRepository.findAllByAccountId(
                    account.getId(), pageRequest))
                    .thenReturn(projects);

            Page<Project> results = projectService.getAllProjectsByAccountId(account.getId(), 0, 5, "name", true);

            Assertions.assertThat(results)
                    .isNotNull()
                    .isNotEmpty()
                    .hasSameSizeAs(projectList)
                    .containsAll(projectList);

            Mockito.verify(projectRepository).findAllByAccountId(account.getId(), pageRequest);
        }

        @Test
        @DisplayName("Get all projects by user and association permission")
        public void getAllProjectsByUserAssociationPermission()
        {
            Sort sort =  Sort.by("name").ascending();
            PageRequest pageRequest = PageRequest.of(0, 5, sort);
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            User user = new User();
            user.setId(1L);
            ProjectAssociation projectAssociation = new ProjectAssociation(user, project, AccessPermission.MANAGE);
            project.setAssociations(Set.of(projectAssociation));
            List<Project> projectList = List.of(project);
            Page<Project> projects = new PageImpl<>(projectList, pageRequest, 1);

            Mockito.when(projectRepository.findAllByAssociationsUserIdAndAssociationsPermission(
                    user.getId(), AccessPermission.MANAGE, pageRequest))
                    .thenReturn(projects);

            Page<Project> results = projectService.getAllProjectsByUserIdAndPermission(
                    user.getId(), AccessPermission.MANAGE, 0, 5, "name", true);

            Assertions.assertThat(results)
                    .isNotNull()
                    .isNotEmpty()
                    .hasSameSizeAs(projectList)
                    .containsAll(projectList);

            Mockito.verify(projectRepository).findAllByAssociationsUserIdAndAssociationsPermission(
                    user.getId(), AccessPermission.MANAGE, pageRequest);
        }

        @Test
        @DisplayName("Get all projects by user and account and association permission")
        public void getAllProjectsByUserAccountAssociationPermission()
        {
            Sort sort =  Sort.by("name").ascending();
            PageRequest pageRequest = PageRequest.of(0, 5, sort);
            Project project = new Project();
            project.setId(1L);
            project.setName("project");
            User user = new User();
            user.setId(1L);
            Account account = new Account();
            account.setId(1L);
            account.setName("name");
            project.setAccount(account);
            ProjectAssociation projectAssociation = new ProjectAssociation(user, project, AccessPermission.MANAGE);
            project.setAssociations(Set.of(projectAssociation));
            List<Project> projectList = List.of(project);
            Page<Project> projects = new PageImpl<>(projectList, pageRequest, 1);

            Mockito.when(projectRepository.findAllByAccountIdAndAssociationsUserIdAndAssociationsPermission(
                    account.getId(), user.getId(), AccessPermission.MANAGE, pageRequest))
                    .thenReturn(projects);

            Page<Project> results = projectService.getAllProjectsByAccountIdAndUserIdAndPermission(
                    account.getId(), user.getId(), AccessPermission.MANAGE, 0, 5, "name", true);

            Assertions.assertThat(results)
                    .isNotNull()
                    .isNotEmpty()
                    .hasSameSizeAs(projectList)
                    .containsAll(projectList);

            Mockito.verify(projectRepository).findAllByAccountIdAndAssociationsUserIdAndAssociationsPermission(
                account.getId(), user.getId(), AccessPermission.MANAGE, pageRequest);
        }

        @Test
        @DisplayName("Get all project results by user and account")
        public void getAllProjectResultsByUserAndAccount()
        {
            Sort sort =  Sort.by("name").descending();
            PageRequest pageRequest = PageRequest.of(0, 5, sort);
            ProjectSearchResult project = new ProjectSearchResult()
            {
                @Override
                public Long getId() {
                    return 1L;
                }

                @Override
                public String getName() {
                    return "project";
                }
            };

            User user = new User();
            user.setId(1L);
            Account account = new Account();
            account.setId(1L);
            account.setName("account");
            List<ProjectSearchResult> projectList = List.of(project);

            Mockito.when(projectRepository.findAllResultsByAccountIdAndAssociationsUserId(
                    account.getId(), user.getId(), pageRequest))
                    .thenReturn(projectList);

            List<ProjectSearchResult> results = projectService.getAllProjectResultsByAccountIdAndUserId(
                    account.getId(), user.getId(), 5);

            Assertions.assertThat(results)
                    .isNotNull()
                    .isNotEmpty()
                    .hasSameSizeAs(projectList)
                    .containsAll(projectList);

            Mockito.verify(projectRepository).findAllResultsByAccountIdAndAssociationsUserId(
                    account.getId(), user.getId(), pageRequest);
        }
    }
}
