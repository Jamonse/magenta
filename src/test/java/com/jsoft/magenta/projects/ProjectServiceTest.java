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
import com.jsoft.magenta.projects.domain.SubProject;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.users.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

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
    }
}
