package com.jsoft.magenta.security.annotations.users;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasPermission('user', 'write')")
public @interface UserWritePermission {

}
