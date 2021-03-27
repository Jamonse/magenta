package com.jsoft.magenta.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UnsupportedPermissionLevelException extends RuntimeException {

  public UnsupportedPermissionLevelException(String message) {
    super(message);
  }
}
