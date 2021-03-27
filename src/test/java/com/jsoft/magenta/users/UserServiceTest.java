package com.jsoft.magenta.users;

import com.jsoft.magenta.exceptions.DuplicationException;
import com.jsoft.magenta.security.SecurityService;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.util.AppDefaults;
import java.time.LocalDate;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;

public class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private SecurityService securityService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @BeforeEach
  private void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Nested
  @DisplayName("User creation tests")
  class UserCreationTests {

    @Test
    @DisplayName("Create user")
    public void createUser() {
      User user = new User();
      user.setFirstName("First name");
      user.setLastName("Last name");
      user.setEmail("user@user.com");
      user.setPhoneNumber("055-5555555");
      user.setBirthDay(LocalDate.now());
      user.setPreferredTheme(ColorTheme.LIGHT);
      user.setPassword("password");

      Mockito.when(userRepository.save(user)).thenReturn(user);

      User savedUser = userService.createUser(user, null);

      Assertions.assertThat(savedUser)
          .isNotNull()
          .extracting("enabled")
          .isEqualTo(true);
      Assertions.assertThat(savedUser)
          .extracting("createdAt")
          .isNotNull();

      Mockito.verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Create user with existing email address - should throw exception")
    public void createUserWithExistingEmail() {
      User user = new User();
      user.setFirstName("First name");
      user.setLastName("Last name");
      user.setEmail("user@user.com");
      user.setPhoneNumber("055-5555555");
      user.setBirthDay(LocalDate.now());
      user.setPreferredTheme(ColorTheme.LIGHT);
      user.setPassword("password");

      Mockito.when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
      Mockito.when(userRepository.save(user)).thenReturn(user);

      Assertions.assertThatThrownBy(() -> userService.createUser(user, null))
          .isInstanceOf(DuplicationException.class)
          .hasMessage(String.format("User with email address %s already exists", user.getEmail()));

      Mockito.verify(userRepository).existsByEmail(user.getEmail());
      Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    @Test
    @DisplayName("Create user with existing phone number - should throw exception")
    public void createUserWithExistingPhoneNumber() {
      User user = new User();
      user.setFirstName("First name");
      user.setLastName("Last name");
      user.setEmail("user@user.com");
      user.setPhoneNumber("055-5555555");
      user.setBirthDay(LocalDate.now());
      user.setPreferredTheme(ColorTheme.LIGHT);
      user.setPassword("password");

      Mockito.when(userRepository.existsByPhoneNumber(user.getPhoneNumber())).thenReturn(true);
      Mockito.when(userRepository.save(user)).thenReturn(user);

      Assertions.assertThatThrownBy(() -> userService.createUser(user, null))
          .isInstanceOf(DuplicationException.class)
          .hasMessage(
              String.format("User with phone number %s already exists", user.getPhoneNumber()));

      Mockito.verify(userRepository).existsByPhoneNumber(user.getPhoneNumber());
      Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    @Test
    @DisplayName("Create supervision")
    public void createSupervision() {
      User user = new User();
      user.setId(1L);
      user.setFirstName("First name");
      user.setLastName("Last name");
      user.setEmail("user@user.com");
      user.setPhoneNumber("055-5555555");
      user.setBirthDay(LocalDate.now());
      user.setPreferredTheme(ColorTheme.LIGHT);
      user.setPassword("password");
      user.setSupervisedUsers(Set.of());
      User supervised = new User();
      Privilege privilege = new Privilege();
      privilege.setName(AppConstants.USER_PERMISSION);
      privilege.setLevel(AccessPermission.ADMIN);
      Set<Privilege> privileges = new HashSet<>();
      privileges.add(privilege);
      user.setPrivileges(privileges);
      supervised.setId(2L);
      supervised.setFirstName("First name");
      supervised.setLastName("Last name");
      supervised.setEmail("super@super.com");
      supervised.setPhoneNumber("055-5555554");
      supervised.setBirthDay(LocalDate.now());
      supervised.setPreferredTheme(ColorTheme.LIGHT);
      supervised.setPassword("password");

      Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
      Mockito.when(userRepository.findById(supervised.getId())).thenReturn(Optional.of(supervised));
      Mockito.when(userRepository.save(user)).thenReturn(user);

      User savedUser = userService.createSupervision(user.getId(), supervised.getId());

      Assertions.assertThat(savedUser.getSupervisedUsers())
          .isNotNull()
          .isNotEmpty()
          .hasSize(1)
          .contains(supervised);

      Mockito.verify(userRepository).findById(user.getId());
      Mockito.verify(userRepository).findById(supervised.getId());
      Mockito.verify(userRepository).save(user);
    }
  }

  @Nested
  @DisplayName("User update tests")
  class UserUpdateTests {

    @Test
    @DisplayName("Update user")
    public void updateUser() {
      User user = new User();
      user.setId(1L);
      user.setFirstName("First name");
      user.setLastName("Last name");
      user.setEmail("user@user.com");
      user.setPhoneNumber("055-5555555");
      user.setBirthDay(LocalDate.now());
      user.setPreferredTheme(ColorTheme.LIGHT);
      user.setPassword("password");

      Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
      Mockito.when(userRepository.save(user)).thenReturn(user);

      User savedUser = userService.updateUser(user);

      Assertions.assertThat(savedUser).isEqualTo(user);

      Mockito.verify(userRepository).findById(user.getId());
    }

    @Test
    @DisplayName("Update user with existing email address - should throw exception")
    public void updateUserWithExistingEmail() {
      User user = new User();
      user.setId(1L);
      user.setFirstName("First name");
      user.setLastName("Last name");
      user.setEmail("user@user.com");
      user.setPhoneNumber("055-5555555");
      user.setBirthDay(LocalDate.now());
      user.setPreferredTheme(ColorTheme.LIGHT);
      user.setPassword("password");

      Mockito.when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
      Mockito.when(userRepository.save(user)).thenReturn(user);

      Assertions.assertThatThrownBy(() -> userService.createUser(user, null))
          .isInstanceOf(DuplicationException.class)
          .hasMessage(String.format("User with email address %s already exists", user.getEmail()));

      Mockito.verify(userRepository).existsByEmail(user.getEmail());
      Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    @Test
    @DisplayName("Update user with existing phone number - should throw exception")
    public void updateUserWithExistingPhoneNumber() {
      User user = new User();
      user.setId(1L);
      user.setFirstName("First name");
      user.setLastName("Last name");
      user.setEmail("user@user.com");
      user.setPhoneNumber("055-5555555");
      user.setBirthDay(LocalDate.now());
      user.setPreferredTheme(ColorTheme.LIGHT);
      user.setPassword("password");

      Mockito.when(userRepository.existsByPhoneNumber(user.getPhoneNumber())).thenReturn(true);
      Mockito.when(userRepository.save(user)).thenReturn(user);

      Assertions.assertThatThrownBy(() -> userService.createUser(user, null))
          .isInstanceOf(DuplicationException.class)
          .hasMessage(
              String.format("User with phone number %s already exists", user.getPhoneNumber()));

      Mockito.verify(userRepository).existsByPhoneNumber(user.getPhoneNumber());
      Mockito.verify(userRepository, Mockito.never()).save(user);
    }

    @Test
    @DisplayName("Update preferred theme")
    public void updatePreferredTheme() {
      User user = new User();
      user.setId(1L);
      user.setFirstName("First name");
      user.setLastName("Last name");
      user.setEmail("user@user.com");
      user.setPhoneNumber("055-5555555");
      user.setBirthDay(LocalDate.now());
      user.setPreferredTheme(ColorTheme.LIGHT);
      user.setPassword("password");

      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(userRepository.save(user)).thenReturn(user);

      userService.updatePreferredTheme(ColorTheme.DARK);

      Assertions.assertThat(user)
          .extracting("preferredTheme")
          .isEqualTo(ColorTheme.DARK);

      Mockito.verify(userRepository).save(user);
    }
  }

  @Nested
  @WithUserDetails("admin@admin.com")
  @DisplayName("User get tests")
  class UserGetTests {

    @Test
    @DisplayName("Get user")
    public void getUser() {
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

      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

      userService.getUser(1L);

      Mockito.verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Get user details")
    public void getUserDetails() {
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

      Mockito.when(securityService.currentUser()).thenReturn(user);

      userService.getDetails();
    }

    @Test
    @DisplayName("Get all users")
    public void getAllUsers() {
      Sort sort = Sort.by(AppDefaults.USER_DEFAULT_SORT).descending();
      PageRequest pageRequest = PageRequest.of(0, 5, sort);
      Page<User> users = new PageImpl<>(List.of(new User()), pageRequest, 1);

      Mockito.when(userRepository.findAll(pageRequest)).thenReturn(users);

      userService.getAllUsers(0, 5, AppDefaults.USER_DEFAULT_SORT_NAME, false);

      Mockito.verify(userRepository).findAll(pageRequest);
    }

    @Test
    @DisplayName("Get all supervised users")
    public void getAllSupervisedUsers() {
      User user = new User();
      user.setId(1L);
      User supervised = new User();
      supervised.setFirstName("first name");
      supervised.setLastName("last name");
      Set<User> users = new HashSet<>();
      user.setSupervisedUsers(users);
      Sort sort = Sort.by("firstName").and(Sort.by("lastName")).descending();
      PageRequest pageRequest = PageRequest.of(0, 5, sort);

      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(userRepository.findSupervisedUsersBySupervisorId(user.getId(), pageRequest))
          .thenReturn(new PageImpl<>(List.of(new User()), pageRequest, 1));

      userService
          .getAllSupervisedUsers(pageRequest.getPageNumber(), pageRequest.getPageSize(), "name",
              false);
    }

    @Test
    @DisplayName("Get all supervised users of a user")
    public void getAllSupervisedUsersOfUser() {
      User user = new User();
      user.setId(1L);
      User supervised = new User();
      supervised.setFirstName("first name");
      supervised.setLastName("last name");
      Set<User> users = new HashSet<>();
      user.setSupervisedUsers(users);
      Sort sort = Sort.by("firstName").and(Sort.by("lastName")).descending();
      PageRequest pageRequest = PageRequest.of(0, 5, sort);

      Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
      Mockito.when(userRepository.findSupervisedUsersBySupervisorId(1L, pageRequest))
          .thenReturn(new PageImpl<>(List.of(new User()), pageRequest, 1));

      userService.getAllSupervisedUsersOfUser(user.getId(), 0, 5, "name", false);

      Mockito.verify(userRepository).findById(user.getId());
      Mockito.verify(userRepository).findSupervisedUsersBySupervisorId(1L, pageRequest);
    }

    @Test
    @DisplayName("Get supervised users results")
    public void getAllSupervisedResults() {
      User user = new User();
      user.setId(1L);
      Sort sort = Sort.by("firstName").and(Sort.by("lastName")).descending();
      PageRequest pageRequest = PageRequest.of(0, 5, sort);

      Mockito.when(securityService.currentUser()).thenReturn(user);
      Mockito.when(userRepository.findSupervisedUsersResultsBySupervisorId(1L, pageRequest))
          .thenReturn(List.of());

      userService.getAllSupervisedUsersResults(5);

      Mockito.verify(userRepository).findSupervisedUsersResultsBySupervisorId(1L, pageRequest);
    }

    @Test
    @DisplayName("Get supervised users results of user")
    public void getAllSupervisedResultsOfUser() {
      User user = new User();
      user.setId(1L);
      Sort sort = Sort.by("firstName").and(Sort.by("lastName")).descending();
      PageRequest pageRequest = PageRequest.of(0, 5, sort);

      Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
      Mockito
          .when(userRepository.findSupervisedUsersResultsBySupervisorId(user.getId(), pageRequest))
          .thenReturn(List.of());

      userService.getAllSupervisedUsersResultsOfUser(user.getId(), 5);

      Mockito.verify(userRepository).findById(user.getId());
      Mockito.verify(userRepository)
          .findSupervisedUsersResultsBySupervisorId(user.getId(), pageRequest);
    }

    @Test
    @DisplayName("Get users results by name example")
    public void getUsersResultsByNameExample() {
      Sort sort = Sort.by("firstName").and(Sort.by("lastName")).descending();
      PageRequest pageRequest = PageRequest.of(0, 5, sort);

      Mockito.when(userRepository.findAllByNameExample("name", pageRequest))
          .thenReturn(List.of());

      userService.getAllUsersByNameExample("name", 5);

      Mockito.verify(userRepository).findAllByNameExample("name", pageRequest);
    }
  }

  @Nested
  @WithUserDetails("admin@admin.com")
  @DisplayName("User delete tests")
  class UserDeleteTests {

    @Test
    @DisplayName("Delete user")
    public void deleteUser() {
      User user = new User();
      user.setId(1L);

      Mockito.when(userRepository.findById(user.getId()))
          .thenReturn(Optional.of(user));
      Mockito.doNothing().when(userRepository).deleteById(user.getId());

      userService.deleteUser(user.getId());

      Mockito.verify(userRepository).findById(user.getId());
      Mockito.verify(userRepository).deleteById(user.getId());
    }

    @Test
    @DisplayName("Remove supervision")
    public void removeSupervision() {
      User supervisor = new User();
      supervisor.setId(1L);
      User supervised = new User();
      supervised.setId(1L);
      Set<User> users = new HashSet<>();
      users.add(supervised);
      supervisor.setSupervisedUsers(users);

      Mockito.when(userRepository.findById(supervisor.getId()))
          .thenReturn(Optional.of(supervisor));

      userService.removeSupervision(supervisor.getId(), supervised.getId());

      Mockito.verify(userRepository).findById(supervisor.getId());
    }

  }
}
