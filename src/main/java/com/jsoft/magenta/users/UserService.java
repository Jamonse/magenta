package com.jsoft.magenta.users;

import com.jsoft.magenta.events.PermissionEvent;
import com.jsoft.magenta.events.accounts.AccountAssociationCreationEvent;
import com.jsoft.magenta.events.projects.ProjectAssociationUpdateEvent;
import com.jsoft.magenta.events.workplans.WorkPlanCreationEvent;
import com.jsoft.magenta.exceptions.AuthorizationException;
import com.jsoft.magenta.exceptions.DuplicationException;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.exceptions.RedundantAssociationException;
import com.jsoft.magenta.files.MagentaImage;
import com.jsoft.magenta.files.MagentaImageService;
import com.jsoft.magenta.files.MagentaImageType;
import com.jsoft.magenta.security.SecurityService;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.util.AppDefaults;
import com.jsoft.magenta.util.pagination.PageRequestBuilder;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final MagentaImageService imageService;
  private final SecurityService securityService;

  public User createUser(User user, MultipartFile profileImage) {
    verifyUserUniques(user);
    user.setCreatedAt(LocalDate.now());
    user.setEnabled(true);
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    MagentaImage magentaImage = profileImage != null ?
        this.imageService.uploadImage(user.getName(), profileImage, MagentaImageType.PROFILE)
        : null;
    user.setProfileImage(magentaImage);
    return this.userRepository.save(user);
  }

  public User createSupervision(Long supervisorId, Long supervisedId) { // Find both users
    User supervisor = findUser(supervisorId);
    User supervised = findUser(supervisedId);
    AccessPermission accessPermission = findUserPermission(
        supervisor); // Verify supervision permission
    if (accessPermission == AccessPermission.READ) {
      throw new AuthorizationException("User is not authorized to supervise");
    }
    boolean exist = supervisor.getSupervisedUsers()
        .contains(supervised); // Check for supervision existence
    if (exist) {
      throw new DuplicationException("Supervision already exists");
    }
    supervisor.setSupervisedUsers(Set.of(supervised)); // Create supervision and save
    return this.userRepository.save(supervisor);
  }

  public MagentaImage updateUserProfileImage(Long userId,
      MultipartFile profileImage) { // Fetch user
    User user = findUser(userId);
    MagentaImage userImage = user.getProfileImage();
    if (userImage == null) { // User does not have profile image yet -> upload a new one
      MagentaImage magentaImage = this.imageService
          .uploadImage(user.getName(), profileImage, MagentaImageType.PROFILE);
      user.setProfileImage(magentaImage);
      this.userRepository.save(user);
      return magentaImage;
    } // User profile image exists -> perform update
    return this.imageService
        .updateImage(userImage.getId(), user.getName(), profileImage, MagentaImageType.PROFILE);
  }

  public User updateUser(User user) {
    User userToUpdate = findUser(user.getId());
    verifyUserUniques(user);
    userToUpdate.setFirstName(user.getFirstName());
    userToUpdate.setLastName(user.getLastName());
    userToUpdate.setPreferredTheme(user.getPreferredTheme());
    userToUpdate.setEmail(user.getEmail());
    userToUpdate.setPhoneNumber(user.getPhoneNumber());
    userToUpdate.setBirthDay(user.getBirthDay());
    return this.userRepository.save(userToUpdate);
  }

  public User updatePreferredTheme(ColorTheme colorTheme) {
    User user = securityService.currentUser();
    user.setPreferredTheme(colorTheme);
    return this.userRepository.save(user);
  }

  public User getUser(Long userId) {
    User supervised = findUser(userId);
    User supervisor = securityService.currentUser();
    AccessPermission accessPermission = findUserPermission(supervisor);
    switch (accessPermission) {
      case READ:
        throw new AuthorizationException("User is not authorized to get such details");
      case MANAGE:
        boolean isSupervisor = supervisor.isSupervisorOf(supervised);
        if (!isSupervisor) {
          throw new AuthorizationException("User is not authorized to get such details");
        }
      case WRITE:
      case ADMIN:
        return supervised;
      default:
        throw new AuthorizationException("User is not authorized with supervision");
    }
  }

  public User getDetails() {
    return securityService.currentUser();
  }

  public Page<User> getAllUsers(int pageIndex, int pageSize, String sortBy, boolean asc) {
    PageRequest pageRequest;
    if (sortBy.equalsIgnoreCase("name")) {
      pageRequest = PageRequestBuilder
          .buildPageRequest(pageIndex, pageSize, AppDefaults.USER_DEFAULT_SORT, asc);
    } else {
      pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
    }
    return this.userRepository.findAll(pageRequest);
  }

  public Page<User> getAllSupervisedUsers(int pageIndex, int pageSize, String sortBy, boolean asc) {
    User user = securityService.currentUser();
    return getSupervisedUsers(user, pageIndex, pageSize, sortBy, asc);
  }

  public Page<User> getAllSupervisedUsersOfUser(Long userId, int pageIndex, int pageSize,
      String sortBy,
      boolean asc) {
    User user = findUser(userId);
    return getSupervisedUsers(user, pageIndex, pageSize, sortBy, asc);
  }

  public List<UserSearchResult> getAllSupervisedUsersResults(int resultsCount) {
    User user = securityService.currentUser();
    return getSupervisedUsersResults(user, resultsCount);
  }

  public List<UserSearchResult> getAllSupervisedUsersResultsOfUser(Long userId, int resultsCount) {
    User user = findUser(userId);
    return getSupervisedUsersResults(user, resultsCount);
  }

  public List<UserSearchResult> getAllUsersByNameExample(String nameExample, int resultsCount) {
    PageRequest pageRequest = PageRequestBuilder
        .buildPageRequest(0, resultsCount, AppDefaults.USER_DEFAULT_SORT,
            false);
    return this.userRepository.findAllByNameExample(nameExample, pageRequest);
  }

  public void removeUserProfileImage(Long userId) {
    User user = findUser(userId);
    Long imageId = user.getProfileImage().getId();
    user.setProfileImage(null);
    this.userRepository.save(user);
    this.imageService.removeImage(imageId, MagentaImageType.PROFILE);
  }

  public void deleteUser(Long userId) {
    findUser(userId);
    this.userRepository.deleteById(userId);
  }

  public void removeSupervision(Long supervisorId, Long supervisedId) {
    User supervisor = findUser(supervisorId);
    boolean foundAndRemoved = supervisor
        .getSupervisedUsers()
        .removeIf(user -> user.getId().equals(supervisedId));
    if (!foundAndRemoved) {
      throw new NoSuchElementException("Supervised user not found");
    }
  }

  @EventListener
  public void handleProjectAssociationUpdateEvent(
      ProjectAssociationUpdateEvent associationUpdateEvent) {
    User user = findUser(associationUpdateEvent.getAssociatedUserId());
    AccessPermission accessPermission = user.getProjectPermission();
    handleAssociationEvent(accessPermission, associationUpdateEvent);
  }

  @EventListener
  public void handleAccountAssociationCreationEvent(
      AccountAssociationCreationEvent associationCreationEvent) { //
    // Find user account permission
    User user = findUser(associationCreationEvent.getAssociatedUserId());
    AccessPermission accessPermission = user.getAccountsPermission();
    handleAssociationEvent(accessPermission, associationCreationEvent);
  }

  @EventListener
  public void handleWorkPlanCreationEvent(WorkPlanCreationEvent creationEvent) {
    Long userId = creationEvent.getPayload();
    isUserExists(userId);
  }

  private void handleAssociationEvent(AccessPermission userPermission,
      PermissionEvent permissionEvent) {
    if (userPermission == AccessPermission.ADMIN) // Association creation with admin is redundant
    {
      throw new RedundantAssociationException("Association of admin is redundant");
    }
    int userPermissionLevel = userPermission.getPermissionLevel();
    int requestedPermission = permissionEvent.getPermission().getPermissionLevel();
    if (userPermissionLevel
        < requestedPermission) // Verify that association permission is allowed for user
    {
      throw new AuthorizationException("User is not authorized to handle such association");
    }
  }

  private Page<User> getSupervisedUsers(User user, int pageIndex, int pageSize, String sortBy,
      boolean asc) {
    PageRequest pageRequest;
    if (sortBy.equalsIgnoreCase("name")) {
      pageRequest = PageRequestBuilder
          .buildPageRequest(pageIndex, pageSize, AppDefaults.USER_DEFAULT_SORT, asc);
    } else {
      pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
    }
    Page<User> results = this.userRepository
        .findSupervisedUsersBySupervisorId(user.getId(), pageRequest);
    return new PageImpl<>(results.getContent(), pageRequest, results.getTotalElements());
  }

  private List<UserSearchResult> getSupervisedUsersResults(User user, int resultsCount) {
    PageRequest pageRequest = PageRequestBuilder
        .buildPageRequest(0, resultsCount, AppDefaults.USER_DEFAULT_SORT,
            false);
    return this.userRepository.findSupervisedUsersResultsBySupervisorId(user.getId(), pageRequest);
  }

  private void verifyUserUniques(User user) {
    boolean emailExist = this.userRepository.existsByEmail(user.getEmail());
    if (emailExist) {
      throw new DuplicationException(
          String.format("User with email address %s already exists", user.getEmail()));
    }
    boolean phoneNumberExist = this.userRepository
        .existsByPhoneNumber(user.getPhoneNumber());
    if (phoneNumberExist) {
      throw new DuplicationException(
          String.format("User with phone number %s already exists", user.getPhoneNumber()));
    }
  }

  private User findUser(Long userId) {
    return this.userRepository
        .findById(userId)
        .orElseThrow(() -> new NoSuchElementException("User not found"));
  }

  private void isUserExists(Long userId) {
    boolean exists = this.userRepository.existsById(userId);
    if (!exists) {
      throw new NoSuchElementException("User not found");
    }
  }

  private AccessPermission findUserPermission(User user) {
    return findPermission(user, AppConstants.USER_PERMISSION).getSecond();
  }

  private Pair<Long, AccessPermission> findPermission(User user, String entity) {
    String entityName = entity.toLowerCase();
    AccessPermission accessPermission = user.getPrivileges().stream()
        .filter(privilege -> privilege.getName().equals(entityName))
        .map(Privilege::getLevel)
        .findFirst()
        .orElse(AccessPermission.READ);
    return Pair.of(user.getId(), accessPermission);
  }

}
