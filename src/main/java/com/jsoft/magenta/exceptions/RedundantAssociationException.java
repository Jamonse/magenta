package com.jsoft.magenta.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RedundantAssociationException extends RuntimeException {

  public RedundantAssociationException(String message) {
    super(message);
  }
}
