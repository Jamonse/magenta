package com.jsoft.magenta.workplans;

import com.jsoft.magenta.projects.domain.SubProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class WorkPlanPojoTest
{
    @Test
    @DisplayName("Create two work plans and check getter and equal")
    public void create()
    {
        WorkPlan wp1 = new WorkPlan(1L, "title", LocalDateTime.now(),
                LocalDateTime.now(), new SubProject());
        WorkPlan wp2 = new WorkPlan(1L, "title", LocalDateTime.now(),
                LocalDateTime.now(), new SubProject());

        Assertions.assertEquals(wp1.getTitle(), "title");
        Assertions.assertEquals(wp1, wp2);
    }

    @Test
    @DisplayName("Update work plan and check getter")
    public void update()
    {
        WorkPlan wp1 = new WorkPlan(1L, "title", LocalDateTime.now(),
                LocalDateTime.now(), new SubProject());
        wp1.setTitle("new title");

        Assertions.assertEquals(wp1.getTitle(), "new title");
    }
}
