package com.jsoft.magenta.events.subprojects;

import com.jsoft.magenta.events.AssociationEvent;

public class SubProjectAssociationCreationEvent extends AssociationEvent<Long> {
    public SubProjectAssociationCreationEvent(Long projectId, Long associatedUserId) {
        super(projectId, associatedUserId);
    }
}
