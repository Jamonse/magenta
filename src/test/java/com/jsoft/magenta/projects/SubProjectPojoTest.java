package com.jsoft.magenta.projects;

import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.subprojects.SubProject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

public class SubProjectPojoTest
{
    @Test
    @DisplayName("Create two sub-projects and check getter and equals")
    public void create()
    {
        SubProject sp1 = new SubProject(1L, "sub-project", true, 10d, new Project(), new HashSet<>(), new HashSet<>());
        SubProject sp2 = new SubProject(1L, "sub-project", true, 10d, new Project(), new HashSet<>(), new HashSet<>());

        Assertions.assertEquals(sp1.getName(), "sub-project");
        Assertions.assertTrue(sp1.equals(sp2));
    }

    @Test
    @DisplayName("Update sub project and check getter")
    public void update()
    {
        SubProject sp1 = new SubProject(1L, "sub-project", true, 10d, new Project(), new HashSet<>(), new HashSet<>());
        sp1.setName("new name");

        Assertions.assertEquals(sp1.getName(), "new name");
    }


}
