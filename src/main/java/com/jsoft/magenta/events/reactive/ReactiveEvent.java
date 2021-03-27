package com.jsoft.magenta.events.reactive;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReactiveEvent<T> {

  protected T payLoad;
  protected ReactiveEventType eventType;

}
