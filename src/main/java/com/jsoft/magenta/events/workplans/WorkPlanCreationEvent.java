package com.jsoft.magenta.events.workplans;

import com.jsoft.magenta.events.ApplicationEvent;

public class WorkPlanCreationEvent extends ApplicationEvent<Long>
{
    public WorkPlanCreationEvent(Long userId)
    {
        super(userId);
    }
}
