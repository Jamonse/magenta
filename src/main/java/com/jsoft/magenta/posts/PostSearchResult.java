package com.jsoft.magenta.posts;

import java.time.LocalDateTime;

public interface PostSearchResult {
  Long getId();
  String getTitle();
  LocalDateTime getCreatedAt();
}
