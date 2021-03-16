package com.jsoft.magenta.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TextLengthException extends RuntimeException {
    public TextLengthException(String message) {
        super(message);
    }
}
