package com.jsoft.magenta.events.accounts;

import com.jsoft.magenta.events.ApplicationEvent;

public class AccountAssociatedEntityEvent extends ApplicationEvent<Long> {

  public AccountAssociatedEntityEvent(Long accountId) {
    super(accountId);
  }
}
