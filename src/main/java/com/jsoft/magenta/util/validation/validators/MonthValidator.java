package com.jsoft.magenta.util.validation.validators;

import com.jsoft.magenta.util.validation.annotations.ValidMonth;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MonthValidator implements ConstraintValidator<ValidMonth, Integer> {

  @Override
  public boolean isValid(Integer month, ConstraintValidatorContext constraintValidatorContext) {
    return month != null && month >= 1 && month <= 12;
  }
}
