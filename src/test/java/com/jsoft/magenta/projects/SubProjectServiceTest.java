package com.jsoft.magenta.projects;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.accounts.domain.AccountAssociation;
import com.jsoft.magenta.exceptions.AuthorizationException;
import com.jsoft.magenta.exceptions.DuplicationException;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.projects.domain.ProjectAssociation;
import com.jsoft.magenta.security.SecurityService;
import com.jsoft.magenta.subprojects.SubProject;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.subprojects.SubProjectRepository;
import com.jsoft.magenta.subprojects.SubProjectService;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.util.AppConstants;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.context.ApplicationEventPublisher;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class SubProjectServiceTest
{
    @InjectMocks
    private SubProjectService subProjectService;

    @Mock
    private SecurityService securityService;

    @Mock
    private SubProjectRepository subProjectRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @BeforeEach
    public void init()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Sub-projects creation tests")
    class SubProjectCreationTests {
        @Test
        @DisplayName("Create sub-project")
        public void createSubProject() {
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

            Mockito.when(securityService.currentUser()).thenReturn(user);

            Mockito.when(subProjectRepository.save(subProject)).thenReturn(subProject);

            subProjectService.createSubProject(1L, subProject);

            Assertions.assertThat(subProject)
                    .extracting("project")
                    .extracting("id")
                    .isEqualTo(project.getId());
            Assertions.assertThat(subProject)
                    .extracting("available")
                    .isEqualTo(true);

            Mockito.verify(subProjectRepository).save(subProject);
        }

        @Test
        @DisplayName("Create association")
        public void createAssociation() {
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

            Mockito.when(securityService.currentUser()).thenReturn(user);

            Mockito.when(subProjectRepository.findProjectIdById(subProject.getId())).thenReturn(Optional.of(subProject.getId()));
            Mockito.when(subProjectRepository.findById(subProject.getId())).thenReturn(Optional.of(subProject));
            Mockito.when(subProjectRepository.existsByUsersId(user.getId())).thenReturn(false);

            subProjectService.createAssociation(user.getId(), subProject.getId());

            Assertions.assertThat(subProject.getUsers())
                    .contains(user);
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

            Mockito.when(securityService.currentUser()).thenReturn(user);

            Mockito.when(subProjectRepository.findProjectIdById(subProject.getId())).thenReturn(Optional.of(project.getId()));
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

            SubProject subProject1 = new SubProject(1L);
            subProject1.setName("new name");

            Mockito.when(securityService.currentUser()).thenReturn(user);

            Mockito.when(subProjectRepository.findProjectIdById(project.getId())).thenReturn(Optional.of(1L));
            Mockito.when(subProjectRepository.existsByProjectIdAndName(project.getId(), subProject.getName()))
                    .thenReturn(true);
            Mockito.when(subProjectRepository.findById(subProject.getId())).thenReturn(Optional.of(subProject1));
            Mockito.when(subProjectRepository.save(subProject)).thenReturn(subProject);

            Assertions.assertThatThrownBy(() -> subProjectService.updateSubProject(subProject))
                    .isInstanceOf(DuplicationException.class)
                    .hasMessage(
                            String.format("Sub-project with name %s already exists for project", subProject.getName())
                    );

            Mockito.verify(subProjectRepository).findProjectIdById(project.getId());
            Mockito.verify(subProjectRepository).existsByProjectIdAndName(project.getId(), subProject.getName());
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

            Mockito.when(securityService.currentUser()).thenReturn(user);

            Mockito.when(subProjectRepository.findProjectIdById(subProject.getId())).thenReturn(Optional.of(project.getId()));
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

            Mockito.when(securityService.currentUser()).thenReturn(user);

            Mockito.when(subProjectRepository.findProjectIdById(subProject.getId())).thenReturn(Optional.of(project.getId()));
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

            Mockito.when(securityService.currentUser()).thenReturn(user);

            Mockito.when(subProjectRepository.findProjectIdById(subProject.getId())).thenReturn(Optional.of(project.getId()));
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

            Mockito.when(securityService.currentUser()).thenReturn(user);

            Mockito.when(subProjectRepository.findProjectIdById(subProject.getId())).thenReturn(Optional.of(project.getId()));
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
            Set<User> users = new HashSet<>();
            users.add(supervised);
            subProject.setUsers(users);
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

            Mockito.when(securityService.currentUser()).thenReturn(supervisor);
            Mockito.when(subProjectRepository.findByIdAndUsersId(subProject.getId(), supervised.getId()))
                    .thenReturn(Optional.of(subProject));

            subProjectService.removeAssociation(supervised.getId(), subProject.getId());

            Mockito.verify(subProjectRepository).findByIdAndUsersId(subProject.getId(), supervised.getId());
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

            Mockito.when(securityService.currentUser()).thenReturn(supervisor);
            Mockito.when(subProjectRepository.findById(subProject.getId()))
                    .thenReturn(Optional.of(subProject));

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

            Mockito.when(securityService.currentUser()).thenReturn(supervisor);
            Mockito.when(subProjectRepository.findById(subProject.getId()))
                    .thenReturn(Optional.of(subProject));
            Mockito.doNothing().when(subProjectRepository).deleteById(subProject.getId());

            subProjectService.deleteSubProject(subProject.getId());

            Mockito.verify(subProjectRepository).findById(subProject.getId());
            Mockito.verify(subProjectRepository).deleteById(subProject.getId());
        }
    }
}
