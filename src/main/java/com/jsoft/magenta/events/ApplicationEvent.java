package com.jsoft.magenta.events;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApplicationEvent<T> {

  protected final T payLoad;

  public T getPayload() {
    return this.payLoad;
  }
}
