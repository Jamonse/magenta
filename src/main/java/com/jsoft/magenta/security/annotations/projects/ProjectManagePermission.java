package com.jsoft.magenta.security.annotations.projects;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasPermission('project', 'manage')")
public @interface ProjectManagePermission {

}
