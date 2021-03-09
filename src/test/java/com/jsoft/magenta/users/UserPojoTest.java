package com.jsoft.magenta.users;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashSet;

public class UserPojoTest
{
    @Test
    @DisplayName("Create two users and check getter and equals")
    public void create()
    {
        User user1 = new User(1L, "first name", "last name", "email", "phoneNumber", "password", null,
                true, ColorTheme.LIGHT, LocalDate.now(), LocalDate.now(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        User user2 = new User(1L, "first name", "last name", "email", "phoneNumber", "password", null,
                true, ColorTheme.LIGHT, LocalDate.now(), LocalDate.now(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());

        Assertions.assertEquals(user1.getFirstName(), "first name");
        Assertions.assertEquals(user1, user2);
    }

    @Test
    @DisplayName("Update user and check getter")
    public void update()
    {
        User user1 = new User(1L, "first name", "last name", "email", "phoneNumber", "password", null,
                true, ColorTheme.LIGHT, LocalDate.now(), LocalDate.now(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        user1.setFirstName("new first name");

        Assertions.assertEquals(user1.getFirstName(), "new first name");
    }
}
