package com.jsoft.magenta.users;

import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.util.AppDefaults;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@WithUserDetails("admin@admin.com")
@AutoConfigureMockMvc
public class UserControllerTest {

  @MockBean
  private UserService userService;

  @Autowired
  private UserController userController;

  @Autowired
  private MockMvc mockMvc;

  @Nested
  @WithUserDetails("admin@admin.com")
  @DisplayName("User creation tests")
  class UserCreationTests {

    @Test
    @DisplayName("Create user")
    public void createUser() throws Exception {
      User user = new User();
      user.setFirstName("first name");
      user.setLastName("last name");
      user.setEmail("email@email.com");
      user.setPhoneNumber("055-5555555");
      user.setBirthDay(LocalDate.now().minusYears(20));
      user.setPassword("password");
      user.setPreferredTheme(ColorTheme.LIGHT);

      MockMultipartFile profileImage = new MockMultipartFile(
          "profileImage", "profileImage.jpg", MediaType.IMAGE_JPEG_VALUE,
          "profileImage".getBytes());

      Mockito.when(userService.createUser(user, profileImage)).thenReturn(user);

      mockMvc.perform(MockMvcRequestBuilders.multipart(Stringify.BASE_URL + "users")
          .file(profileImage)
          .contentType(MediaType.APPLICATION_JSON_VALUE)
          .content(Stringify.asJsonString(user)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isCreated());

      Mockito.verify(userService)
          .createUser(Mockito.any(User.class), Mockito.any(MultipartFile.class));
    }

    @Test
    @DisplayName("Create user with invalid name - should return 400")
    public void createUserWithInvalidName() throws Exception {
      User user = new User();
      user.setFirstName("f");
      user.setLastName("last name");
      user.setEmail("email@email.com");
      user.setPhoneNumber("055-5555555");
      user.setBirthDay(LocalDate.now().minusYears(20));
      user.setPassword("password");
      user.setPreferredTheme(ColorTheme.LIGHT);

      Mockito.when(userService.createUser(user, null)).thenReturn(user);

      mockMvc.perform(MockMvcRequestBuilders.multipart(Stringify.BASE_URL + "users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(user)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value(AppConstants.NAME_LENGTH_MESSAGE));

      Mockito.verify(userService, Mockito.never()).createUser(user, null);
    }

    @Test
    @DisplayName("Create user with invalid email - should return 400")
    public void createUserWithInvalidEmail() throws Exception {
      User user = new User();
      user.setFirstName("first name");
      user.setLastName("last name");
      user.setEmail("m");
      user.setPhoneNumber("055-5555555");
      user.setBirthDay(LocalDate.now().minusYears(20));
      user.setPassword("password");
      user.setPreferredTheme(ColorTheme.LIGHT);

      Mockito.when(userService.createUser(user, null)).thenReturn(user);

      mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(user)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value(AppConstants.EMAIL_INVALID_MESSAGE));

      Mockito.verify(userService, Mockito.never()).createUser(user, null);
    }

    @Test
    @DisplayName("Create user with invalid phone number - should return 400")
    public void createUserWithInvalidPhoneNumber() throws Exception {
      User user = new User();
      user.setFirstName("first name");
      user.setLastName("last name");
      user.setEmail("email@email.com");
      user.setPhoneNumber("0");
      user.setBirthDay(LocalDate.now().minusYears(20));
      user.setPassword("password");
      user.setPreferredTheme(ColorTheme.LIGHT);

      Mockito.when(userService.createUser(user, null)).thenReturn(user);

      mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(user)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value(AppConstants.PHONE_NUMBER_MESSAGE));

      Mockito.verify(userService, Mockito.never()).createUser(user, null);
    }

    @Test
    @DisplayName("Create user with invalid password - should return 400")
    public void createUserWithInvalidPassword() throws Exception {
      User user = new User();
      user.setFirstName("first name");
      user.setLastName("last name");
      user.setEmail("email@email.com");
      user.setPhoneNumber("055-5555555");
      user.setBirthDay(LocalDate.now().minusYears(20));
      user.setPreferredTheme(ColorTheme.LIGHT);

      Mockito.when(userService.createUser(user, null)).thenReturn(user);

      mockMvc.perform(MockMvcRequestBuilders.post(Stringify.BASE_URL + "users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(user)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value(AppConstants.PASSWORD_BLANK_MESSAGE));

      Mockito.verify(userService, Mockito.never()).createUser(user, null);
    }

    @Test
    @DisplayName("Create supervision")
    public void createSupervision() throws Exception {
      User user = new User();
      user.setId(1L);
      user.setFirstName("first name");
      user.setLastName("last name");
      user.setEmail("email@email.com");
      user.setPhoneNumber("055-5555555");
      user.setBirthDay(LocalDate.now().minusYears(20));
      user.setPreferredTheme(ColorTheme.LIGHT);

      Mockito.when(userService.createSupervision(user.getId(), 1L))
          .thenReturn(user);

      mockMvc.perform(MockMvcRequestBuilders
          .post(Stringify.BASE_URL + "users/{supervisorId}/supervise/{supervisedId}", user.getId(),
              1L)
          .contentType(MediaType.APPLICATION_JSON))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

      Mockito.verify(userService).createSupervision(user.getId(), 1L);
    }
  }

  @Nested
  @WithUserDetails("admin@admin.com")
  @DisplayName("User update tests")
  class UserUpdateTests {

    @Test
    @DisplayName("Update user")
    public void updateUser() throws Exception {
      User user = new User();
      user.setId(1L);
      user.setFirstName("first name");
      user.setLastName("last name");
      user.setEmail("email@email.com");
      user.setPhoneNumber("055-5555555");
      user.setBirthDay(LocalDate.now().minusYears(20));
      user.setPassword("password");
      user.setPreferredTheme(ColorTheme.LIGHT);

      Mockito.when(userService.updateUser(user)).thenReturn(user);

      mockMvc.perform(MockMvcRequestBuilders.put(Stringify.BASE_URL + "users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(Stringify.asJsonString(user)))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

      Mockito.verify(userService).updateUser(user);
    }

    @Test
    @DisplayName("Update preferred theme")
    public void updatePreferredTheme() throws Exception {
      Mockito.when(userService.updatePreferredTheme(ColorTheme.LIGHT))
          .thenReturn(new User());

      mockMvc.perform(MockMvcRequestBuilders.patch(Stringify.BASE_URL + "users/theme")
          .contentType(MediaType.APPLICATION_JSON)
          .content("light"))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

      Mockito.verify(userService).updatePreferredTheme(ColorTheme.LIGHT);
    }

    @Test
    @DisplayName("Update preferred them to none existing theme - should return 400")
    public void updatePreferredThemeToNoneExistingTheme() throws Exception {
      Mockito.when(userService.updatePreferredTheme(ColorTheme.LIGHT))
          .thenReturn(new User());

      mockMvc.perform(MockMvcRequestBuilders.patch(Stringify.BASE_URL + "users/theme")
          .contentType(MediaType.APPLICATION_JSON)
          .content("blue"))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isBadRequest())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]")
              .value(AppConstants.THEME_NAME_MESSAGE));

      Mockito.verify(userService, Mockito.never()).updatePreferredTheme(ColorTheme.LIGHT);
    }
  }

  @Nested
  @WithUserDetails("admin@admin.com")
  @DisplayName("User get tests")
  class UserGetTests {

    @Test
    @DisplayName("Get user details")
    public void getUserDetails() throws Exception {
      Mockito.when(userService.getDetails()).thenReturn(new User());

      mockMvc.perform(MockMvcRequestBuilders.get(Stringify.BASE_URL + "users"))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk());

      Mockito.verify(userService).getDetails();
    }

    @Test
    @DisplayName("Get user")
    public void getUser() throws Exception {
      User user = new User();
      user.setId(1L);

      Mockito.when(userService.getUser(user.getId())).thenReturn(user);

      mockMvc
          .perform(MockMvcRequestBuilders.get(Stringify.BASE_URL + "users/{userId}", user.getId())
              .contentType(MediaType.APPLICATION_JSON))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

      Mockito.verify(userService).getUser(user.getId());
    }

    @Test
    @DisplayName("Get all users")
    public void getAllUsers() throws Exception {
      User user = new User();
      user.setId(1L);
      user.setFirstName("First name");
      user.setLastName("Last name");
      user.setEmail("user@user.com");
      user.setPhoneNumber("055-5555555");
      user.setBirthDay(LocalDate.now());
      user.setPreferredTheme(ColorTheme.LIGHT);
      user.setPassword("password");
      Privilege privilege = new Privilege();
      privilege.setName("user");
      privilege.setLevel(AccessPermission.ADMIN);
      user.setPrivileges(Set.of(privilege));

      Sort sort = Sort.by("firstName").and(Sort.by("lastName")).descending();
      PageRequest pageRequest = PageRequest.of(0, 5, sort);

      Mockito.when(userService.getAllUsers(0, 5, AppDefaults.USER_DEFAULT_SORT_NAME, false))
          .thenReturn(new PageImpl<>(List.of(), pageRequest, 1));

      mockMvc.perform(MockMvcRequestBuilders.get(Stringify.BASE_URL + "users/all")
          .contentType(MediaType.APPLICATION_JSON))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1));

      Mockito.verify(userService).getAllUsers(0, 5, AppDefaults.USER_DEFAULT_SORT_NAME, false);
    }

    @Test
    @DisplayName("Get all supervised users")
    public void getAllSupervisedUsers() throws Exception {
      User user = new User();
      user.setId(1L);
      user.setFirstName("First name");
      user.setLastName("Last name");
      user.setEmail("user@user.com");
      user.setPhoneNumber("055-5555555");
      user.setBirthDay(LocalDate.now());
      user.setPreferredTheme(ColorTheme.LIGHT);
      user.setPassword("password");
      Privilege privilege = new Privilege();
      privilege.setName("user");
      privilege.setLevel(AccessPermission.ADMIN);
      user.setPrivileges(Set.of(privilege));

      Sort sort = Sort.by("firstName").and(Sort.by("lastName")).descending();
      PageRequest pageRequest = PageRequest.of(0, 5, sort);

      Mockito
          .when(userService.getAllSupervisedUsers(0, 5, AppDefaults.USER_DEFAULT_SORT_NAME, false))
          .thenReturn(new PageImpl<>(List.of(), pageRequest, 1));

      mockMvc.perform(MockMvcRequestBuilders.get(Stringify.BASE_URL + "users/supervised")
          .contentType(MediaType.APPLICATION_JSON))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1));

      Mockito.verify(userService)
          .getAllSupervisedUsers(0, 5, AppDefaults.USER_DEFAULT_SORT_NAME, false);
    }

    @Test
    @DisplayName("Get all supervised users of user")
    public void getAllSupervisedUsersOfUsers() throws Exception {
      User user = new User();
      user.setId(1L);
      user.setFirstName("First name");
      user.setLastName("Last name");
      user.setEmail("user@user.com");
      user.setPhoneNumber("055-5555555");
      user.setBirthDay(LocalDate.now());
      user.setPreferredTheme(ColorTheme.LIGHT);
      user.setPassword("password");
      Privilege privilege = new Privilege();
      privilege.setName("user");
      privilege.setLevel(AccessPermission.ADMIN);
      user.setPrivileges(Set.of(privilege));

      Sort sort = Sort.by("firstName").and(Sort.by("lastName")).descending();
      PageRequest pageRequest = PageRequest.of(0, 5, sort);

      Mockito.when(userService
          .getAllSupervisedUsersOfUser(user.getId(), 0, 5, AppDefaults.USER_DEFAULT_SORT_NAME,
              false))
          .thenReturn(new PageImpl<>(List.of(), pageRequest, 1));

      mockMvc.perform(
          MockMvcRequestBuilders.get(Stringify.BASE_URL + "users/supervised/{userId}", user.getId())
              .contentType(MediaType.APPLICATION_JSON))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(1));

      Mockito.verify(userService)
          .getAllSupervisedUsersOfUser(user.getId(), 0, 5, AppDefaults.USER_DEFAULT_SORT_NAME,
              false);
    }

    @Test
    @DisplayName("Get all supervised users results")
    public void getAllSupervisedUsersResults() throws Exception {
      Mockito.when(userService.getAllSupervisedUsersResults(5))
          .thenReturn(List.of());

      mockMvc.perform(MockMvcRequestBuilders.get(Stringify.BASE_URL + "users/supervised/results")
          .contentType(MediaType.APPLICATION_JSON))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

      Mockito.verify(userService).getAllSupervisedUsersResults(5);
    }

    @Test
    @DisplayName("Get all supervised users results of user")
    public void getAllSupervisedUsersResultsOfUser() throws Exception {
      User user = new User();
      user.setId(1L);

      Mockito.when(userService.getAllSupervisedUsersResultsOfUser(user.getId(), 5))
          .thenReturn(List.of());

      mockMvc.perform(MockMvcRequestBuilders
          .get(Stringify.BASE_URL + "users/supervised/results/{userId}", user.getId())
          .contentType(MediaType.APPLICATION_JSON))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

      Mockito.verify(userService).getAllSupervisedUsersResultsOfUser(user.getId(), 5);
    }

    @Test
    @DisplayName("Get user results by name example")
    public void getAllUsersByNameExample() throws Exception {
      Mockito.when(userService.getAllUsersByNameExample("name", 5))
          .thenReturn(List.of());

      mockMvc.perform(MockMvcRequestBuilders.get(Stringify.BASE_URL + "users/search")
          .contentType(MediaType.APPLICATION_JSON)
          .queryParam("nameExample", "name"))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk())
          .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));

      Mockito.verify(userService).getAllUsersByNameExample("name", 5);
    }
  }

  @Nested
  @WithUserDetails("admin@admin.com")
  @DisplayName("User delete tests")
  class UserDeleteTests {

    @Test
    @DisplayName("Delete user")
    public void deleteUser() throws Exception {
      Mockito.doNothing().when(userService).deleteUser(1L);

      mockMvc.perform(MockMvcRequestBuilders.delete(Stringify.BASE_URL + "users/{userId}", 1L)
          .contentType(MediaType.APPLICATION_JSON))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk());

      Mockito.verify(userService).deleteUser(1L);
    }

    @Test
    @DisplayName("Remove user supervision")
    public void removeAssociation() throws Exception {
      Mockito.doNothing().when(userService).removeSupervision(1L, 1L);

      mockMvc.perform(MockMvcRequestBuilders
          .delete(Stringify.BASE_URL + "users/{supervisorId}/supervise/{supervisedId}", 1L, 1L)
          .contentType(MediaType.APPLICATION_JSON))
          .andDo(MockMvcResultHandlers.print())
          .andExpect(MockMvcResultMatchers.status().isOk());

      Mockito.verify(userService).removeSupervision(1L, 1L);
    }

  }
}
