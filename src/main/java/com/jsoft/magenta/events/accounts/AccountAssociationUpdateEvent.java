package com.jsoft.magenta.events.accounts;

import com.jsoft.magenta.events.AssociationEvent;
import com.jsoft.magenta.events.PermissionEvent;
import com.jsoft.magenta.security.model.AccessPermission;

public class AccountAssociationUpdateEvent extends AssociationEvent<Long> implements PermissionEvent {
    private final AccessPermission newPermission;

    public AccountAssociationUpdateEvent(Long accountId, Long associatedUserId, AccessPermission accessPermission) {
        super(accountId, associatedUserId);
        this.newPermission = accessPermission;
    }

    @Override
    public AccessPermission getPermission() {
        return this.newPermission;
    }
}
