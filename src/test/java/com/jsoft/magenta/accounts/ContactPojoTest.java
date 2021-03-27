package com.jsoft.magenta.accounts;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.contacts.Contact;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ContactPojoTest {

  @Test
  @DisplayName("Create two contacts and check getter and equals")
  public void create() {
    Contact c1 = new Contact(1L, "first name", "last name", new Account(), "email", "phone number");
    Contact c2 = new Contact(1L, "first name", "last name", new Account(), "email", "phone number");

    Assertions.assertEquals(c1.getFirstName(), "first name");
    Assertions.assertTrue(c1.equals(c2));
  }

  @Test
  @DisplayName("Update contact and check getter")
  public void update() {
    Contact c1 = new Contact(1L, "first name", "last name", new Account(), "email", "phone number");
    c1.setFirstName("new first name");

    Assertions.assertEquals(c1.getFirstName(), "new first name");

  }
}
