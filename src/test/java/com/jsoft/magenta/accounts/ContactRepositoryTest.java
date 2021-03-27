package com.jsoft.magenta.accounts;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.contacts.Contact;
import com.jsoft.magenta.contacts.ContactRepository;
import com.jsoft.magenta.contacts.ContactSearchResult;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ContactRepositoryTest {

  @Autowired
  private AccountRepository accountRepository;

  @Autowired
  private ContactRepository contactRepository;

  @Test
  @DisplayName("Get contact by account id")
  public void getAccountContactById() {
    Account account = new Account();
    account.setName("account");
    account.setCreatedAt(LocalDate.now());
    Contact contact = new Contact();
    contact.setFirstName("first name");
    contact.setLastName("last name");
    contact.setEmail("contact@contact.com");
    contact.setPhoneNumber("055-5555555");
    account.setContacts(Set.of(contact));

    this.accountRepository.save(account);

    Optional<Contact> returnedContact = this.contactRepository
        .findByIdAndAccountId(contact.getId(), account.getId());

    Assertions.assertFalse(returnedContact.isEmpty());
    Assertions.assertEquals(returnedContact.get(), contact);
  }

  @Test
  @DisplayName("Get contacts by account id")
  public void getAllAccountContacts() {
    Account account = new Account();
    account.setName("account");
    account.setCreatedAt(LocalDate.now());
    Contact contact = new Contact();
    contact.setFirstName("first name");
    contact.setLastName("last name");
    contact.setEmail("contact@contact.com");
    contact.setPhoneNumber("055-5555555");
    account.setContacts(Set.of(contact));
    PageRequest pageRequest = PageRequest.of(0, 5);

    this.accountRepository.save(account);

    Page<Contact> contacts = this.contactRepository
        .findAllByAccountId(account.getId(), pageRequest);

    Assertions.assertFalse(contacts.isEmpty());
    Assertions.assertEquals(contacts.getContent().get(0), contact);
  }

  @Test
  @DisplayName("Get account contacts result by example")
  public void getAccountContactResults() {
    Account account = new Account();
    account.setName("account");
    account.setCreatedAt(LocalDate.now());
    Contact contact = new Contact();
    contact.setFirstName("first name");
    contact.setLastName("last name");
    contact.setEmail("contact@contact.com");
    contact.setPhoneNumber("055-5555555");
    account.setContacts(Set.of(contact));
    PageRequest pageRequest = PageRequest.of(0, 5);

    this.accountRepository.save(account);

    List<ContactSearchResult> contacts =
        this.contactRepository.findAllByAccountIdAndNameExample(
            account.getId(), "a", pageRequest
        );

    Assertions.assertFalse(contacts.isEmpty());
    Assertions.assertEquals(contacts.get(0).getId(), contact.getId());
  }

  @Test
  @DisplayName("Get account contacts result by example - should return only account contacts")
  public void getAccountContactResultsAndNoOtherContacts() {
    Account account = new Account();
    account.setName("account");
    account.setCreatedAt(LocalDate.now());
    Contact contact = new Contact();
    contact.setFirstName("first name");
    contact.setLastName("last name");
    contact.setEmail("contact@contact.com");
    contact.setPhoneNumber("055-5555555");
    account.setContacts(Set.of(contact));
    PageRequest pageRequest = PageRequest.of(0, 5);
    Contact newContact = new Contact();
    newContact.setFirstName("first name");
    newContact.setLastName("last name");
    newContact.setEmail("contact@contact.com");
    newContact.setPhoneNumber("055-5555555");

    this.accountRepository.save(account);
    this.contactRepository.save(newContact);

    List<ContactSearchResult> contacts =
        this.contactRepository.findAllByAccountIdAndNameExample(
            account.getId(), "a", pageRequest
        );

    Assertions.assertFalse(contacts.isEmpty());
    Assertions.assertEquals(contacts.size(), 1);
    Assertions.assertEquals(contacts.get(0).getId(), contact.getId());
  }

}
