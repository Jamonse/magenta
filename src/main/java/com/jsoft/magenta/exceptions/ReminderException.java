package com.jsoft.magenta.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ReminderException extends RuntimeException {

  public ReminderException(String message) {
    super(message);
  }
}
