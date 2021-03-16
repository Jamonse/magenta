package com.jsoft.magenta.events.projects;

import com.jsoft.magenta.events.AssociationEvent;
import com.jsoft.magenta.events.PermissionEvent;
import com.jsoft.magenta.security.model.AccessPermission;

public class ProjectAssociationCreationEvent extends AssociationEvent<Long> implements PermissionEvent {
    private final AccessPermission accessPermission;

    public ProjectAssociationCreationEvent(Long accountId, Long associatedUserId, AccessPermission accessPermission) {
        super(accountId, associatedUserId);
        this.accessPermission = accessPermission;
    }

    @Override
    public AccessPermission getPermission() {
        return this.accessPermission;
    }
}
