package com.jsoft.magenta.util.validation.validators;

import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.util.validation.annotations.ValidHoursAmount;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class HoursAmountValidator implements ConstraintValidator<ValidHoursAmount, Double> {

  @Override
  public boolean isValid(Double amountOfHours,
      ConstraintValidatorContext constraintValidatorContext) {
    if (amountOfHours == null) {
      return true;
    }
    return amountOfHours <= AppConstants.HOURS_IN_DAY;
  }
}
