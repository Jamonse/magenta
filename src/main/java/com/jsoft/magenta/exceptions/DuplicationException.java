package com.jsoft.magenta.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DuplicationException extends RuntimeException {

  public DuplicationException(String message) {
    super(message);
  }
}
