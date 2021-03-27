package com.jsoft.magenta.accounts;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.contacts.Contact;
import com.jsoft.magenta.contacts.ContactRepository;
import com.jsoft.magenta.contacts.ContactService;
import com.jsoft.magenta.security.SecurityService;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class ContactServiceTest {

  @InjectMocks
  private ContactService contactService;

  @Mock
  private SecurityService securityService;

  @Mock
  private ContactRepository contactRepository;

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @BeforeEach
  private void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Save contact")
  public void saveContact() {
    Account account = new Account();
    account.setId(1L);
    Contact contact = new Contact();
    contact.setAccount(account);
    Contact returnedContact = new Contact();
    returnedContact.setId(1L);
    returnedContact.setAccount(account);

    when(contactRepository.save(contact)).thenReturn(returnedContact);

    Contact savedContact = this.contactService.createContact(account.getId(), contact);

    Assertions.assertNotNull(savedContact.getId());
    Assertions.assertEquals(savedContact, returnedContact);

    verify(contactRepository).save(contact);
  }

  @Test
  @DisplayName("Update contact")
  public void updateContact() {
    Account account = new Account();
    account.setId(1L);
    Contact contact = new Contact();
    contact.setId(1L);
    contact.setFirstName("contact");
    contact.setLastName("contact");
    contact.setEmail("contact");
    contact.setPhoneNumber("contact");
    contact.setAccount(account);

    when(securityService.currentUserId()).thenReturn(1L);
    when(contactRepository.findByIdAndAccountAssociationsUserId(contact.getId(), 1L))
        .thenReturn(Optional.of(contact));
    when(contactRepository.findAccountIdById(contact.getId()))
        .thenReturn(Optional.of(account.getId()));
    when(contactRepository.save(contact)).thenReturn(contact);

    Contact savedContact = this.contactService.updateContact(contact);

    Assertions.assertNotNull(savedContact.getId());
    Assertions.assertEquals(savedContact, contact);

    verify(contactRepository).findByIdAndAccountAssociationsUserId(contact.getId(), 1L);
    verify(contactRepository).findAccountIdById(contact.getId());
    verify(contactRepository).save(contact);
  }

  @Test
  @DisplayName("Get account contacts")
  public void getAccountContacts() {
    List<Contact> contacts = List.of(new Contact(), new Contact());
    Sort sort = Sort.by("firstName").ascending();
    PageRequest pageRequest = PageRequest.of(0, 5, sort);
    Page<Contact> pageResult = new PageImpl<>(contacts, pageRequest, contacts.size());

    when(contactRepository.findAllByAccountId(1L, pageRequest)).thenReturn(pageResult);

    Page<Contact> contactPage = this.contactService.getAllContacts(1L, 0, 5, "firstName", true);

    Assertions.assertFalse(contactPage.isEmpty());
    Assertions.assertEquals(contactPage.getContent().size(), contacts.size());

    verify(contactRepository).findAllByAccountId(1L, pageRequest);
  }

  @Test
  @DisplayName("Delete contact")
  public void deleteContact() {
    Account account = new Account(1L);
    Contact contact = new Contact();
    contact.setAccount(account);
    when(securityService.currentUserId()).thenReturn(1L);
    when(contactRepository.existsById(1L)).thenReturn(true);
    when(contactRepository.findByIdAndAccountAssociationsUserId(1L, 1L))
        .thenReturn(Optional.of(contact));
    doNothing().when(contactRepository).deleteById(1L);

    this.contactService.deleteContact(1L);

    verify(contactRepository).findByIdAndAccountAssociationsUserId(1L, 1L);
    verify(contactRepository).existsById(1L);
  }

}
