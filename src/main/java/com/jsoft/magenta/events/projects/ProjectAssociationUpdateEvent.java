package com.jsoft.magenta.events.projects;

import com.jsoft.magenta.events.AssociationEvent;
import com.jsoft.magenta.events.PermissionEvent;
import com.jsoft.magenta.security.model.AccessPermission;

public class ProjectAssociationUpdateEvent extends AssociationEvent<Long> implements PermissionEvent {
    private final AccessPermission newPermission;

    public ProjectAssociationUpdateEvent(Long projectId, Long associatedUserId, AccessPermission newPermission) {
        super(projectId, associatedUserId);
        this.newPermission = newPermission;
    }

    @Override
    public AccessPermission getPermission() {
        return this.newPermission;
    }
}
