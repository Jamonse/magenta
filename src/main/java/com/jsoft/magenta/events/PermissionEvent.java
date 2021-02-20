package com.jsoft.magenta.events;

import com.jsoft.magenta.security.model.AccessPermission;

public interface PermissionEvent
{
    AccessPermission getPermission();
}
