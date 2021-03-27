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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Documented
@Constraint(validatedBy = {})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@NotNull(message = AppConstants.CONTENT_NULL_MESSAGE)
@Size(max = AppDefaults.MAX_CONTENT_SIZE, message = AppConstants.CONTENT_LENGTH_MESSAGE)
@GroupSequence({NotNull.class, Size.class})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidContent {

  String message() default AppConstants.CONTENT_LENGTH_MESSAGE;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
