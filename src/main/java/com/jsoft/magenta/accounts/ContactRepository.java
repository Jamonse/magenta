package com.jsoft.magenta.accounts;

import com.jsoft.magenta.accounts.domain.Contact;
import com.jsoft.magenta.accounts.domain.ContactSearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactRepository extends JpaRepository<Contact, Long>
{
    Optional<Contact> findByIdAndAccountId(Long contactId, Long accountId);

    Optional<Contact> findByIdAndAccountAssociationsUserId(Long contactId, Long accountId);

    Optional<Contact> findByEmailAndAccountId(String email, Long accountId);

    Optional<Contact> findByPhoneNumberAndAccountId(String phoneNumber, Long accountId);

    Page<Contact> findAllByAccountId(Long contactId, Pageable pageable);

    List<ContactSearchResult> findAllByAccountIdAndFirstNameContainingIgnoreCaseOrAccountIdAndLastNameContainingIgnoreCaseOrAccountIdAndEmailContainingIgnoreCase(
            Long idFirstArg, String firstNameExample, Long idSecondArg, String lastNameExample, Long idThirdArg, String emailExample, Pageable pageable
    );

    default List<ContactSearchResult> findAllByAccountIdAndNameExample(
            Long accountId, String example, Pageable pageable)
    {
        return findAllByAccountIdAndFirstNameContainingIgnoreCaseOrAccountIdAndLastNameContainingIgnoreCaseOrAccountIdAndEmailContainingIgnoreCase(
                accountId, example, accountId, example, accountId, example, pageable
        );
    }
}
