package com.jsoft.magenta.accounts;

import com.jsoft.magenta.accounts.domain.Account;
import java.time.LocalDate;
import java.util.HashSet;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AccountPojoTest {

  @Test
  @DisplayName("Create two accounts and check getter and equals")
  public void create() {
    Account ac1 = new Account(1L, "account", LocalDate.now(), null, null, null,
        new HashSet<>(), new HashSet<>(), new HashSet<>());
    Account ac2 = new Account(1L, "ac", LocalDate.now(), null, null, null,
        new HashSet<>(), new HashSet<>(), new HashSet<>());

    Assertions.assertEquals(ac1.getName(), "account");
    Assertions.assertTrue(ac1.equals(ac2));
  }

  @Test
  @DisplayName("Update account and check getter")
  public void update() {
    Account ac1 = new Account(1L, "account", LocalDate.now(), null, null, null,
        new HashSet<>(), new HashSet<>(), new HashSet<>());
    ac1.setName("new name");

    Assertions.assertEquals(ac1.getName(), "new name");
  }
}
