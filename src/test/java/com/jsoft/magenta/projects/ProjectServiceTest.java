package com.jsoft.magenta.projects;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.events.projects.ProjectAssociationRemovalEvent;
import com.jsoft.magenta.events.projects.ProjectAssociationUpdateEvent;
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
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.subprojects.SubProject;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.util.AppConstants;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class ProjectServiceTest {

  @InjectMocks
  private ProjectService projectService;

  @Mock
  private SecurityService securityService;

  @Mock
  private ProjectRepository projectRepository;

  @Mock
  private ProjectAssociationRepository projectAssociationRepository;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  @DisplayName("Project creation tests")
  class ProjectCreationTests {

    @Test
    @DisplayName("Create project")
    public void createProject() {
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

      User user = new User();
      user.setId(1L);
      Privilege privilege = new Privilege();
      privilege.setName(AppConstants.ACCOUNT_PERMISSION);
      privilege.setLevel(AccessPermission.ADMIN);
      user.setPrivileges(Set.of(privilege));

      Mockito.when(projectRepository.save(project)).thenReturn(returnedProject);
      Mockito.when(securityService.currentUser()).thenReturn(user);

      projectService.createProject(1L, project);

      Assertions.assertThat(project)
          .extracting("createdAt")
          .isNotNull();

      Mockito.verify(projectRepository).save(project);
    }
  }

  @Nested
  @DisplayName("Project update tests")
  class ProjectUpdateTests {

    @Test
    @DisplayName("Update project")
    public void updateProject() {
      Project project = new Project();
      project.setId(1L);
      project.setName("project");
      Account account = new Account();
      account.setId(1L);
      project.setAccount(account);
      User user = new User();
      user.setId(1L);
      Privilege privilege = new Privilege();
      privilege.setId(1L);
      privilege.setName(AppConstants.PROJECT_PERMISSION);
      privilege.setLevel(AccessPermission.ADMIN);
      Privilege privilege1 = new Privilege();
      privilege1.setId(2L);
      privilege1.setName(AppConstants.ACCOUNT_PERMISSION);
      privilege1.setLevel(AccessPermission.ADMIN);
      user.setPrivileges(Set.of(privilege, privilege1));

      Mockito.when(projectRepository.findAccountIdById(1L))
          .thenReturn(Optional.of(account.getId()));
      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(projectRepository.save(project)).thenReturn(project);
      Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

      projectService.updateProject(project);

      Assertions.assertThat(project)
          .extracting("name")
          .isEqualTo("project");

      Mockito.verify(projectRepository).save(project);
      Mockito.verify(projectRepository).findById(1L);
    }

    @Test
    @DisplayName("Update project with existing name - should throw exception")
    public void updateProjectWithExistingName() {
      Project project = new Project();
      project.setId(1L);
      project.setName("project");
      Account account = new Account();
      account.setId(1L);
      project.setAccount(account);
      User user = new User(1L);
      Privilege privilege = new Privilege();
      privilege.setId(1L);
      privilege.setName(AppConstants.PROJECT_PERMISSION);
      privilege.setLevel(AccessPermission.ADMIN);
      user.setPrivileges(Set.of(privilege));
      Project project1 = new Project(1L);
      project.setName("new name");

      Mockito.when(projectRepository.findAccountIdById(project.getId()))
          .thenReturn(Optional.of(account.getId()));
      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(projectRepository.save(project)).thenReturn(project);
      Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project1));
      Mockito.when(projectRepository.existsByAccountIdAndName(account.getId(), project.getName()))
          .thenReturn(true);

      Assertions.assertThatThrownBy(() -> projectService.updateProject(project))
          .isInstanceOf(DuplicationException.class)
          .hasMessage(
              String.format("Project with name %s already exist for account", project.getName()));

      Mockito.verify(projectRepository, Mockito.never()).save(project);
      Mockito.verify(projectRepository).findById(1L);
    }

    @Test
    @DisplayName("Update project that does not exist")
    public void updateProjectThatDoesNotExist() {
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
      Mockito.verify(projectRepository, Mockito.never())
          .findByAccountIdAndName(1L, project.getName());
    }

    @Test
    @DisplayName("Update project name")
    public void updateProjectName() {
      Project project = new Project();
      project.setId(1L);
      project.setName("project");
      Account account = new Account();
      account.setId(1L);
      project.setAccount(account);
      String newName = "project";
      User user = new User();
      user.setId(1L);
      Privilege privilege = new Privilege();
      privilege.setId(1L);
      privilege.setName(AppConstants.PROJECT_PERMISSION);
      privilege.setLevel(AccessPermission.ADMIN);
      Privilege privilege1 = new Privilege();
      privilege1.setId(2L);
      privilege1.setName(AppConstants.ACCOUNT_PERMISSION);
      privilege1.setLevel(AccessPermission.ADMIN);
      user.setPrivileges(Set.of(privilege, privilege1));

      Mockito.when(projectRepository.findAccountIdById(1L))
          .thenReturn(Optional.of(account.getId()));
      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(projectRepository.save(project)).thenReturn(project);
      Mockito.when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

      projectService.updateProject(project);

      Assertions.assertThat(project)
          .extracting("name")
          .isEqualTo("project");

      Mockito.verify(projectRepository).save(project);
      Mockito.verify(projectRepository).findById(1L);
    }

    @Test
    @DisplayName("Update project name with existing name - should throw exception")
    public void updateProjectNameWithExistingName() {
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

      Assertions
          .assertThatThrownBy(() -> projectService.updateProjectName(project.getId(), "project"))
          .isInstanceOf(NoSuchElementException.class)
          .hasMessage("Project not found");

      Mockito.verify(projectRepository).findById(1L);
      Mockito.verify(projectRepository, Mockito.never()).save(project);
      Mockito.verify(projectRepository, Mockito.never())
          .findByAccountIdAndName(1L, project.getName());
    }

    @Test
    @DisplayName("Create association between user and project")
    public void createAssociation() {
      Project project = new Project();
      project.setId(1L);
      project.setName("project");
      Account account = new Account();
      account.setId(1L);
      project.setAccount(account);
      User user = new User();
      user.setId(1L);
      Privilege privilege = new Privilege();
      privilege.setName(AppConstants.PROJECT_PERMISSION);
      privilege.setLevel(AccessPermission.ADMIN);
      Privilege privilege1 = new Privilege();
      privilege1.setId(2L);
      privilege1.setName(AppConstants.ACCOUNT_PERMISSION);
      privilege1.setLevel(AccessPermission.ADMIN);
      user.setPrivileges(Set.of(privilege, privilege1));

      ProjectAssociation projectAssociation = new ProjectAssociation(user, project,
          AccessPermission.ADMIN);

      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(projectRepository.findAccountIdById(project.getId()))
          .thenReturn(Optional.of(account.getId()));
      Mockito.when(projectRepository.existsById(1L)).thenReturn(true);
      Mockito.when(
          projectAssociationRepository.findByUserIdAndProjectId(user.getId(), project.getId()))
          .thenReturn(Optional.empty());
      Mockito.when(projectAssociationRepository.save(projectAssociation))
          .thenReturn(projectAssociation);

      projectService.createAssociation(user.getId(), project.getId(), AccessPermission.ADMIN);

      Mockito.verify(projectAssociationRepository).save(projectAssociation);
    }

    @Test
    @DisplayName("Create association between user and project with READ permission - should throw exception")
    public void createAssociationWithReadPermission() {
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
      Privilege privilege1 = new Privilege();
      privilege1.setId(2L);
      privilege1.setName(AppConstants.PROJECT_PERMISSION);
      privilege1.setLevel(AccessPermission.ADMIN);
      user.setPrivileges(Set.of(privilege, privilege1));

      ProjectAssociation projectAssociation = new ProjectAssociation(user, project,
          AccessPermission.ADMIN);

      Mockito.when(projectRepository.existsById(1L)).thenReturn(true);
      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(projectAssociationRepository.save(projectAssociation))
          .thenReturn(projectAssociation);

      Assertions.assertThatThrownBy(
          () -> projectService
              .createAssociation(user.getId(), project.getId(), AccessPermission.READ))
          .isInstanceOf(RedundantAssociationException.class)
          .hasMessage("Read association with project while no sub project exist is redundant");

      Mockito.verify(projectRepository).existsById(1L);
      Mockito.verify(projectAssociationRepository, Mockito.never()).save(projectAssociation);
    }

    @Test
    @DisplayName("Update association between user and project to MANAGE permission")
    public void updateAssociationToManagePermission() {
      Project project = new Project();
      project.setId(1L);
      project.setName("project");
      Account account = new Account();
      account.setId(1L);
      project.setAccount(account);
      User user = new User();
      user.setId(1L);
      Privilege privilege = new Privilege();
      privilege.setId(1L);
      privilege.setName(AppConstants.PROJECT_PERMISSION);
      privilege.setLevel(AccessPermission.ADMIN);
      Privilege privilege1 = new Privilege();
      privilege1.setId(2L);
      privilege1.setName(AppConstants.ACCOUNT_PERMISSION);
      privilege1.setLevel(AccessPermission.ADMIN);
      user.setPrivileges(Set.of(privilege, privilege1));

      ProjectAssociation projectAssociation = new ProjectAssociation(user, project,
          AccessPermission.ADMIN);

      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(
          projectAssociationRepository.findByUserIdAndProjectId(user.getId(), project.getId()))
          .thenReturn(Optional.of(projectAssociation));
      Mockito.when(projectAssociationRepository.save(projectAssociation))
          .thenReturn(projectAssociation);

      projectService.updateAssociation(user.getId(), project.getId(), AccessPermission.MANAGE);

      Mockito.verify(projectAssociationRepository)
          .findByUserIdAndProjectId(user.getId(), project.getId());
      Mockito.verify(projectAssociationRepository).save(projectAssociation);
    }

    @Test
    @DisplayName("Update association to READ permission without sub projects - should throw exception")
    public void updateAssociationToReadPermissionWithoutSubProjects() {
      Project project = new Project();
      project.setId(1L);
      project.setName("project");
      Account account = new Account();
      account.setId(1L);
      project.setAccount(account);
      User user = new User();
      user.setId(1L);
      Privilege privilege = new Privilege();
      privilege.setId(1L);
      privilege.setName(AppConstants.PROJECT_PERMISSION);
      privilege.setLevel(AccessPermission.ADMIN);
      Privilege privilege1 = new Privilege();
      privilege1.setId(2L);
      privilege1.setName(AppConstants.ACCOUNT_PERMISSION);
      privilege1.setLevel(AccessPermission.ADMIN);
      user.setPrivileges(Set.of(privilege, privilege1));

      ProjectAssociation projectAssociation = new ProjectAssociation(user, project,
          AccessPermission.ADMIN);

      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(
          projectAssociationRepository.findByUserIdAndProjectId(user.getId(), project.getId()))
          .thenReturn(Optional.of(projectAssociation));
      Mockito.when(projectAssociationRepository.save(projectAssociation))
          .thenReturn(projectAssociation);
      Mockito.doThrow(new RedundantAssociationException()).when(eventPublisher)
          .publishEvent(Mockito.any(ProjectAssociationUpdateEvent.class));

      Assertions.assertThatThrownBy(
          () -> projectService
              .updateAssociation(user.getId(), project.getId(), AccessPermission.READ))
          .isInstanceOf(RedundantAssociationException.class);

      Mockito.verify(eventPublisher)
          .publishEvent(Mockito.any(ProjectAssociationUpdateEvent.class));
      Mockito.verify(projectAssociationRepository, Mockito.never()).save(projectAssociation);
    }

    @Test
    @DisplayName("Update association to MANAGE permission with invalid permission - should throw exception")
    public void updateAssociationToReadPermissionWithoutValidPermission() {
      Project project = new Project();
      project.setId(1L);
      project.setName("project");
      Account account = new Account();
      account.setId(1L);
      project.setAccount(account);
      User user = new User();
      user.setId(1L);
      Privilege privilege = new Privilege();
      privilege.setName(AppConstants.PROJECT_PERMISSION);
      privilege.setLevel(AccessPermission.READ);
      user.setPrivileges(Set.of(privilege));

      User moderator = new User();
      moderator.setId(1L);
      Privilege privilege1 = new Privilege();
      privilege1.setName(AppConstants.PROJECT_PERMISSION);
      privilege1.setLevel(AccessPermission.ADMIN);
      moderator.setPrivileges(Set.of(privilege));

      ProjectAssociation projectAssociation = new ProjectAssociation(user, project,
          AccessPermission.MANAGE);
      user.setProjects(Set.of(projectAssociation));

      Mockito.when(securityService.currentUser()).thenReturn(moderator);
      Mockito.when(securityService.currentUserId()).thenReturn(user.getId());
      Mockito.when(projectRepository.existsById(1L)).thenReturn(true);
      Mockito.when(
          projectAssociationRepository.findByUserIdAndProjectId(user.getId(), project.getId()))
          .thenReturn(Optional.of(projectAssociation));
      Mockito.when(projectAssociationRepository.save(projectAssociation))
          .thenReturn(projectAssociation);

      Assertions.assertThatThrownBy(
          () -> projectService
              .updateAssociation(user.getId(), project.getId(), AccessPermission.MANAGE))
          .isInstanceOf(AuthorizationException.class)
          .hasMessage("User is not authorized to update specified project");

      Mockito.verify(projectAssociationRepository)
          .findByUserIdAndProjectId(user.getId(), project.getId());
      Mockito.verify(projectAssociationRepository, Mockito.never()).save(projectAssociation);
    }
  }

  @Nested
  @DisplayName("Project get tests")
  class ProjectGetTests {

    @Test
    @DisplayName("Get project as admin")
    public void getProjectAsAdmin() {
      Project project = new Project();
      project.setId(1L);
      project.setName("name");
      User user = new User();
      user.setId(1L);
      Privilege privilege = new Privilege();
      privilege.setName(AppConstants.PROJECT_PERMISSION);
      privilege.setLevel(AccessPermission.ADMIN);
      user.setPrivileges(Set.of(privilege));

      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

      Project result = projectService.getProject(project.getId());

      Assertions.assertThat(result)
          .isNotNull()
          .usingRecursiveComparison()
          .isEqualTo(project);

      Mockito.verify(projectRepository).findById(project.getId());
    }

    @Test
    @DisplayName("Get project as manager")
    public void getProjectAsManager() {
      Project project = new Project();
      project.setId(1L);
      project.setName("name");
      User user = new User();
      user.setId(1L);
      Privilege privilege = new Privilege();
      privilege.setName(AppConstants.PROJECT_PERMISSION);
      privilege.setLevel(AccessPermission.MANAGE);
      user.setPrivileges(Set.of(privilege));
      ProjectAssociation projectAssociation = new ProjectAssociation();
      projectAssociation.setProject(project);
      projectAssociation.setUser(user);
      user.setProjects(Set.of(projectAssociation));

      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(securityService.currentUserId()).thenReturn(1L);
      Mockito.when(projectAssociationRepository
          .findAccessPermissionByUserIdAndProjectId(user.getId(), project.getId()))
          .thenReturn(Optional.of(AccessPermission.MANAGE));
      Mockito.when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));

      Project result = projectService.getProject(project.getId());

      Assertions.assertThat(result)
          .isNotNull()
          .usingRecursiveComparison()
          .isEqualTo(project);

      Mockito.verify(projectRepository).findById(1L);
      Mockito.verify(projectAssociationRepository)
          .findAccessPermissionByUserIdAndProjectId(user.getId(), project.getId());
    }

    @Test
    @DisplayName("Get project as reader - should throw exception")
    public void getProjectAsReader() {
      Project project = new Project();
      project.setId(1L);
      project.setName("name");
      User user = new User();
      user.setId(1L);
      Privilege privilege = new Privilege();
      privilege.setName(AppConstants.PROJECT_PERMISSION);
      privilege.setLevel(AccessPermission.READ);
      user.setPrivileges(Set.of(privilege));
      ProjectAssociation projectAssociation = new ProjectAssociation();
      projectAssociation.setProject(project);
      projectAssociation.setUser(user);

      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
      Mockito.when(
          projectAssociationRepository.findByUserIdAndProjectId(user.getId(), project.getId()))
          .thenReturn(Optional.of(projectAssociation));

      Assertions.assertThatThrownBy(() -> projectService.getProject(project.getId()))
          .isInstanceOf(AuthorizationException.class)
          .hasMessage("Cannot access project information with specified permission");

      Mockito.verify(projectRepository, Mockito.never()).findById(1L);
      Mockito.verify(projectAssociationRepository, Mockito.never())
          .findByUserIdAndProjectId(user.getId(), project.getId());
    }

    @Test
    @DisplayName("Get all projects as reader")
    public void getAllProjectsAsReader() {
      Sort sort = Sort.by("name").ascending();
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
      ProjectAssociation projectAssociation = new ProjectAssociation(user, project,
          AccessPermission.READ);
      project.setAssociations(Set.of(projectAssociation));
      List<Project> projectList = List.of(project);
      Page<Project> projects = new PageImpl<>(projectList, pageRequest, 1);

      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(securityService.currentUserId()).thenReturn(1L);
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
    public void getAllProjectsAsManager() {
      Sort sort = Sort.by("name").ascending();
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
      ProjectAssociation projectAssociation = new ProjectAssociation(user, project,
          AccessPermission.MANAGE);
      project.setAssociations(Set.of(projectAssociation));
      List<Project> projectList = List.of(project);
      Page<Project> projects = new PageImpl<>(projectList, pageRequest, 1);

      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(securityService.currentUserId()).thenReturn(1L);
      Mockito.when(
          projectRepository.findAllByAssociationsUserIdAndAssociationsPermissionGreaterThanEqual(
              user.getId(), AccessPermission.MANAGE, pageRequest))
          .thenReturn(projects);

      Page<Project> results = projectService.getAllProjects(0, 5, "name", true);

      Assertions.assertThat(results)
          .isNotNull()
          .isNotEmpty()
          .hasSameSizeAs(projectList)
          .containsAll(projectList);

      Mockito.verify(projectRepository)
          .findAllByAssociationsUserIdAndAssociationsPermissionGreaterThanEqual(
              user.getId(), AccessPermission.MANAGE, pageRequest);
    }

    @Test
    @DisplayName("Get all projects as admin")
    public void getAllProjectsAsAdmin() {
      Sort sort = Sort.by("name").ascending();
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
      ProjectAssociation projectAssociation = new ProjectAssociation(user, project,
          AccessPermission.READ);
      project.setAssociations(Set.of(projectAssociation));
      List<Project> projectList = List.of(project);
      Page<Project> projects = new PageImpl<>(projectList, pageRequest, 1);

      Mockito.when(securityService.currentUser()).thenReturn(user);
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
    @DisplayName("Get all project results by name example as reader")
    public void getAllProjectResultsByNameExampleAsReader() {
      Sort sort = Sort.by("name").descending();
      PageRequest pageRequest = PageRequest.of(0, 5, sort);
      ProjectSearchResult project = new ProjectSearchResult() {
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
      user.setPrivileges(Set.of());
      Account account = new Account();
      account.setId(1L);
      account.setName("account");
      List<ProjectSearchResult> projectList = List.of(project);
      String nameExample = "name";

      Mockito.when(securityService.currentUserId()).thenReturn(1L);
      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(projectRepository
          .findAllResultsByAssociationsUserIdAndNameContainingIgnoreCaseAndAssociationsPermission(
              user.getId(), nameExample, AccessPermission.READ, pageRequest))
          .thenReturn(projectList);

      projectService.getAllProjectsResultsByNameExample(
          nameExample, 5);

      Mockito.verify(projectRepository)
          .findAllResultsByAssociationsUserIdAndNameContainingIgnoreCaseAndAssociationsPermission(
              user.getId(), nameExample, AccessPermission.READ, pageRequest);
    }

    @Test
    @DisplayName("Get all project results by name example as manager")
    public void getAllProjectResultsByNameExampleAsManager() {
      Sort sort = Sort.by("name").descending();
      PageRequest pageRequest = PageRequest.of(0, 5, sort);
      ProjectSearchResult project = new ProjectSearchResult() {
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
      Privilege privilege = new Privilege();
      privilege.setName(AppConstants.PROJECT_PERMISSION);
      privilege.setLevel(AccessPermission.MANAGE);
      user.setPrivileges(Set.of(privilege));
      Account account = new Account();
      account.setId(1L);
      account.setName("account");
      List<ProjectSearchResult> projectList = List.of(project);
      String nameExample = "name";

      Mockito.when(securityService.currentUserId()).thenReturn(1L);
      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(projectRepository
          .findAllResultsByAssociationsUserIdAndNameContainingIgnoreCaseAndAssociationsPermissionGreaterThanEqual(
              user.getId(), nameExample, AccessPermission.MANAGE, pageRequest))
          .thenReturn(projectList);

      projectService.getAllProjectsResultsByNameExample(
          nameExample, 5);

      Mockito.verify(projectRepository)
          .findAllResultsByAssociationsUserIdAndNameContainingIgnoreCaseAndAssociationsPermissionGreaterThanEqual(
              user.getId(), nameExample, AccessPermission.MANAGE, pageRequest);
    }

    @Test
    @DisplayName("Get all project results by name example as admin")
    public void getAllProjectResultsByNameExampleAsAdmin() {
      Sort sort = Sort.by("name").descending();
      PageRequest pageRequest = PageRequest.of(0, 5, sort);
      ProjectSearchResult project = new ProjectSearchResult() {
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
      Privilege privilege = new Privilege();
      privilege.setName(AppConstants.PROJECT_PERMISSION);
      privilege.setLevel(AccessPermission.ADMIN);
      user.setPrivileges(Set.of(privilege));
      Account account = new Account();
      account.setId(1L);
      account.setName("account");
      List<ProjectSearchResult> projectList = List.of(project);
      String nameExample = "name";

      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(projectRepository.findAllResultsByNameContainingIgnoreCase(
          nameExample, pageRequest))
          .thenReturn(projectList);

      projectService.getAllProjectsResultsByNameExample(
          nameExample, 5);

      Mockito.verify(projectRepository).findAllResultsByNameContainingIgnoreCase(
          nameExample, pageRequest);
    }
  }

  @Nested
  @DisplayName("Project delete tests")
  class ProjectDeleteTests {

    @Test
    @DisplayName("Delete project")
    public void deleteProject() {
      Project project = new Project();
      project.setId(1L);
      project.setName("project");
      project.setAssociations(Set.of());
      User user = new User();
      user.setId(1L);
      Privilege privilege = new Privilege();
      privilege.setName(AppConstants.PROJECT_PERMISSION);
      privilege.setLevel(AccessPermission.ADMIN);
      user.setPrivileges(Set.of(privilege));

      Mockito.when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
      Mockito.doNothing().when(projectRepository).deleteById(project.getId());
      Mockito.when(securityService.currentUser()).thenReturn(user);

      projectService.deleteProject(project.getId());

      Mockito.verify(projectRepository).deleteById(project.getId());
    }

    @Test
    @DisplayName("Association removal")
    public void removeAssociation() {
      Project project = new Project();
      project.setId(1L);
      project.setName("project");
      Account account = new Account();
      account.setId(1L);
      project.setAccount(account);
      User user = new User();
      user.setId(1L);
      Privilege privilege = new Privilege();
      privilege.setName(AppConstants.PROJECT_PERMISSION);
      privilege.setLevel(AccessPermission.ADMIN);
      user.setPrivileges(Set.of(privilege));
      Set<SubProject> subProjects = new HashSet<>();
      user.setSubProjects(subProjects);

      ProjectAssociationId projectAssociationId = new ProjectAssociationId();
      projectAssociationId.setProjectId(project.getId());
      projectAssociationId.setUserId(user.getId());
      ProjectAssociation projectAssociation = new ProjectAssociation(
          projectAssociationId,
          AccessPermission.READ,
          project,
          user
      );
      project.setAssociations(Set.of(projectAssociation));

      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
      Mockito.when(projectAssociationRepository.existsById(projectAssociationId)).thenReturn(true);
      Mockito.doNothing().when(projectAssociationRepository).deleteById(projectAssociationId);
      Mockito.doNothing().when(eventPublisher)
          .publishEvent(new ProjectAssociationRemovalEvent(project, user.getId()));

      projectService.removeAssociation(user.getId(), project.getId());

      Mockito.verify(projectRepository).findById(project.getId());
      Mockito.verify(projectAssociationRepository).existsById(projectAssociationId);
      Mockito.verify(projectAssociationRepository).deleteById(projectAssociationId);
    }

    @Test
    @DisplayName("All associations removal")
    public void removeAllAssociation() {
      Project project = new Project();
      project.setId(1L);
      Account account = new Account();
      account.setId(1L);
      project.setAccount(account);
      User user = new User();
      user.setId(1L);
      Privilege privilege = new Privilege();
      privilege.setName(AppConstants.PROJECT_PERMISSION);
      privilege.setLevel(AccessPermission.ADMIN);
      user.setPrivileges(Set.of(privilege));
      Set<SubProject> subProjects = new HashSet<>();
      user.setSubProjects(subProjects);
      ProjectAssociationId projectAssociationId = new ProjectAssociationId();
      projectAssociationId.setProjectId(project.getId());
      projectAssociationId.setUserId(user.getId());
      ProjectAssociation projectAssociation = new ProjectAssociation(
          projectAssociationId,
          AccessPermission.READ,
          project,
          user
      );
      project.setAssociations(Set.of(projectAssociation));

      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(projectAssociationRepository.existsById(projectAssociationId)).thenReturn(true);
      Mockito.when(projectRepository.findById(project.getId())).thenReturn(Optional.of(project));
      Mockito.doNothing().when(projectAssociationRepository).deleteById(projectAssociationId);
      Mockito.doNothing().when(eventPublisher)
          .publishEvent(new ProjectAssociationRemovalEvent(project, user.getId()));

      projectService.removeAllAssociations(project.getId());

      Mockito.verify(projectAssociationRepository).existsById(projectAssociationId);
      Mockito.verify(projectRepository, Mockito.times(2)).findById(project.getId());
      Mockito.verify(projectAssociationRepository).deleteById(projectAssociationId);
    }
  }

}
