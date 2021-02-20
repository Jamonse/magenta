package com.jsoft.magenta.security.annotations.users;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasPermission('user', 'manage')")
public @interface UserManagePermission
{

}
