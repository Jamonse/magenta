package com.jsoft.magenta.contacts;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.events.accounts.AccountAssociatedEntityEvent;
import com.jsoft.magenta.exceptions.DuplicationException;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.security.UserEvaluator;
import com.jsoft.magenta.util.AppDefaults;
import com.jsoft.magenta.util.PageRequestBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ContactService
{
    private final ContactRepository contactRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Contact createContact(Long accountId, Contact contact)
    { // Verify that account exists and permission is valid by publishing event
        this.eventPublisher.publishEvent(new AccountAssociatedEntityEvent(accountId));
        verifyContactUniques(accountId, contact);
        contact.setAccount(new Account(accountId));
        return this.contactRepository.save(contact);
    }

    public Contact updateContact(Contact contact)
    {
        Long accountId = getAccountId(contact.getId());
        this.eventPublisher.publishEvent(new AccountAssociatedEntityEvent(accountId));
        Contact contactToUpdate = findContact(contact.getId());
        if(!contactToUpdate.getEmail().equals(contact.getEmail()))
            verifyContactEmailUnique(accountId, contact.getEmail());
        if(!contactToUpdate.getPhoneNumber().equals(contact.getPhoneNumber()))
            verifyContactPhoneNumberUnique(accountId, contact.getPhoneNumber());
        return this.contactRepository.save(contact);
    }

    public Page<Contact> getAllContacts(
            Long accountId, int pageIndex, int pageSize, String sortBy, boolean asc)
    {
        this.eventPublisher.publishEvent(new AccountAssociatedEntityEvent(accountId));
        PageRequest pageRequest;
        if(sortBy.equals(AppDefaults.CONTACT_DEFAULT_SORT_NAME))
            pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, AppDefaults.CONTACT_DEFAULT_SORT, asc);
        else
        pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
        Page<Contact> pageResult = this.contactRepository.findAllByAccountId(accountId, pageRequest);
        return new PageImpl<>(pageResult.getContent(), pageRequest, pageResult.getTotalElements());
    }

    public List<ContactSearchResult> getAllContactsByNameExample(Long accountId, String nameExample, int resultsCount)
    {
        this.eventPublisher.publishEvent(new AccountAssociatedEntityEvent(accountId));
        PageRequest pageRequest = PageRequestBuilder.buildPageRequest(0, resultsCount, AppDefaults.CONTACT_DEFAULT_SORT, false);
        return this.contactRepository.findAllByAccountIdAndNameExample(accountId, nameExample, pageRequest);
    }

    public void deleteContact(Long accountId, Long contactId)
    {
        isContactExists(contactId);
        this.eventPublisher.publishEvent(new AccountAssociatedEntityEvent(accountId));
        this.contactRepository.deleteById(contactId);
    }

    private Contact findContact(Long contactId)
    {
        Long userId = UserEvaluator.currentUserId();
        return this.contactRepository
                .findByIdAndAccountAssociationsUserId(contactId, userId)
                .orElseThrow(() -> new NoSuchElementException("Contact not found"));
    }

    private Long getAccountId(Long contactId)
    {
        return this.contactRepository
                .findAccountIdById(contactId)
                .orElseThrow(() -> new NoSuchElementException("Contact not found"));
    }

    private void isContactExists(Long contactId)
    {
        boolean exists = this.contactRepository.existsById(contactId);
        if(!exists)
            throw new NoSuchElementException("Contact not found");
    }

    private void verifyContactUniques(Long accountId, Contact contact)
    {
        verifyContactEmailUnique(accountId, contact.getEmail());
        verifyContactPhoneNumberUnique(accountId, contact.getPhoneNumber());
    }

    private void verifyContactEmailUnique(Long accountId, String email)
    {
        boolean exists = this.contactRepository
                .existsByEmailAndAccountId(email, accountId);
        if(exists)
            throw new DuplicationException(
                    String.format("Contact with email address %s, already exists for specified account", email));
    }

    private void verifyContactPhoneNumberUnique(Long accountId, String phoneNumber)
    {
        boolean exists = this.contactRepository
                .existsByPhoneNumberAndAccountId(phoneNumber, accountId);
        if(exists)
            throw new DuplicationException(
                    String.format("Contact with phone number %s, already exists for specified account", phoneNumber));
    }

}
