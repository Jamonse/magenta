package com.jsoft.magenta.projects;

import com.jsoft.magenta.subprojects.SubProject;
import com.jsoft.magenta.subprojects.SubProjectController;
import com.jsoft.magenta.subprojects.SubProjectService;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.util.Stringify;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@WithUserDetails("admin@admin.com")
@AutoConfigureMockMvc
public class SubProjectControllerTest {

  @MockBean
  private SubProjectService subProjectService;

  @Autowired
  private SubProjectController subProjectController;

  @Autowired
  private MockMvc mockMvc;

  @Nested
  @WithUserDetails("admin@admin.com")
  @DisplayName("Sub-project creation tests")
  class SubProjectCreationTests {

    @Test
    @DisplayName("Create sub project")
    public void createSubProject() throws Exception {
      SubProject subProject = new SubProject();
      subProject.setName("sp");
      subProject.setAmountOfHours(20D);

      SubProject savedSubProject = new SubProject();
      savedSubProject.setId(1L);
      savedSubProject.setName("sp");
      savedSubProject.setAmountOfHours(20D);

      Mockito.when(subProjectService.createSubProject(1L, subProject)).thenReturn(savedSubProject);

      mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "sp/{projectId}", 1L)
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(subProject)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isCreated())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.id").isNotEmpty());

      Mockito.verify(subProjectService).createSubProject(1L, subProject);
    }

    @Test
    @DisplayName("Create sub project with invalid name - should return 400")
    public void createSubProjectWithInvalidName() throws Exception {
      SubProject subProject = new SubProject();
      subProject.setAmountOfHours(20D);

      SubProject savedSubProject = new SubProject();
      savedSubProject.setId(1L);
      savedSubProject.setAmountOfHours(20D);

      Mockito.when(subProjectService.createSubProject(1L, subProject)).thenReturn(savedSubProject);

      mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "sp/{projectId}", 1L)
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(subProject)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value(AppConstants.NAME_BLANK_MESSAGE));

      Mockito.verify(subProjectService, Mockito.never()).createSubProject(1L, subProject);
    }

    @Test
    @DisplayName("Create sub project with invalid amount of hours - should return 400")
    public void createSubProjectWithInvalidAmountOfHours() throws Exception {
      SubProject subProject = new SubProject();
      subProject.setName("sp");
      subProject.setAmountOfHours(-20D);

      SubProject savedSubProject = new SubProject();
      savedSubProject.setId(1L);
      savedSubProject.setAmountOfHours(20D);
      savedSubProject.setName("sp");

      Mockito.when(subProjectService.createSubProject(1L, subProject)).thenReturn(savedSubProject);

      mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "sp/{projectId}", 1L)
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(subProject)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value(AppConstants.POSITIVE_NUMBER_MESSAGE));

      Mockito.verify(subProjectService, Mockito.never()).createSubProject(1L, subProject);
    }

    @Test
    @DisplayName("Create association")
    public void createAssociation() throws Exception {
      SubProject subProject = new SubProject();
      subProject.setId(1L);
      subProject.setName("sp");
      User user = new User();
      user.setId(1L);
      subProject.setUsers(Set.of(user));

      Mockito.when(subProjectService.createAssociation(user.getId(), subProject.getId()))
          .thenReturn(subProject);

      mockMvc.perform(MockMvcRequestBuilders.post(
          Stringify.BASE_URL + "sp/{subProjectId}/association/{userId}",
          subProject.getId(),
          user.getId())
          .contentType(MediaType.APPLICATION_JSON)
      )
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.content().json(Stringify.asJsonString(subProject)));

      Mockito.verify(subProjectService).createAssociation(user.getId(), subProject.getId());
    }
  }

  @Nested
  @WithUserDetails("admin@admin.com")
  @DisplayName("Sub-project update tests")
  class SubProjectUpdateTests {

    @Test
    @DisplayName("Update sub-project")
    public void updateSubProject() throws Exception {
      SubProject subProject = new SubProject();
      subProject.setId(1L);
      subProject.setName("sp");
      subProject.setAmountOfHours(20D);

      SubProject savedSubProject = new SubProject();
      savedSubProject.setId(1L);
      savedSubProject.setName("sp");
      savedSubProject.setAmountOfHours(20D);

      Mockito.when(subProjectService.updateSubProject(subProject)).thenReturn(savedSubProject);

      mockMvc.perform(MockMvcRequestBuilders.put(Stringify.BASE_URL + "sp")
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(subProject)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.content().json(Stringify.asJsonString(savedSubProject)));

      Mockito.verify(subProjectService).updateSubProject(subProject);
    }

    @Test
    @DisplayName("Update sub-project with invalid name - should return 400")
    public void updateSubProjectWithInvalidName() throws Exception {
      SubProject subProject = new SubProject();
      subProject.setId(1L);
      subProject.setAmountOfHours(20D);

      SubProject savedSubProject = new SubProject();
      savedSubProject.setId(1L);
      savedSubProject.setAmountOfHours(20D);

      Mockito.when(subProjectService.updateSubProject(subProject)).thenReturn(savedSubProject);

      mockMvc.perform(MockMvcRequestBuilders.put(Stringify.BASE_URL + "sp")
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(subProject)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value(AppConstants.NAME_BLANK_MESSAGE));

      Mockito.verify(subProjectService, Mockito.never()).updateSubProject(subProject);
    }

    @Test
    @DisplayName("Update sub-project with invalid amount of hours - should return 400")
    public void updateSubProjectWithInvalidHoursAmount() throws Exception {
      SubProject subProject = new SubProject();
      subProject.setId(1L);
      subProject.setName("sp");
      subProject.setAmountOfHours(-20D);

      SubProject savedSubProject = new SubProject();
      savedSubProject.setId(1L);
      savedSubProject.setName("sp");
      savedSubProject.setAmountOfHours(20D);

      Mockito.when(subProjectService.updateSubProject(subProject)).thenReturn(savedSubProject);

      mockMvc.perform(MockMvcRequestBuilders.put(Stringify.BASE_URL + "sp")
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(subProject)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value(AppConstants.POSITIVE_NUMBER_MESSAGE));

      Mockito.verify(subProjectService, Mockito.never()).updateSubProject(subProject);
    }

    @Test
    @DisplayName("Update sub-project name")
    public void updateSubProjectName() throws Exception {
      SubProject subProject = new SubProject();
      subProject.setId(1L);
      subProject.setName("sp");
      subProject.setAmountOfHours(20D);
      String newName = "new name";

      Mockito.when(subProjectService.updateSubProjectName(subProject.getId(), newName))
          .thenReturn(subProject);

      mockMvc.perform(MockMvcRequestBuilders
          .patch(Stringify.BASE_URL + "sp/name/{subProjectId}", subProject.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(newName))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.content().json(Stringify.asJsonString(subProject)));

      Mockito.verify(subProjectService).updateSubProjectName(subProject.getId(), newName);
    }

    @Test
    @DisplayName("Update sub-project name with invalid name")
    public void updateSubProjectNameWithInvalidName() throws Exception {
      SubProject subProject = new SubProject();
      subProject.setId(1L);
      subProject.setName("sp");
      subProject.setAmountOfHours(20D);
      String newName = "n";

      Mockito.when(subProjectService.updateSubProjectName(subProject.getId(), newName))
          .thenReturn(subProject);

      mockMvc.perform(MockMvcRequestBuilders
          .patch(Stringify.BASE_URL + "sp/name/{subProjectId}", subProject.getId())
          .contentType(MediaType.APPLICATION_JSON)
          .content(newName))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value(AppConstants.NAME_LENGTH_MESSAGE));

      Mockito.verify(subProjectService, Mockito.never())
          .updateSubProjectName(subProject.getId(), newName);
    }

    @Test
    @DisplayName("Update sub-project amount of hours")
    public void updateSubProjectHoursAmount() throws Exception {
      SubProject subProject = new SubProject();
      subProject.setId(1L);
      subProject.setName("sp");
      subProject.setAmountOfHours(20D);
      Double newAmount = 25D;

      Mockito.when(subProjectService.updateSubProjectHours(subProject.getId(), newAmount))
          .thenReturn(subProject);

      mockMvc.perform(MockMvcRequestBuilders.patch(
          Stringify.BASE_URL + "sp/{subProjectId}/amount/{newAmount}",
          subProject.getId(),
          newAmount)
          .contentType(MediaType.APPLICATION_JSON))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.content().json(Stringify.asJsonString(subProject)));

      Mockito.verify(subProjectService).updateSubProjectHours(subProject.getId(), newAmount);
    }

    @Test
    @DisplayName("Update sub-project amount of hours with invalid amount - should return 400")
    public void updateSubProjectHoursAmountWithInvalidAmount() throws Exception {
      SubProject subProject = new SubProject();
      subProject.setId(1L);
      subProject.setName("sp");
      subProject.setAmountOfHours(20D);
      Double newAmount = -25D;

      Mockito.when(subProjectService.updateSubProjectHours(subProject.getId(), newAmount))
          .thenReturn(subProject);

      mockMvc.perform(MockMvcRequestBuilders.patch(
          Stringify.BASE_URL + "sp/{subProjectId}/amount/{newAmount}",
          subProject.getId(),
          newAmount)
          .contentType(MediaType.APPLICATION_JSON))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value(AppConstants.POSITIVE_NUMBER_MESSAGE));

      Mockito.verify(subProjectService, Mockito.never())
          .updateSubProjectHours(subProject.getId(), newAmount);
    }

    @Test
    @DisplayName("Increase sub-project amount of hours")
    public void increaseSubProjectHoursAmount() throws Exception {
      SubProject subProject = new SubProject();
      subProject.setId(1L);
      subProject.setName("sp");
      subProject.setAmountOfHours(20D);
      Double amountToAdd = 25D;

      Mockito.when(subProjectService.increaseSubProjectHours(subProject.getId(), amountToAdd))
          .thenReturn(subProject);

      mockMvc.perform(MockMvcRequestBuilders.patch(
          Stringify.BASE_URL + "sp/{subProjectId}/increase/{amountToAdd}",
          subProject.getId(),
          amountToAdd)
          .contentType(MediaType.APPLICATION_JSON))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.content().json(Stringify.asJsonString(subProject)));

      Mockito.verify(subProjectService).increaseSubProjectHours(subProject.getId(), amountToAdd);
    }

    @Test
    @DisplayName("Decrease sub-project amount of hours")
    public void decreaseSubProjectHoursAmount() throws Exception {
      SubProject subProject = new SubProject();
      subProject.setId(1L);
      subProject.setName("sp");
      subProject.setAmountOfHours(20D);
      Double amountToRemove = 5D;

      Mockito.when(subProjectService.decreaseSubProjectHours(subProject.getId(), amountToRemove))
          .thenReturn(subProject);

      mockMvc.perform(MockMvcRequestBuilders.patch(
          Stringify.BASE_URL + "sp/{subProjectId}/decrease/{amountToAdd}",
          subProject.getId(),
          amountToRemove)
          .contentType(MediaType.APPLICATION_JSON))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.content().json(Stringify.asJsonString(subProject)));

      Mockito.verify(subProjectService).decreaseSubProjectHours(subProject.getId(), amountToRemove);
    }

    @Test
    @DisplayName("Increase sub-project amount of hours with invalid amount - should return 400")
    public void increaseSubProjectHoursAmountWithInvalidAmount() throws Exception {
      SubProject subProject = new SubProject();
      subProject.setId(1L);
      subProject.setName("sp");
      subProject.setAmountOfHours(20D);
      Double amountToAdd = -25D;

      Mockito.when(subProjectService.increaseSubProjectHours(subProject.getId(), amountToAdd))
          .thenReturn(subProject);

      mockMvc.perform(MockMvcRequestBuilders.patch(
          Stringify.BASE_URL + "sp/{subProjectId}/increase/{amountToAdd}",
          subProject.getId(),
          amountToAdd)
          .contentType(MediaType.APPLICATION_JSON))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value(AppConstants.POSITIVE_NUMBER_MESSAGE));

      Mockito.verify(subProjectService, Mockito.never())
          .increaseSubProjectHours(subProject.getId(), amountToAdd);
    }

    @Test
    @DisplayName("Decrease sub-project amount of hours with invalid amount - should return 400")
    public void decreaseSubProjectHoursAmountWithInvalidAmount() throws Exception {
      SubProject subProject = new SubProject();
      subProject.setId(1L);
      subProject.setName("sp");
      subProject.setAmountOfHours(20D);
      Double amountToRemove = -5D;

      Mockito.when(subProjectService.decreaseSubProjectHours(subProject.getId(), amountToRemove))
          .thenReturn(subProject);

      mockMvc.perform(MockMvcRequestBuilders.patch(
          Stringify.BASE_URL + "sp/{subProjectId}/decrease/{amountToAdd}",
          subProject.getId(),
          amountToRemove)
          .contentType(MediaType.APPLICATION_JSON))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value(AppConstants.POSITIVE_NUMBER_MESSAGE));

      Mockito.verify(subProjectService, Mockito.never())
          .decreaseSubProjectHours(subProject.getId(), amountToRemove);
    }
  }

  @Nested
  @WithUserDetails("admin@admin.com")
  @DisplayName("Sub-project get tests")
  class SubProjectGetTests {

    @Nested
    @WithUserDetails("admin@admin.com")
    @DisplayName("Sub-project delete tests")
    class SubProjectDeleteTests {

      @Test
      @DisplayName("Remove association")
      public void removeAssociation() throws Exception {
        Mockito.doNothing().when(subProjectService).removeAssociation(1L, 1L);

        mockMvc.perform(MockMvcRequestBuilders
            .delete(Stringify.BASE_URL + "sp/{subProjectId}/association/{userId}", 1L, 1L))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(subProjectService).removeAssociation(1L, 1L);
      }

      @Test
      @DisplayName("Delete sub-project")
      public void deleteSubProject() throws Exception {
        Mockito.doNothing().when(subProjectService).deleteSubProject(1L);

        mockMvc.perform(MockMvcRequestBuilders.delete(Stringify.BASE_URL + "sp/{subProjectId}", 1L))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk());

        Mockito.verify(subProjectService).deleteSubProject(1L);
      }
    }
  }
}
