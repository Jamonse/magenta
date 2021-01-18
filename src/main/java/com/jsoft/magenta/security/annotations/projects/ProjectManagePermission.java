package com.jsoft.magenta.security.annotations.projects;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasPermission('project', 'manage')")
public @interface ProjectManagePermission
{

}
