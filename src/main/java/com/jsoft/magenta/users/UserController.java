package com.jsoft.magenta.users;

import static com.jsoft.magenta.util.AppDefaults.ASCENDING_SORT;
import static com.jsoft.magenta.util.AppDefaults.PAGE_INDEX;
import static com.jsoft.magenta.util.AppDefaults.PAGE_SIZE;
import static com.jsoft.magenta.util.AppDefaults.RESULTS_COUNT;
import static com.jsoft.magenta.util.AppDefaults.USER_DEFAULT_SORT_NAME;

import com.jsoft.magenta.files.MagentaImage;
import com.jsoft.magenta.security.annotations.users.UserManagePermission;
import com.jsoft.magenta.security.annotations.users.UserWritePermission;
import com.jsoft.magenta.util.validation.annotations.ValidImage;
import com.jsoft.magenta.util.validation.annotations.ValidTheme;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Validated
@RestController
@RequestMapping("${application.url}users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  @UserWritePermission
  @ResponseStatus(HttpStatus.CREATED)
  public User createUser(
      @RequestPart(required = false) @ValidImage MultipartFile profileImage,
      @RequestBody @Valid User user
  ) {
    return this.userService.createUser(user, profileImage);
  }

  @PostMapping("{supervisorId}/supervise/{supervisedId}")
  @UserWritePermission
  public User createSupervision(
      @PathVariable Long supervisorId,
      @PathVariable Long supervisedId
  ) {
    return this.userService.createSupervision(supervisorId, supervisedId);
  }

  @PutMapping
  @UserWritePermission
  public User updateUser(@RequestBody @Valid User user) {
    return this.userService.updateUser(user);
  }

  @PatchMapping("theme")
  public User updatePreferredTheme(@RequestBody @ValidTheme String preferredTheme) {
    ColorTheme colorTheme = ColorTheme.valueOf(preferredTheme.toUpperCase());
    return this.userService.updatePreferredTheme(colorTheme);
  }

  @PatchMapping("{userId}")
  @UserWritePermission
  public MagentaImage updateUserProfileImage(
      @PathVariable Long userId,
      @RequestPart @ValidImage MultipartFile profileImage
  ) {
    return this.userService.updateUserProfileImage(userId, profileImage);
  }

  @GetMapping
  public User getDetails() {
    return this.userService.getDetails();
  }

  @GetMapping("{userId}")
  @UserManagePermission
  public User getUser(@PathVariable Long userId) {
    return this.userService.getUser(userId);
  }

  @GetMapping("all")
  @UserWritePermission
  public Page<User> getAllUsers(
      @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
      @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
      @RequestParam(required = false, defaultValue = USER_DEFAULT_SORT_NAME) String sortBy,
      @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
  ) {
    return this.userService.getAllUsers(pageIndex, pageSize, sortBy, asc);
  }

  @GetMapping("supervised")
  @UserManagePermission
  public Page<User> getAllSupervisedUsers(
      @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
      @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
      @RequestParam(required = false, defaultValue = USER_DEFAULT_SORT_NAME) String sortBy,
      @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
  ) {
    return this.userService.getAllSupervisedUsers(pageIndex, pageSize, sortBy, asc);
  }

  @GetMapping("supervised/{userId}")
  @UserWritePermission
  public Page<User> getAllSupervisedUsers(
      @PathVariable Long userId,
      @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
      @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
      @RequestParam(required = false, defaultValue = USER_DEFAULT_SORT_NAME) String sortBy,
      @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
  ) {
    return this.userService.getAllSupervisedUsersOfUser(userId, pageIndex, pageSize, sortBy, asc);
  }

  @GetMapping("supervised/results")
  @UserManagePermission
  public List<UserSearchResult> getAllSupervisedUsersResults(
      @RequestParam(required = false, defaultValue = RESULTS_COUNT) int resultsCount
  ) {
    return this.userService.getAllSupervisedUsersResults(resultsCount);
  }

  @GetMapping("supervised/results/{userId}")
  @UserWritePermission
  public List<UserSearchResult> getAllSupervisedUsersResultsOfUser(
      @PathVariable Long userId,
      @RequestParam(required = false, defaultValue = RESULTS_COUNT) int resultsCount
  ) {
    return this.userService.getAllSupervisedUsersResultsOfUser(userId, resultsCount);
  }

  @GetMapping("search")
  @UserWritePermission
  public List<UserSearchResult> getAllUsersByNameExample(
      @RequestParam String nameExample,
      @RequestParam(required = false, defaultValue = RESULTS_COUNT) int resultsCount
  ) {
    return this.userService.getAllUsersByNameExample(nameExample, resultsCount);
  }

  @DeleteMapping("{userId}/image/{imageId}")
  @UserWritePermission
  public void removeUserProfileImage(
      @PathVariable Long userId
  ) {
    this.userService.removeUserProfileImage(userId);
  }

  @DeleteMapping("{userId}")
  @UserWritePermission
  public void deleteUser(@PathVariable Long userId) {
    this.userService.deleteUser(userId);
  }

  @DeleteMapping("{supervisorId}/supervise/{supervisedId}")
  @UserWritePermission
  public void removeSupervision(
      @PathVariable Long supervisorId,
      @PathVariable Long supervisedId
  ) {
    this.userService.removeSupervision(supervisorId, supervisedId);
  }

}
