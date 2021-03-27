package com.jsoft.magenta.events;

public class AssociationEvent<T> extends ApplicationEvent<T> {

  protected final Long associatedUserId;

  public AssociationEvent(T payLoad, Long associatedUserId) {
    super(payLoad);
    this.associatedUserId = associatedUserId;
  }

  public Long getAssociatedUserId() {
    return this.associatedUserId;
  }
}
