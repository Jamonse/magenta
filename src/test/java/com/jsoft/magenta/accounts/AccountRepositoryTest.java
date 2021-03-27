package com.jsoft.magenta.accounts;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.accounts.domain.AccountAssociation;
import com.jsoft.magenta.accounts.domain.AccountSearchResult;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.users.User;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Set;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountRepositoryTest {

  @Autowired
  AccountRepository accountRepository;

  @Test
  @DisplayName("Create account, save and get it")
  public void create() {
    Account ac1 = new Account();
    ac1.setName("name");
    ac1.setCreatedAt(LocalDate.now());

    Assertions.assertNull(ac1.getId());
    this.accountRepository.save(ac1);
    Assertions.assertNotNull(ac1.getId());
  }

  @Test
  @DisplayName("Get account by name")
  public void findAccountByName() {
    Account ac1 = new Account();
    ac1.setName("name");
    ac1.setCreatedAt(LocalDate.now());

    this.accountRepository.save(ac1);

    Account foundByName = this.accountRepository
        .findByName("name")
        .orElseThrow(() -> new NoSuchElementException("Account not found"));
    Assertions.assertEquals(ac1.getName(), foundByName.getName());
  }

  @Test
  @DisplayName("Get first page of five accounts sorted by name ascending")
  public void crateAccountsDuplication() {
    this.accountRepository.deleteAll();

    Account ac1 = new Account();
    ac1.setName("account a");
    ac1.setCreatedAt(LocalDate.now());
    Account ac2 = new Account();
    ac2.setName("account b");
    ac2.setCreatedAt(LocalDate.now());

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
  public void getAccountByNameExample() {
    Account ac = new Account();
    ac.setName("name");
    ac.setId(151L);
    ac.setCreatedAt(LocalDate.now());
    User user = new User();
    user.setId(179L);
    user.setFirstName("first name");
    user.setLastName("last name");
    user.setEmail("email");
    user.setPassword("password");
    AccountAssociation accountAssociation = new AccountAssociation(user, ac,
        AccessPermission.ADMIN);
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
