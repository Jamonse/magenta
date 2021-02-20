package com.jsoft.magenta.security.annotations;

import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.security.UserEvaluator;
import com.jsoft.magenta.security.annotations.users.SupervisorOrOwner;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.users.UserRepository;
import com.jsoft.magenta.util.AppConstants;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class SupervisionValidator implements ConstraintValidator<SupervisorOrOwner, Long>
{
    @Override
    public boolean isValid(Long userId, ConstraintValidatorContext constraintValidatorContext)
    {
        User supervisor = UserEvaluator.currentUser();
        boolean admin = supervisor.isAdminOf(AppConstants.USER_PERMISSION);
        if(admin)
            return true;
        if(supervisor.getId().equals(userId))
            return true;
        return supervisor.isSupervisorOf(userId);
    }
}
