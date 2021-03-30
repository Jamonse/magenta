package com.jsoft.magenta.posts;

import java.time.LocalDate;

public interface PostSearchResult {
  Long getId();
  String getTitle();
  LocalDate getCreatedAt();
}
