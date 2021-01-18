package com.jsoft.magenta.security;

import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.security.model.CustomGrantedAuthority;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.security.model.UserPrincipal;
import com.jsoft.magenta.users.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Set;

public class PermissionEvaluatorTest
{
    @Test
    @DisplayName("Check permission with equal permission")
    public void equalPermission()
    {
        PermissionEvaluator permissionEvaluator = new CustomPermissionEvaluator();
        User user = new User();
        Privilege privilege = new Privilege();
        privilege.setName("account");
        privilege.setLevel(AccessPermission.READ);
        user.setPrivileges(Set.of(privilege));
        UserPrincipal principal = new UserPrincipal(user);
        CustomGrantedAuthority grantedAuthority = new CustomGrantedAuthority(privilege);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principal, "", Set.of(grantedAuthority));

        boolean hasPermission = permissionEvaluator.hasPermission(authentication, "account", "read");

        Assertions.assertTrue(hasPermission);
    }

    @Test
    @DisplayName("Check permission with higher permission than expected - should pass")
    public void higherPermission()
    {
        PermissionEvaluator permissionEvaluator = new CustomPermissionEvaluator();
        User user = new User();
        Privilege privilege = new Privilege();
        privilege.setName("account");
        privilege.setLevel(AccessPermission.MANAGE);
        user.setPrivileges(Set.of(privilege));
        UserPrincipal principal = new UserPrincipal(user);
        CustomGrantedAuthority grantedAuthority = new CustomGrantedAuthority(privilege);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principal, "", Set.of(grantedAuthority));

        boolean hasPermission = permissionEvaluator.hasPermission(authentication, "account", "read");

        Assertions.assertTrue(hasPermission);
    }

    @Test
    @DisplayName("Check permission with lower permission than expected - should fail")
    public void lowerPermission()
    {
        PermissionEvaluator permissionEvaluator = new CustomPermissionEvaluator();
        User user = new User();
        Privilege privilege = new Privilege();
        privilege.setName("account");
        privilege.setLevel(AccessPermission.READ);
        user.setPrivileges(Set.of(privilege));
        UserPrincipal principal = new UserPrincipal(user);
        CustomGrantedAuthority grantedAuthority = new CustomGrantedAuthority(privilege);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principal, "", Set.of(grantedAuthority));

        boolean hasPermission = permissionEvaluator.hasPermission(authentication, "account", "manage");

        Assertions.assertFalse(hasPermission);
    }

    @Test
    @DisplayName("Check permission that is not granted at all - should fail")
    public void notGrantedPermission()
    {
        PermissionEvaluator permissionEvaluator = new CustomPermissionEvaluator();
        User user = new User();
        Privilege privilege = new Privilege();
        privilege.setName("account");
        privilege.setLevel(AccessPermission.READ);
        user.setPrivileges(Set.of(privilege));
        UserPrincipal principal = new UserPrincipal(user);
        CustomGrantedAuthority grantedAuthority = new CustomGrantedAuthority(privilege);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principal, "", Set.of(grantedAuthority));

        boolean hasPermission = permissionEvaluator.hasPermission(authentication, "project", "read");

        Assertions.assertFalse(hasPermission);
    }

    @Test
    @DisplayName("Check permission with objects that are not strings - should fail")
    public void notStrings()
    {
        PermissionEvaluator permissionEvaluator = new CustomPermissionEvaluator();
        User user = new User();
        Privilege privilege = new Privilege();
        privilege.setName("account");
        privilege.setLevel(AccessPermission.READ);
        user.setPrivileges(Set.of(privilege));
        UserPrincipal principal = new UserPrincipal(user);
        CustomGrantedAuthority grantedAuthority = new CustomGrantedAuthority(privilege);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principal, "", Set.of(grantedAuthority));

        boolean hasPermission = permissionEvaluator.hasPermission(authentication, new Object(), new Object());

        Assertions.assertFalse(hasPermission);
    }
}
