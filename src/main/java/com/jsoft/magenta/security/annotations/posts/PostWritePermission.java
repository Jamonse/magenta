package com.jsoft.magenta.security.annotations.posts;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.security.access.prepost.PreAuthorize;

@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasPermission('post', 'write')")
public @interface PostWritePermission {

}
