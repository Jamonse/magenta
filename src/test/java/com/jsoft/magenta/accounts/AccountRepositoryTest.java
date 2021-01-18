package com.jsoft.magenta.accounts;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.accounts.domain.AccountAssociation;
import com.jsoft.magenta.accounts.domain.AccountSearchResult;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.users.User;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class AccountRepositoryTest
{
    @Autowired
    AccountRepository accountRepository;

    @Test
    @DisplayName("Create account, save and get it")
    public void create()
    {
        Account ac1 = new Account();
        ac1.setName("name");
        ac1.setCreatedAt(LocalDate.now());
        ac1.setImage("image");
        ac1.setBackgroundImage("background_image");

        Assertions.assertNull(ac1.getId());
        this.accountRepository.save(ac1);
        Assertions.assertNotNull(ac1.getId());
    }

    @Test
    @DisplayName("Get account by name")
    public void findAccountByName()
    {
        Account ac1 = new Account();
        ac1.setName("name");
        ac1.setCreatedAt(LocalDate.now());
        ac1.setImage("image");
        ac1.setBackgroundImage("background_image");

        this.accountRepository.save(ac1);

        Account foundByName = this.accountRepository
                .findByName("name")
                .orElseThrow(() -> new NoSuchElementException("Account not found"));
        Assertions.assertEquals(ac1.getName(), foundByName.getName());
    }

    @Test
    @DisplayName("Get first page of five accounts sorted by name ascending")
    public void crateAccountsDuplication()
    {
        Account ac1 = new Account();
        ac1.setName("account a");
        ac1.setCreatedAt(LocalDate.now());
        ac1.setImage("image");
        ac1.setBackgroundImage("background_image");
        Account ac2 = new Account();
        ac2.setName("account b");
        ac2.setCreatedAt(LocalDate.now());
        ac2.setImage("image");
        ac2.setBackgroundImage("background_image");

        Sort sort = Sort.by("name");

        this.accountRepository.saveAll(Arrays.asList(ac1, ac2));

        PageRequest pageRequest = PageRequest.of(0, 5, sort);
        Page<Account> accountPage = this.accountRepository.findAll(pageRequest);

        Assertions.assertEquals(accountPage.getTotalElements(), 2);
        Account first = accountPage.stream().findFirst().orElseThrow();
        Assertions.assertEquals(first.getName(), "account a");
    }

    @Test
    @DisplayName("Get account by name example")
    public void getAccountByNameExample()
    {
        Account ac = new Account();
        ac.setName("name");
        ac.setImage("image");
        ac.setBackgroundImage("background_image");
        ac.setCreatedAt(LocalDate.now());
        User user = new User();
        user.setId(1L);
        user.setFirstName("first name");
        user.setLastName("last name");
        user.setEmail("email");
        user.setPassword("password");
        AccountAssociation accountAssociation = new AccountAssociation();
        accountAssociation.setUser(user);
        Set<AccountAssociation> associations = Sets.newHashSet();
        associations.add(accountAssociation);
        ac.setAssociations(associations);

        this.accountRepository.save(ac);

        Page<AccountSearchResult> ac1 = this.accountRepository
                .findAllByNameContainingIgnoreCase("n", PageRequest.of(0, 1));

        Assertions.assertFalse(ac1.isEmpty());
        Assertions.assertEquals(ac1.stream().findFirst().get().getName(), "name");
    }

}
