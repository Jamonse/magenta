package com.jsoft.magenta.notes;

import com.jsoft.magenta.users.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class NotePojoTest {

  @Test
  @DisplayName("Create two notes and check getter and equal")
  public void create() {
    UserNote note1 = new UserNote(1L, new User(), "title",
        "description", LocalDateTime.now(), LocalDateTime.now());
    UserNote note2 = new UserNote(1L, new User(), "title",
        "description", LocalDateTime.now(), LocalDateTime.now());

    Assertions.assertEquals(note1.getTitle(), "title");
    Assertions.assertEquals(note1, note2);
  }

  @Test
  @DisplayName("update note and check getter")
  public void update() {
    UserNote note1 = new UserNote(1L, new User(), "title",
        "description", LocalDateTime.now(), LocalDateTime.now());
    note1.setTitle("new title");

    Assertions.assertEquals(note1.getTitle(), "new title");
  }
}
