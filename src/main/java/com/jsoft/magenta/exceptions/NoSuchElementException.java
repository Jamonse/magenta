package com.jsoft.magenta.exceptions;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@NoArgsConstructor
public class NoSuchElementException extends RuntimeException
{
    public NoSuchElementException(String message)
    {
        super(message);
    }
}
