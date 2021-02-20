package com.jsoft.magenta.worktimes;

import com.jsoft.magenta.subprojects.SubProject;
import com.jsoft.magenta.users.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

public class WorkTimePojoTest
{
    @Test
    @DisplayName("Create work time and check getter and equals")
    public void create()
    {
        WorkTime wt1 = new WorkTime(1L, LocalDate.now(), LocalTime.now(), LocalTime.now(), new User(),
                new SubProject(), 10D, "note");
        WorkTime wt2 = new WorkTime(1L, LocalDate.now(), LocalTime.now(), LocalTime.now(), new User(),
                new SubProject(), 10D, "note");

        Assertions.assertEquals(wt1.getNote(), "note");
        Assertions.assertEquals(wt2, wt1);
    }

    @Test
    @DisplayName("Update note and check getter")
    public void update()
    {
        WorkTime wt1 = new WorkTime(1L, LocalDate.now(), LocalTime.now(), LocalTime.now(), new User(),
                new SubProject(), 10D, "note");

        wt1.setNote("new note");
        Assertions.assertEquals(wt1.getNote(), "new note");
    }

}
