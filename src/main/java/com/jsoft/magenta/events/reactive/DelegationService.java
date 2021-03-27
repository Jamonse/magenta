package com.jsoft.magenta.events.reactive;

import java.util.function.Consumer;

public interface DelegationService<T extends ReactiveEvent> {

  void delegateRequest(Long requesterId, Consumer<T> listener);

}
