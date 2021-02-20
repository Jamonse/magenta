package com.jsoft.magenta.events.subprojects;

import com.jsoft.magenta.events.AssociationEvent;
import com.jsoft.magenta.subprojects.SubProject;

public class SubProjectAssociationRemovalEvent extends AssociationEvent<SubProject>
{
    public SubProjectAssociationRemovalEvent(SubProject payLoad, Long associatedUserId)
    {
        super(payLoad, associatedUserId);
    }
}
