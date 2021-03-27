package com.jsoft.magenta.security.annotations.users;

import com.jsoft.magenta.security.annotations.SupervisionValidator;
import com.jsoft.magenta.util.AppConstants;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = SupervisionValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SupervisorOrOwner {

  String message() default AppConstants.SUPERVISOR_OR_OWNER_MESSAGE;

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
