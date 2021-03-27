package com.jsoft.magenta.exceptions;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class MagentaException {

  private String message;
  private HttpStatus status;
  private LocalDateTime timeStamp;
}
