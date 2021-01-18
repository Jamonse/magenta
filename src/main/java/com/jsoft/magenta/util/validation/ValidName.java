package com.jsoft.magenta.util.validation;

import com.jsoft.magenta.util.AppConstants;

import javax.validation.Constraint;
import javax.validation.GroupSequence;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@NotBlank(message = AppConstants.NAME_BLANK_MESSAGE)
@Size(min = 2, max = 50, message = AppConstants.NAME_LENGTH_MESSAGE)
@GroupSequence({NotBlank.class, Size.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidName
{
    String message() default AppConstants.NAME_DEFAULT_MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
