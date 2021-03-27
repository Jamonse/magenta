package com.jsoft.magenta.security.annotations.accounts;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasPermission('account', 'write')")
public @interface AccountWritePermission {

}
