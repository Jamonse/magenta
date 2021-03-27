package com.jsoft.magenta.projects;

import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.projects.domain.ProjectSearchResult;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.subprojects.SubProject;
import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.util.Stringify;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@WithUserDetails("admin@admin.com")
@AutoConfigureMockMvc
public class ProjectControllerTest {

  @MockBean
  private ProjectService projectService;

  @Autowired
  private ProjectController projectController;

  @Autowired
  private MockMvc mockMvc;

  @Nested
  @WithUserDetails("admin@admin.com")
  @DisplayName("Project creation tests")
  class ProjectCreationTests {

    @Test
    @DisplayName("Create project")
    public void createProject() throws Exception {
      Project project = new Project();
      project.setName("project");
      Project savedProject = new Project();
      savedProject.setId(1L);
      savedProject.setName("project");
      savedProject.setCreatedAt(LocalDate.now());

      Mockito.when(projectService.createProject(1L, project)).thenReturn(savedProject);

      mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "projects/{accountId}", 1L)
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(project)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isCreated())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("id").isNotEmpty())
          .andExpect(MockMvcResultMatchers.jsonPath("createdAt").isNotEmpty());

      Mockito.verify(projectService).createProject(1L, project);
    }

    @Test
    @DisplayName("Create project association")
    public void createProjectAssociation() throws Exception {
      Long userId = 1L;
      Long projectId = 1L;

      Mockito.doNothing().when(projectService)
          .createAssociation(userId, projectId, AccessPermission.MANAGE);

      mockMvc.perform(MockMvcRequestBuilders.post(
          Stringify.BASE_URL + "projects/{projectId}/association/{userId}",
          projectId, userId)
          .content(AccessPermission.MANAGE.name()))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isCreated());

      Mockito.verify(projectService).createAssociation(userId, projectId, AccessPermission.MANAGE);
    }

    @Test
    @DisplayName("Create association with invalid permission level name - should return 400")
    public void createProjectAssociationWithInvalidName() throws Exception {
      Long userId = 1L;
      Long projectId = 1L;

      Mockito.doNothing().when(projectService)
          .createAssociation(userId, projectId, AccessPermission.MANAGE);

      mockMvc.perform(MockMvcRequestBuilders.post(
          Stringify.BASE_URL + "projects/{projectId}/association/{userId}",
          projectId, userId)
          .content("Invalid name"))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value(AppConstants.PERMISSION_NAME_MESSAGE));

      Mockito.verify(projectService, Mockito.never())
          .createAssociation(userId, projectId, AccessPermission.MANAGE);
    }

    @Test
    @DisplayName("Create project with invalid name - should return 400")
    public void createProjectWithInvalidName() throws Exception {
      Project project = new Project();
      Project savedProject = new Project();
      savedProject.setId(1L);
      savedProject.setCreatedAt(LocalDate.now());

      Mockito.when(projectService.createProject(1L, project)).thenReturn(savedProject);

      mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "projects/{accountId}", 1L)
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(project)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value("Name must not be null or empty"));

      Mockito.verify(projectService, Mockito.never()).createProject(1L, project);
    }

    @Test
    @DisplayName("Create project with invalid sub project name - should return 400")
    public void createProjectWithInvalidSubProjectName() throws Exception {
      Project project = new Project();
      Project savedProject = new Project();
      project.setName("project");
      SubProject subProject = new SubProject();
      project.setSubProjects(Set.of(subProject));
      savedProject.setId(1L);
      savedProject.setName("project");
      savedProject.setCreatedAt(LocalDate.now());

      Mockito.when(projectService.createProject(1L, project)).thenReturn(savedProject);

      mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "projects/{accountId}", 1L)
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(project)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value("Name must not be null or empty"))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[1]").doesNotExist());

      Mockito.verify(projectService, Mockito.never()).createProject(1L, project);
    }

    @Test
    @DisplayName("Create project with invalid sub project amount - should return 400")
    public void createProjectWithInvalidSubProjectAmount() throws Exception {
      Project project = new Project();
      Project savedProject = new Project();
      project.setName("project");
      SubProject subProject = new SubProject();
      subProject.setName("sub-project");
      subProject.setAmountOfHours(-1d);
      project.setSubProjects(Set.of(subProject));
      savedProject.setId(1L);
      savedProject.setName("project");
      savedProject.setCreatedAt(LocalDate.now());

      Mockito.when(projectService.createProject(1L, project)).thenReturn(savedProject);

      mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "projects/{accountId}", 1L)
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(project)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value("Number must be greater than or equal to 0"))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[1]").doesNotExist());

      Mockito.verify(projectService, Mockito.never()).createProject(1L, project);
    }
  }

  @Nested
  @WithUserDetails("admin@admin.com")
  @DisplayName("Project update tests")
  class ProjectUpdateTests {

    @Test
    @DisplayName("Update project")
    public void updateProject() throws Exception {
      Project project = new Project();
      project.setId(1L);
      project.setName("project");

      Mockito.when(projectService.updateProject(project)).thenReturn(project);

      mockMvc.perform(MockMvcRequestBuilders.put(Stringify.BASE_URL + "projects")
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(project)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.content().json(Stringify.asJsonString(project)));

      Mockito.verify(projectService).updateProject(project);
    }

    @Test
    @DisplayName("Update project association")
    public void updateProjectAssociation() throws Exception {
      Long userId = 1L;
      Long projectId = 1L;

      Mockito.doNothing().when(projectService)
          .updateAssociation(userId, projectId, AccessPermission.MANAGE);

      mockMvc.perform(MockMvcRequestBuilders.patch(
          Stringify.BASE_URL + "projects/{projectId}/association/{userId}",
          projectId, userId)
          .content(AccessPermission.MANAGE.name()))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk());

      Mockito.verify(projectService).updateAssociation(userId, projectId, AccessPermission.MANAGE);
    }

    @Test
    @DisplayName("Update project association with invalid permission - should return 400")
    public void updateProjectAssociationWithInvalidPermission() throws Exception {
      Long userId = 1L;
      Long projectId = 1L;

      Mockito.doNothing().when(projectService)
          .updateAssociation(userId, projectId, AccessPermission.MANAGE);

      mockMvc.perform(MockMvcRequestBuilders.patch(
          Stringify.BASE_URL + "projects/{projectId}/association/{userId}",
          projectId, userId)
          .content("Invalid permission"))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value(AppConstants.PERMISSION_NAME_MESSAGE));

      Mockito.verify(projectService, Mockito.never())
          .updateAssociation(userId, projectId, AccessPermission.MANAGE);
    }

    @Test
    @DisplayName("Update project with invalid name - should return 400")
    public void updateProjectWithInvalidName() throws Exception {
      Project project = new Project();
      project.setId(1L);

      Mockito.when(projectService.updateProject(project)).thenReturn(project);

      mockMvc.perform(MockMvcRequestBuilders.put(Stringify.BASE_URL + "projects")
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(project)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value(AppConstants.NAME_BLANK_MESSAGE));

      Mockito.verify(projectService, Mockito.never()).updateProject(project);
    }

    @Test
    @DisplayName("Update project name")
    public void updateProjectName() throws Exception {
      Project project = new Project();
      project.setId(1L);
      project.setName("project");
      Project savedProject = new Project();
      String projectNewName = "new name";
      savedProject.setId(1L);
      savedProject.setName(projectNewName);

      Mockito.when(projectService.updateProjectName(1L, projectNewName)).thenReturn(savedProject);

      mockMvc.perform(MockMvcRequestBuilders
          .patch(Stringify.BASE_URL + "projects/name/{projectId}", project.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(projectNewName))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("name").value(projectNewName));

      Mockito.verify(projectService).updateProjectName(1L, projectNewName);
    }

    @Test
    @DisplayName("Update project name with invalid name - should return 400")
    public void updateProjectNameWithInvalidName() throws Exception {
      Project project = new Project();
      project.setId(1L);
      project.setName("project");
      String projectNewName = "t";
      Project savedProject = new Project();

      Mockito.when(projectService.updateProjectName(1L, projectNewName)).thenReturn(savedProject);

      mockMvc.perform(MockMvcRequestBuilders
          .patch(Stringify.BASE_URL + "projects/name/{projectId}", project.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(projectNewName))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value(AppConstants.NAME_LENGTH_MESSAGE));

      Mockito.verify(projectService, Mockito.never()).updateProjectName(1L, projectNewName);
    }
  }

  @Nested
  @WithUserDetails("admin@admin.com")
  @DisplayName("Project get tests")
  class ProjectGetTests {

    @Test
    @DisplayName("Get all projects")
    public void getAllProjects() throws Exception {
      Sort sort = Sort.by("name").descending();
      PageRequest pageRequest = PageRequest.of(0, 5, sort);
      Project project = new Project();
      project.setId(1L);
      project.setName("project");
      List<Project> projectList = List.of(project);
      Page<Project> projects = new PageImpl<>(projectList, pageRequest, projectList.size());

      Mockito.when(projectService.getAllProjects(pageRequest.getPageNumber(),
          pageRequest.getPageSize(), "name", false))
          .thenReturn(projects);

      mockMvc.perform(MockMvcRequestBuilders.get(Stringify.BASE_URL + "projects")
          .contentType(MediaType.APPLICATION_JSON))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1));

      Mockito.verify(projectService).getAllProjects(pageRequest.getPageNumber(),
          pageRequest.getPageSize(), "name", false);
    }

    @Test
    @DisplayName("Get all projects results by name example")
    public void getAllProjectsResultsByNameExample() throws Exception {
      ProjectSearchResult project = new ProjectSearchResult() {

        @Override
        public Long getId() {
          return 1L;
        }

        @Override
        public String getName() {
          return "name";
        }
      };

      List<ProjectSearchResult> projectList = List.of(project);

      Mockito.when(projectService.getAllProjectsResultsByNameExample("name", 5))
          .thenReturn(projectList);

      mockMvc.perform(MockMvcRequestBuilders.get(Stringify.BASE_URL + "projects/search")
          .queryParam("nameExample", "name")
          .queryParam("resultsCount", "5")
          .contentType(MediaType.APPLICATION_JSON))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("name"));

      Mockito.verify(projectService).getAllProjectsResultsByNameExample("name", 5);
    }

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Project delete tests")
    class ProjectDeleteTests {

      @Test
      @DisplayName("Delete project")
      public void deleteProject() throws Exception {
        Mockito.doNothing().when(projectService).deleteProject(1L);

        mockMvc
            .perform(MockMvcRequestBuilders.delete(Stringify.BASE_URL + "projects/{projectId}", 1L))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(projectService).deleteProject(1L);
      }

      @Test
      @DisplayName("Remove association")
      public void removeAssociation() throws Exception {
        Mockito.doNothing().when(projectService).removeAssociation(1L, 1L);

        mockMvc.perform(MockMvcRequestBuilders.delete(
            Stringify.BASE_URL + "projects/{projectId}/association/{userId}", 1L, 1L))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(projectService).removeAssociation(1L, 1L);
      }

      @Test
      @DisplayName("Remove all associations")
      public void removeAllAssociation() throws Exception {
        Mockito.doNothing().when(projectService).removeAllAssociations(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete(
            Stringify.BASE_URL + "projects/{projectId}/association", 1L))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(projectService).removeAllAssociations(1L);
      }
    }
  }
}
