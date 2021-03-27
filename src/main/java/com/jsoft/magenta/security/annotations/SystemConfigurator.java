package com.jsoft.magenta.security.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasPermission('config', 'admin')")
public @interface SystemConfigurator {

}
