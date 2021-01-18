package com.jsoft.magenta.security;

import com.jsoft.magenta.exceptions.UnsupportedPermissionLevelException;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.security.model.CustomGrantedAuthority;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator
{
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission)
    {
        if(authentication != null && (targetDomainObject instanceof String) && (permission instanceof String))
        { // Valid input
            String target = ((String) targetDomainObject).toLowerCase();
            String permissionLevel = ((String) permission).toLowerCase();
            // Search for valid permission
            return authentication.getAuthorities().stream() // Stream authorities
                    .filter(privilege -> privilege instanceof CustomGrantedAuthority) // Check for compatibility
                    .map(authority -> (CustomGrantedAuthority) authority) // Cast to CustomGrantedAuthority
                    .anyMatch(authority -> // Look for matching permission with equal or greater level
                            authority.getAuthority().toLowerCase().equals(target) &&
                            authority.getPrivilege().getLevel().getPermissionLevel() >= resolvePermission(permissionLevel));
        } // Invalid input
        return false;
    }

    @Override
    public boolean hasPermission(
            Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }

    private int resolvePermission(String permission)
    {
        permission = permission.toUpperCase().trim();

        if(permission.equals(AccessPermission.READ.name()))
            return 1;
        else if(permission.equals(AccessPermission.MANAGE.name()))
            return 2;
        else if(permission.equals(AccessPermission.WRITE.name()))
            return 3;
        else if (permission.equals(AccessPermission.ADMIN.name()))
            return 4;
        else
            throw new UnsupportedPermissionLevelException("Permission level specified is unsupported");
    }
}
