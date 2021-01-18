package com.jsoft.magenta.accounts;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.accounts.domain.Contact;
import com.jsoft.magenta.exceptions.DuplicationException;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.security.UserEvaluator;
import com.jsoft.magenta.util.PageRequestBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ContactService
{
    private final AccountRepository accountRepository;
    private final ContactRepository contactRepository;

    public Contact createContact(Long accountId, Contact contact)
    {
        Account account = findAccount(accountId);
        verifyContactForAccount(accountId, contact);
        contact.setAccount(account);
        return this.contactRepository.save(contact);
    }

    public Contact updateContact(Long accountId, Contact contact)
    {
        Contact contactToUpdate = findContact(contact.getId());
        if(!contactToUpdate.getEmail().equals(contact.getEmail()))
            verifyContactEmail(accountId, contact.getEmail());
        if(!contactToUpdate.getPhoneNumber().equals(contact.getPhoneNumber()))
            verifyContactPhoneNumber(accountId, contact.getPhoneNumber());
        return this.contactRepository.save(contact);
    }

    public Page<Contact> getAllContacts(
            Long accountId, int pageIndex, int pageSize, String sortBy, boolean asc)
    {
        findAccount(accountId); // Verify association
        PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
        Page<Contact> pageResult = this.contactRepository.findAllByAccountId(accountId, pageRequest);
        return new PageImpl<>(pageResult.getContent(), pageRequest, pageResult.getTotalElements());
    }

    public void deleteContact(Long contactId)
    {
        findContact(contactId);
        this.contactRepository.deleteById(contactId);
    }

    private Account findAccount(Long accountId)
    {
        Long userId = UserEvaluator.currentUserId();
        return this.accountRepository
                .findByAssociationsUserIdAndId(userId, accountId)
                .orElseThrow(() -> new NoSuchElementException("Account not found"));
    }

    private Contact findContact(Long contactId)
    {
        Long userId = UserEvaluator.currentUserId();
        return this.contactRepository
                .findByIdAndAccountAssociationsUserId(contactId, userId)
                .orElseThrow(() -> new NoSuchElementException("Contact not found"));
    }

    private void verifyContactForAccount(Long accountId, Contact contact)
    {
        verifyContactEmail(accountId, contact.getEmail());
        verifyContactPhoneNumber(accountId, contact.getPhoneNumber());
    }

    private void verifyContactEmail(Long accountId, String email)
    {
        this.contactRepository
                .findByEmailAndAccountId(email, accountId)
                .ifPresent(this::throwContactEmailExistForAccountException);
    }
    private void verifyContactPhoneNumber(Long accountId, String phoneNumber)
    {
        this.contactRepository
                .findByPhoneNumberAndAccountId(phoneNumber, accountId)
                .ifPresent(this::throwContactPhoneNumberExistForAccountException);
    }

    private void throwContactEmailExistForAccountException(Contact contact)
    {
        throw new DuplicationException(
                String.format("Contact with email address %s, already exists for specified account", contact.getEmail()));
    }

    private void throwContactPhoneNumberExistForAccountException(Contact contact)
    {
        throw new DuplicationException(
                String.format("Contact with phone number %s, already exists for specified account", contact.getPhoneNumber()));
    }

}
