package com.jsoft.magenta.security.annotations.users;

import com.jsoft.magenta.security.annotations.SupervisionValidator;
import com.jsoft.magenta.util.AppConstants;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SupervisionValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SupervisorOrOwner
{
    String message() default AppConstants.SUPERVISOR_OR_OWNER_MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
