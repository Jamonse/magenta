package com.jsoft.magenta.posts;

import com.jsoft.magenta.events.posts.PostReactiveEvent;
import com.jsoft.magenta.events.reactive.DelegationService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PostsDelegationService implements DelegationService<PostReactiveEvent> {

  private Map<Long, Consumer<PostReactiveEvent>> listeners = new ConcurrentHashMap<>();

  @Override
  public void delegateRequest(Long requesterId, Consumer<PostReactiveEvent> listener) {
    this.listeners.put(requesterId, listener);
  }

  public void sendEvent(PostReactiveEvent reactiveEvent) {
    if (reactiveEvent == null) {
      throw new IllegalArgumentException(
          "Reactive event must not be null, otherwise cannot be processed");
    }
    processEvent(reactiveEvent);
  }

  private void processEvent(PostReactiveEvent reactiveEvent) {
    listeners.forEach((requesterId, listener) -> listener.accept(reactiveEvent));
  }
}
