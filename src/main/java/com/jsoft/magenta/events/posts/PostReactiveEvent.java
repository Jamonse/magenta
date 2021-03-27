package com.jsoft.magenta.events.posts;

import com.jsoft.magenta.events.reactive.ReactiveEvent;
import com.jsoft.magenta.events.reactive.ReactiveEventType;
import com.jsoft.magenta.posts.Post;

public class PostReactiveEvent extends ReactiveEvent<Post> {

  public PostReactiveEvent(Post post, ReactiveEventType eventType) {
    super(post, eventType);
  }

}
