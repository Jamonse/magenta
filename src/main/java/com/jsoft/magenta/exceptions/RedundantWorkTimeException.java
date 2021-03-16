package com.jsoft.magenta.exceptions;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class RedundantWorkTimeException extends RuntimeException {
    public RedundantWorkTimeException(String message) {
        super(message);
    }
}
