package com.jsoft.magenta.events.subprojects;

import com.jsoft.magenta.events.ApplicationEvent;

public class SubProjectRelatedEntityEvent extends ApplicationEvent<Long>
{
    public SubProjectRelatedEntityEvent(Long spId)
    {
        super(spId);
    }
}
