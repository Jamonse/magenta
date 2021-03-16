package com.jsoft.magenta.notes;

import java.time.LocalDateTime;

public interface UserNoteSearchResult {
    Long getId();

    String getTitle();

    LocalDateTime getTakenAt();

    LocalDateTime getRemindAt();
}
