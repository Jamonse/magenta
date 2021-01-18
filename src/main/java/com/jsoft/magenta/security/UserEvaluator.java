package com.jsoft.magenta.security;

import com.jsoft.magenta.security.model.UserPrincipal;
import com.jsoft.magenta.users.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Evaluates and extracts user details from security context
 */
@Slf4j
public class UserEvaluator
{
    /**
     * Returns the current logged in user id
     * @return the current logged in user id
     */
    public static Long currentUserId()
    {
        return currentUser().getId();
    }

    public static String currentUserName()
    {
        return currentUser().getName();
    }

    public static User currentUser()
    { // Get authentication from security context holder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof UserPrincipal)
        { // Valid authentication and principal type
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User currentUser = userPrincipal.getUser(); // Extract the user id from the principal
            if(currentUser == null) // User without id initialized
                throw new IllegalStateException("Uninitialized user in authentication");
            return currentUser;
        } // Unsupported user principal in authentication or an empty authentication
        log.error(
                "Extraction of user from an unsupported user principal type in authentication" +
                        " or an empty authentication was received from security context holder");
        throw new IllegalStateException("Unsupported user principal type or an empty authentication");
    }


}
