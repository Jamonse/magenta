package com.jsoft.magenta.util.validation.annotations;

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
@NotBlank(message = AppConstants.TITLE_BLANK_MESSAGE)
@Size(min = 2, max = 50, message = AppConstants.TITLE_LENGTH_MESSAGE)
@GroupSequence({NotBlank.class, Size.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTitle
{
    String message() default AppConstants.TITLE_DEFAULT_MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
