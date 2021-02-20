package com.jsoft.magenta.util.validation.annotations;

import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.util.validation.validators.ThemeNameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotBlank;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ThemeNameValidator.class)
@NotBlank(message = AppConstants.PERMISSION_BLANK_MESSAGE)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTheme
{
    String message() default AppConstants.THEME_NAME_MESSAGE;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
