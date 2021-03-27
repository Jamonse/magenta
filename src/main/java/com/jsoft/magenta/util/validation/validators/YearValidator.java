package com.jsoft.magenta.util.validation.validators;

import com.jsoft.magenta.util.validation.annotations.ValidYear;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class YearValidator implements ConstraintValidator<ValidYear, Integer> {

  @Override
  public boolean isValid(Integer year, ConstraintValidatorContext constraintValidatorContext) {
    return year != null && year >= 1970 && year <= 3000;
  }
}
