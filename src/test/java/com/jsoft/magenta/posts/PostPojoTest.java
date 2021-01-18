package com.jsoft.magenta.posts;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Date;

public class PostPojoTest
{
    @Test
    @DisplayName("Create two posts and check getter and equals")
    public void create()
    {
        Post p1 = new Post(1L, "title", "createdBy",
                "content", "image", LocalDateTime.now());
        Post p2 = new Post(1L, "title", "createdBy",
                "content", "image", LocalDateTime.now());

        Assertions.assertEquals(p1.getTitle(), "title");
        Assertions.assertEquals(p1, p2);
    }

    @Test
    @DisplayName("Update post and check getter")
    public void update()
    {
        Post p1 = new Post(1L, "title", "createdBy",
                "content", "image", LocalDateTime.now());
        p1.setTitle("new title");

        Assertions.assertEquals(p1.getTitle(), "new title");
    }

}
