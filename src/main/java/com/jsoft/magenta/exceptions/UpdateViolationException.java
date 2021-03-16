package com.jsoft.magenta.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UpdateViolationException extends RuntimeException {
    public UpdateViolationException(String message) {
        super(message);
    }
}
