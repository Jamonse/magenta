package com.jsoft.magenta.util.validation;

import org.springframework.util.Assert;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PositiveAmountValidator implements ConstraintValidator<PositiveNumber, Number>
{
    @Override
    public boolean isValid(Number number, ConstraintValidatorContext constraintValidatorContext)
    {
        Assert.notNull(number, "Validated number must not be null");
        if(number instanceof Double)
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
