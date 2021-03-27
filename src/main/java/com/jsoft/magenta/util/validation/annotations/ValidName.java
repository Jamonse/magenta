package com.jsoft.magenta.util.validation.annotations;

import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.util.AppDefaults;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.GroupSequence;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@NotBlank(message = AppConstants.NAME_BLANK_MESSAGE)
@Size(
    min = AppDefaults.MIN_NAME_SIZE,
    max = AppDefaults.MAX_NAME_SIZE,
    message = AppConstants.NAME_LENGTH_MESSAGE
)
@GroupSequence({NotBlank.class, Size.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidName {

  String message() default AppConstants.NAME_DEFAULT_MESSAGE;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
