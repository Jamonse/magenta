package com.jsoft.magenta.security.annotations.accounts;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasPermission('account', 'read')")
public @interface AccountReadPermission
{

}
