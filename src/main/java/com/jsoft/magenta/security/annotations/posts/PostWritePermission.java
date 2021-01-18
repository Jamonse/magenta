package com.jsoft.magenta.security.annotations.posts;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasPermission('post', 'write')")
public @interface PostWritePermission
{

}
