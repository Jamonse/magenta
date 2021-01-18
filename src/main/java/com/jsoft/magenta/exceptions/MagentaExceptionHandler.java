package com.jsoft.magenta.exceptions;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class MagentaExceptionHandler
{
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException e)
    {
        return handleValidationException(e);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentValidationException(MethodArgumentNotValidException e)
    {
        return handleValidationException(e);
    }

    private ResponseEntity<Object> handleException(Exception e, HttpStatus httpStatus)
    {
        MagentaException magentaException = new MagentaException(e.getMessage(), httpStatus, LocalDateTime.now());
        return ResponseEntity.status(httpStatus).body(magentaException);
    }

    private ResponseEntity<Object> handleValidationException(Exception e)
    {
        List<String> errors; // Validation errors list
        Map<String, Object> responseBody = new HashMap<>(); // Create response body
        // Determine validation exception type
        if(e instanceof MethodArgumentNotValidException)
            errors = ((MethodArgumentNotValidException) e).getBindingResult().getFieldErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .collect(Collectors.toList());
        else if(e instanceof ConstraintViolationException)
            errors = ((ConstraintViolationException) e).getConstraintViolations()
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList());
        else
            throw new IllegalStateException("Specified exception is not an acceptable validation exception");
        // Build the response body
        responseBody.put("message", "Error/s during parameters validation");
        responseBody.put("errors", errors);
        responseBody.put("httpStatus", HttpStatus.BAD_REQUEST);
        responseBody.put("timeStamp", LocalDateTime.now());
        // Return response body with status 400
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }
}
