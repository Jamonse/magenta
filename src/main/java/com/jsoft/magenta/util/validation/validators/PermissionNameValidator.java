package com.jsoft.magenta.util.validation.validators;

import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.util.validation.annotations.ValidPermission;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PermissionNameValidator implements ConstraintValidator<ValidPermission, String> {

  @Override
  public boolean isValid(String permissionLevelName,
      ConstraintValidatorContext constraintValidatorContext) {
    return AppConstants
        .PERMISSION_LEVEL_NAMES
        .contains(permissionLevelName.toUpperCase());
  }
}
