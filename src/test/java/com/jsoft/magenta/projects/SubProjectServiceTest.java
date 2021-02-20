package com.jsoft.magenta.projects;

import com.jsoft.magenta.accounts.AccountAssociationRepository;
import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.accounts.domain.AccountAssociation;
import com.jsoft.magenta.exceptions.AuthorizationException;
import com.jsoft.magenta.exceptions.DuplicationException;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.projects.domain.ProjectAssociation;
import com.jsoft.magenta.subprojects.SubProject;
import com.jsoft.magenta.subprojects.SubProjectSearchResult;
import com.jsoft.magenta.security.UserEvaluator;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.subprojects.SubProjectRepository;
import com.jsoft.magenta.subprojects.SubProjectService;
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

public class SubProjectServiceTest
{
    @InjectMocks
    private SubProjectService subProjectService;

    @Mock
    private SubProjectRepository subProjectRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private ProjectAssociationRepository projectAssociationRepository;

    @Mock
    private AccountAssociationRepository accountAssociationRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void init()
    {
        MockitoAnnotations.openMocks(this);
    }

    private static MockedStatic<UserEvaluator> mockedStatic;

    @BeforeAll
    private static void initStaticMock()
    {
        mockedStatic = mockStatic(UserEvaluator.class);
    }

    @Nested
    @DisplayName("Sub-projects creation tests")
    class SubProjectCreationTests
    {
        @Test
        @DisplayName("Create sub-project")
        public void createSubProject()
        {
            SubProject subProject = new SubProject();
            subProject.setName("sp");
            subProject.setAmountOfHours(15D);
            subProject.setAvailable(true);
            Project project = new Project();
            project.setId(1L);

            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            user.setPrivileges(Set.of(privilege));

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);

            Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
            Mockito.when(subProjectRepository.save(subProject)).thenReturn(subProject);

            subProjectService.createSubProject(1L, subProject);

            Assertions.assertThat(subProject)
                    .extracting("project")
                    .extracting("id")
                    .isEqualTo(project.getId());
            Assertions.assertThat(subProject)
                    .extracting("available")
                    .isEqualTo(true);

            Mockito.verify(projectRepository).findById(1L);
            Mockito.verify(subProjectRepository).save(subProject);
        }

        @Test
        @DisplayName("Create sub-project for project that does not exist - should throw exception")
        public void createSubProjectForNonExistingProject()
        {
            SubProject subProject = new SubProject();
            subProject.setName("sp");
            subProject.setAmountOfHours(15D);
            subProject.setAvailable(true);

            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            user.setPrivileges(Set.of(privilege));

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);
            Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.empty());
            Mockito.when(subProjectRepository.save(subProject)).thenReturn(subProject);

            Assertions.assertThatThrownBy(() -> subProjectService.createSubProject(1L, subProject))
                    .isInstanceOf(NoSuchElementException.class)
                    .hasMessage("Project not found");

            Mockito.verify(projectRepository).findById(1L);
            Mockito.verify(subProjectRepository, Mockito.never()).save(subProject);
        }

        @Test
        @DisplayName("Create sub-project without permission (as a manager with manage association)" +
                " - should check association and throw exception")
        public void createSubProjectAsManager()
        {
            SubProject subProject = new SubProject();
            subProject.setName("sp");
            subProject.setAmountOfHours(15D);
            subProject.setAvailable(true);
            Project project = new Project();
            project.setId(1L);

            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.MANAGE);
            user.setPrivileges(Set.of(privilege));

            ProjectAssociation projectAssociation = new ProjectAssociation();
            projectAssociation.setProject(project);
            projectAssociation.setUser(user);
            projectAssociation.setPermission(AccessPermission.MANAGE);

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);
            Mockito.when(projectAssociationRepository.findByUserIdAndProjectId(user.getId(), project.getId()))
                    .thenReturn(Optional.of(projectAssociation));
            Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.empty());
            Mockito.when(subProjectRepository.save(subProject)).thenReturn(subProject);

            Assertions.assertThatThrownBy(() -> subProjectService.createSubProject(1L, subProject))
                    .isInstanceOf(AuthorizationException.class)
                    .hasMessage("User is not authorized to update specified project");

            Mockito.verify(projectRepository, Mockito.never()).findById(1L);
            Mockito.verify(subProjectRepository, Mockito.never()).save(subProject);
        }

        @Test
        @DisplayName("Create association")
        public void createAssociation()
        {
            SubProject subProject = new SubProject();
            subProject.setId(1L);
            User user = new User();
            user.setId(1L);
            Project project = new Project();
            project.setId(1L);
            subProject.setProject(project);
            Account account = new Account();
            account.setId(1L);
            project.setAccount(account);

            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            user.setPrivileges(Set.of(privilege));

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);

            Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            Mockito.when(subProjectRepository.findById(subProject.getId())).thenReturn(Optional.of(subProject));
            Mockito.when(subProjectRepository.existsByUsersId(user.getId())).thenReturn(false);

            subProjectService.createAssociation(user.getId(), subProject.getId());

            Assertions.assertThat(subProject.getUsers())
                    .contains(user);
        }

        @Test
        @DisplayName("Create association as manager - should check association and throw exception")
        public void createAssociationAsManager()
        {
            SubProject subProject = new SubProject();
            subProject.setId(1L);
            subProject.setName("sp");
            subProject.setAmountOfHours(15D);
            subProject.setAvailable(true);
            Project project = new Project();
            project.setId(1L);
            subProject.setProject(project);

            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.MANAGE);
            user.setPrivileges(Set.of(privilege));

            ProjectAssociation projectAssociation = new ProjectAssociation();
            projectAssociation.setProject(project);
            projectAssociation.setUser(user);
            projectAssociation.setPermission(AccessPermission.MANAGE);

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);
            Mockito.when(subProjectRepository.findById(subProject.getId())).thenReturn(Optional.of(subProject));
            Mockito.when(projectAssociationRepository.findByUserIdAndProjectId(user.getId(), project.getId()))
                    .thenReturn(Optional.of(projectAssociation));
            Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.empty());
            Mockito.when(subProjectRepository.save(subProject)).thenReturn(subProject);

            Assertions.assertThatThrownBy(() -> subProjectService.createAssociation(project.getId(), user.getId()))
                    .isInstanceOf(AuthorizationException.class)
                    .hasMessage("User is not authorized to update specified project");

            Mockito.verify(projectRepository, Mockito.never()).findById(1L);
            Mockito.verify(subProjectRepository, Mockito.never()).save(subProject);
        }
    }

    @Nested
    @DisplayName("Sub-project update tests")
    class SubProjectUpdateTests
    {
        @Test
        @DisplayName("Update subProject")
        public void updateSubProject()
        {
            SubProject subProject = new SubProject();
            subProject.setId(1L);
            subProject.setName("sp");
            subProject.setAmountOfHours(15D);
            subProject.setAvailable(true);
            Project project = new Project();
            project.setId(1L);
            subProject.setProject(project);

            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            user.setPrivileges(Set.of(privilege));

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);

            Mockito.when(subProjectRepository.findById(subProject.getId())).thenReturn(Optional.of(subProject));
            Mockito.when(subProjectRepository.save(subProject)).thenReturn(subProject);

            subProjectService.updateSubProject(subProject);

            Assertions.assertThat(subProject)
                    .extracting("project")
                    .extracting("id")
                    .isEqualTo(project.getId());
            Assertions.assertThat(subProject)
                    .extracting("available")
                    .isEqualTo(true);

            Mockito.verify(subProjectRepository).findById(subProject.getId());
            Mockito.verify(subProjectRepository).save(subProject);
        }

        @Test
        @DisplayName("Update subProject with existing name - should throw exception")
        public void updateSubProjectWithExistingName()
        {
            SubProject subProject = new SubProject();
            subProject.setId(1L);
            subProject.setName("sp");
            subProject.setAmountOfHours(15D);
            subProject.setAvailable(true);
            Project project = new Project();
            project.setId(1L);
            subProject.setProject(project);

            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            user.setPrivileges(Set.of(privilege));

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);

            Mockito.when(subProjectRepository.findByProjectIdAndName(project.getId(), subProject.getName()))
                    .thenReturn(Optional.of(subProject));
            Mockito.when(subProjectRepository.findById(subProject.getId())).thenReturn(Optional.of(subProject));
            Mockito.when(subProjectRepository.save(subProject)).thenReturn(subProject);

            Assertions.assertThatThrownBy(() -> subProjectService.updateSubProject(subProject))
                    .isInstanceOf(DuplicationException.class)
                    .hasMessage(
                            String.format("Sub-project with name %s already exist for project", subProject.getName())
                    );

            Mockito.verify(subProjectRepository).findByProjectIdAndName(project.getId(), subProject.getName());
            Mockito.verify(subProjectRepository).findById(subProject.getId());
            Mockito.verify(subProjectRepository, Mockito.never()).save(subProject);
        }

        @Test
        @DisplayName("Update subProject as project manager - should check association and throw exception")
        public void updateSubProjectAsProjectManager()
        {
            SubProject subProject = new SubProject();
            subProject.setId(1L);
            subProject.setName("sp");
            subProject.setAmountOfHours(15D);
            subProject.setAvailable(true);
            Project project = new Project();
            project.setId(1L);
            subProject.setProject(project);

            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.MANAGE);
            user.setPrivileges(Set.of(privilege));

            ProjectAssociation projectAssociation = new ProjectAssociation();
            projectAssociation.setProject(project);
            projectAssociation.setUser(user);

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);

            Mockito.when(projectAssociationRepository.findByUserIdAndProjectId(user.getId(), project.getId()))
                    .thenReturn(Optional.of(projectAssociation));
            Mockito.when(subProjectRepository.findById(subProject.getId())).thenReturn(Optional.of(subProject));
            Mockito.when(subProjectRepository.save(subProject)).thenReturn(subProject);

            Assertions.assertThatThrownBy(() -> subProjectService.updateSubProject(subProject))
                    .isInstanceOf(AuthorizationException.class)
                    .hasMessage(
                            "User is not authorized to update specified project"
                    );

            Mockito.verify(subProjectRepository).findById(subProject.getId());
            Mockito.verify(subProjectRepository, Mockito.never()).save(subProject);
        }

        @Test
        @DisplayName("Update sub-project name as admin")
        public void updateSubProjectName()
        {
            SubProject subProject = new SubProject();
            subProject.setId(1L);
            subProject.setName("sp");
            subProject.setAmountOfHours(15D);
            subProject.setAvailable(true);
            Project project = new Project();
            project.setId(1L);
            subProject.setProject(project);

            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            user.setPrivileges(Set.of(privilege));

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);

            Mockito.when(subProjectRepository.findById(subProject.getId())).thenReturn(Optional.of(subProject));
            Mockito.when(subProjectRepository.save(subProject)).thenReturn(subProject);

            subProjectService.updateSubProjectName(subProject.getId(), "new name");

            Assertions.assertThat(subProject)
                    .extracting("name")
                    .isEqualTo(subProject.getName());
            Assertions.assertThat(subProject)
                    .extracting("available")
                    .isEqualTo(true);

            Mockito.verify(subProjectRepository).findById(subProject.getId());
            Mockito.verify(subProjectRepository).save(subProject);
        }

        @Test
        @DisplayName("Update sub-project amount of hours as admin")
        public void updateSubProjectAmountOfHours()
        {
            SubProject subProject = new SubProject();
            subProject.setId(1L);
            subProject.setName("sp");
            subProject.setAmountOfHours(15D);
            subProject.setAvailable(true);
            Project project = new Project();
            project.setId(1L);
            subProject.setProject(project);

            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            user.setPrivileges(Set.of(privilege));

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);

            Mockito.when(subProjectRepository.findById(subProject.getId())).thenReturn(Optional.of(subProject));
            Mockito.when(subProjectRepository.save(subProject)).thenReturn(subProject);

            subProjectService.updateSubProjectHours(subProject.getId(), 20D);

            Assertions.assertThat(subProject)
                    .extracting("amountOfHours")
                    .isEqualTo(subProject.getAmountOfHours());
            Assertions.assertThat(subProject)
                    .extracting("available")
                    .isEqualTo(true);

            Mockito.verify(subProjectRepository).findById(subProject.getId());
            Mockito.verify(subProjectRepository).save(subProject);
        }

        @Test
        @DisplayName("Increase sub-project amount of hours as admin")
        public void increaseSubProjectAmountOfHours()
        {
            SubProject subProject = new SubProject();
            subProject.setId(1L);
            subProject.setName("sp");
            subProject.setAmountOfHours(15D);
            subProject.setAvailable(true);
            Project project = new Project();
            project.setId(1L);
            subProject.setProject(project);

            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            user.setPrivileges(Set.of(privilege));

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);

            Mockito.when(subProjectRepository.findById(subProject.getId())).thenReturn(Optional.of(subProject));
            Mockito.when(subProjectRepository.save(subProject)).thenReturn(subProject);

            subProjectService.increaseSubProjectHours(subProject.getId(), 20D);

            Assertions.assertThat(subProject)
                    .extracting("amountOfHours")
                    .isEqualTo(subProject.getAmountOfHours());
            Assertions.assertThat(subProject)
                    .extracting("available")
                    .isEqualTo(true);

            Mockito.verify(subProjectRepository).findById(subProject.getId());
            Mockito.verify(subProjectRepository).save(subProject);
        }

        @Test
        @DisplayName("Decrease sub-project amount of hours as admin")
        public void decreaseSubProjectAmountOfHours()
        {
            SubProject subProject = new SubProject();
            subProject.setId(1L);
            subProject.setName("sp");
            subProject.setAmountOfHours(15D);
            subProject.setAvailable(true);
            Project project = new Project();
            project.setId(1L);
            subProject.setProject(project);

            User user = new User();
            user.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            user.setPrivileges(Set.of(privilege));

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);

            Mockito.when(subProjectRepository.findById(subProject.getId())).thenReturn(Optional.of(subProject));
            Mockito.when(subProjectRepository.save(subProject)).thenReturn(subProject);

            subProjectService.decreaseSubProjectHours(subProject.getId(), 20D);

            Assertions.assertThat(subProject)
                    .extracting("amountOfHours")
                    .isEqualTo(subProject.getAmountOfHours());
            Assertions.assertThat(subProject)
                    .extracting("available")
                    .isEqualTo(true);

            Mockito.verify(subProjectRepository).findById(subProject.getId());
            Mockito.verify(subProjectRepository).save(subProject);
        }
    }

    @Nested
    @DisplayName("Sub-project delete tests")
    class SubProjectDeleteTests
    {
        @Test
        @DisplayName("Remove association")
        public void removeAssociation()
        {
            User supervisor = new User();
            supervisor.setId(1L);
            User supervised = new User();
            supervised.setId(1L);
            SubProject subProject = new SubProject();
            subProject.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            supervisor.setPrivileges(Set.of(privilege));
            Set<SubProject> subProjects = new HashSet<>();
            subProjects.add(subProject);
            subProject.setUsers(Set.of(supervised));
            supervised.setSubProjects(subProjects);
            supervised.setPrivileges(Set.of());
            Project project = new Project();
            project.setId(1L);
            Account account = new Account();
            account.setId(1L);
            subProject.setProject(project);
            project.setAccount(account);

            ProjectAssociation projectAssociation = new ProjectAssociation();
            projectAssociation.setProject(project);
            projectAssociation.setUser(supervised);
            projectAssociation.setPermission(AccessPermission.READ);

            AccountAssociation accountAssociation = new AccountAssociation();
            accountAssociation.setAccount(account);
            accountAssociation.setUser(supervised);
            accountAssociation.setPermission(AccessPermission.READ);

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(supervisor);
            Mockito.when(subProjectRepository.findByIdAndUsersId(subProject.getId(), supervised.getId()))
                    .thenReturn(Optional.of(subProject));
            Mockito.when(userRepository.findById(supervised.getId())).thenReturn(Optional.of(supervised));
            Mockito.when(projectAssociationRepository.findByUserIdAndProjectId(supervised.getId(), project.getId()))
                    .thenReturn(Optional.of(projectAssociation));
            Mockito.when(accountAssociationRepository.findByUserIdAndAccountId(supervised.getId(), account.getId()))
                    .thenReturn(Optional.of(accountAssociation));

            subProjectService.removeAssociation(supervised.getId(), subProject.getId());

            Mockito.verify(subProjectRepository).findByIdAndUsersId(subProject.getId(), supervised.getId());
            Mockito.verify(userRepository).findById(supervised.getId());
        }

        @Test
        @DisplayName("Remove all association")
        public void removeAllAssociation()
        {
            User supervisor = new User();
            supervisor.setId(1L);
            User supervised = new User();
            supervised.setId(1L);
            SubProject subProject = new SubProject();
            subProject.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            supervisor.setPrivileges(Set.of(privilege));
            Set<SubProject> subProjects = new HashSet<>();
            subProjects.add(subProject);
            subProject.setUsers(Set.of(supervised));
            supervised.setSubProjects(subProjects);
            supervised.setPrivileges(Set.of());
            Project project = new Project();
            project.setId(1L);
            Account account = new Account();
            account.setId(1L);
            subProject.setProject(project);
            project.setAccount(account);

            ProjectAssociation projectAssociation = new ProjectAssociation();
            projectAssociation.setProject(project);
            projectAssociation.setUser(supervised);
            projectAssociation.setPermission(AccessPermission.READ);

            AccountAssociation accountAssociation = new AccountAssociation();
            accountAssociation.setAccount(account);
            accountAssociation.setUser(supervised);
            accountAssociation.setPermission(AccessPermission.READ);

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(supervisor);
            Mockito.when(subProjectRepository.findById(subProject.getId()))
                    .thenReturn(Optional.of(subProject));
            Mockito.when(userRepository.findById(supervised.getId())).thenReturn(Optional.of(supervised));
            Mockito.when(projectAssociationRepository.findByUserIdAndProjectId(supervised.getId(), project.getId()))
                    .thenReturn(Optional.of(projectAssociation));
            Mockito.when(accountAssociationRepository.findByUserIdAndAccountId(supervised.getId(), account.getId()))
                    .thenReturn(Optional.of(accountAssociation));

            subProjectService.removeAllAssociations(subProject.getId());

            Mockito.verify(subProjectRepository).findById(subProject.getId());
        }

        @Test
        @DisplayName("Delete sub-project")
        public void deleteSubProject()
        {
            User supervisor = new User();
            supervisor.setId(1L);
            User supervised = new User();
            supervised.setId(1L);
            SubProject subProject = new SubProject();
            subProject.setId(1L);
            Privilege privilege = new Privilege();
            privilege.setName(AppConstants.PROJECT_PERMISSION);
            privilege.setLevel(AccessPermission.ADMIN);
            supervisor.setPrivileges(Set.of(privilege));
            Set<SubProject> subProjects = new HashSet<>();
            subProjects.add(subProject);
            subProject.setUsers(Set.of(supervised));
            supervised.setSubProjects(subProjects);
            supervised.setPrivileges(Set.of());
            Project project = new Project();
            project.setId(1L);
            Account account = new Account();
            account.setId(1L);
            subProject.setProject(project);
            project.setAccount(account);

            ProjectAssociation projectAssociation = new ProjectAssociation();
            projectAssociation.setProject(project);
            projectAssociation.setUser(supervised);
            projectAssociation.setPermission(AccessPermission.READ);

            AccountAssociation accountAssociation = new AccountAssociation();
            accountAssociation.setAccount(account);
            accountAssociation.setUser(supervised);
            accountAssociation.setPermission(AccessPermission.READ);

            mockedStatic.when(UserEvaluator::currentUser).thenReturn(supervisor);
            Mockito.when(subProjectRepository.findById(subProject.getId()))
                    .thenReturn(Optional.of(subProject));
            Mockito.when(userRepository.findById(supervised.getId())).thenReturn(Optional.of(supervised));
            Mockito.when(projectAssociationRepository.findByUserIdAndProjectId(supervised.getId(), project.getId()))
                    .thenReturn(Optional.of(projectAssociation));
            Mockito.when(accountAssociationRepository.findByUserIdAndAccountId(supervised.getId(), account.getId()))
                    .thenReturn(Optional.of(accountAssociation));
            Mockito.doNothing().when(subProjectRepository).deleteById(subProject.getId());

            subProjectService.deleteSubProject(subProject.getId());

            Mockito.verify(subProjectRepository).findById(subProject.getId());
            Mockito.verify(subProjectRepository).deleteById(subProject.getId());
        }
    }
}
