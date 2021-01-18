package com.jsoft.magenta.projects;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.projects.domain.Project;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;

public class ProjectPojoTest
{
    @Test
    @DisplayName("Create two projects and check getter and equals")
    public void create()
    {
        Project p1 = new Project(1L, "project", true, new Account(),
                LocalDate.now(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        Project p2 = new Project(1L, "project", true, new Account(),
                LocalDate.now(), new HashSet<>(), new HashSet<>(), new HashSet<>());

        Assertions.assertEquals(p1.getName(), "project");
        Assertions.assertTrue(p1.equals(p2));
    }

    @Test
    @DisplayName("Update projects and check getter")
    public void update()
    {
        Project p1 = new Project(1L, "project", true, new Account(),
                LocalDate.now(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        p1.setName("new name");

        Assertions.assertEquals(p1.getName(), "new name");
    }
}
