package com.jsoft.magenta.util.validation.validators;

import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.util.validation.annotations.ValidTheme;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ThemeNameValidator implements ConstraintValidator<ValidTheme, String> {

  @Override
  public boolean isValid(String themeName, ConstraintValidatorContext constraintValidatorContext) {
    themeName = themeName.toUpperCase();
    return AppConstants
        .THEME_NAMES
        .contains(themeName);
  }
}
