package com.jsoft.magenta.security.annotations;

import com.jsoft.magenta.security.SecurityService;
import com.jsoft.magenta.security.annotations.users.SupervisorOrOwner;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.util.AppConstants;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SupervisionValidator implements ConstraintValidator<SupervisorOrOwner, Long> {

  private final SecurityService securityService;

  @Override
  public boolean isValid(Long userId, ConstraintValidatorContext constraintValidatorContext) {
    User supervisor = securityService.currentUser();
    boolean admin = supervisor.isAdminOf(AppConstants.USER_PERMISSION);
    if (admin) {
      return true;
    }
    if (supervisor.getId().equals(userId)) {
      return true;
    }
    return supervisor.isSupervisorOf(userId);
  }
}
