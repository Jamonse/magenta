package com.jsoft.magenta.security.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccessPermission {
    READ(1),
    MANAGE(2),
    WRITE(3),
    ADMIN(4);

    private final int permissionLevel;
}
