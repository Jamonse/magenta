package com.jsoft.magenta.util.validation.validators;

import com.jsoft.magenta.util.validation.annotations.PositiveNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PositiveAmountValidator implements ConstraintValidator<PositiveNumber, Number>
{
    @Override
    public boolean isValid(Number number, ConstraintValidatorContext constraintValidatorContext)
    {
        if(number == null)
            return true;
        else if(number instanceof Double)
            return ((Double) number) > 0;
        else if(number instanceof Float)
            return ((Float) number) > 0;
        else if(number instanceof Integer)
            return ((Integer) number) > 0;
        else if(number instanceof Long)
            return ((Long) number) > 0;
        else if(number instanceof Short)
            return ((Short) number) > 0;
        else if(number instanceof Byte)
            return ((Byte) number) > 0;
        else
            return false;
    }
}
