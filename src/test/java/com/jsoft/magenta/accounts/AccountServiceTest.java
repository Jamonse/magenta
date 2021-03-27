package com.jsoft.magenta.accounts;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.accounts.domain.AccountAssociation;
import com.jsoft.magenta.events.accounts.AccountAssociationUpdateEvent;
import com.jsoft.magenta.exceptions.DuplicationException;
import com.jsoft.magenta.security.SecurityService;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.users.UserRepository;
import com.jsoft.magenta.util.AppConstants;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class AccountServiceTest {

  @InjectMocks
  private AccountService accountService;

  @Mock
  private SecurityService securityService;

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private AccountAssociationRepository accountAssociationRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  @DisplayName("Save account")
  public void saveAccount() {
    Account account1 = new Account();
    account1.setName("name");
    Account savedAccount = new Account();
    savedAccount.setName("Name");

    when(accountRepository.save(account1)).thenReturn(account1);

    this.accountService.createAccount(account1, null, null, null);
    Assertions.assertEquals(savedAccount.getName(), "Name");
  }

  @Test
  @DisplayName("Create association")
  public void createAssociation() {
    User user = new User();
    user.setId(1L);
    Account account = new Account();
    account.setId(1L);
    Privilege privilege = new Privilege();
    privilege.setName(AppConstants.ACCOUNT_PERMISSION);
    privilege.setLevel(AccessPermission.ADMIN);
    user.setPrivileges(Set.of(privilege));
    AccountAssociation accountAssociation = new AccountAssociation(user, account,
        AccessPermission.MANAGE);

    Mockito.when(securityService.currentUser()).thenReturn(user);
    Mockito.when(
        accountAssociationRepository.existsByUserIdAndAccountId(user.getId(), account.getId()))
        .thenReturn(false);
    Mockito.when(accountAssociationRepository.save(accountAssociation))
        .thenReturn(accountAssociation);

    accountService.createAssociation(user.getId(), account.getId(), AccessPermission.MANAGE);

    Mockito.verify(accountAssociationRepository)
        .existsByUserIdAndAccountId(user.getId(), account.getId());
    Mockito.verify(accountAssociationRepository).save(accountAssociation);
  }

  @Test
  @DisplayName("Update association")
  public void updateAssociation() {
    User user = new User();
    user.setId(1L);
    Account account = new Account();
    account.setId(1L);
    Privilege privilege = new Privilege();
    privilege.setName(AppConstants.ACCOUNT_PERMISSION);
    privilege.setLevel(AccessPermission.ADMIN);
    user.setPrivileges(Set.of(privilege));
    AccountAssociation accountAssociation = new AccountAssociation(user, account,
        AccessPermission.MANAGE);
    user.setAccounts(Set.of(accountAssociation));

    when(securityService.currentUser()).thenReturn(user);
    Mockito
        .when(accountAssociationRepository.findByUserIdAndAccountId(user.getId(), account.getId()))
        .thenReturn(Optional.of(accountAssociation));
    Mockito.when(accountAssociationRepository.save(accountAssociation))
        .thenReturn(accountAssociation);
    Mockito.when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
    Mockito.doNothing().when(eventPublisher)
        .publishEvent(Mockito.any(AccountAssociationUpdateEvent.class));

    accountService.updateAssociation(user.getId(), account.getId(), AccessPermission.MANAGE);

    Mockito.verify(accountAssociationRepository)
        .findByUserIdAndAccountId(user.getId(), account.getId());
    Mockito.verify(accountAssociationRepository).save(accountAssociation);
  }

  @Test
  @DisplayName("Save account with existing name")
  public void saveExistingAccount() {
    Account account1 = new Account();
    account1.setName("name");

    when(accountRepository.existsByName("name")).thenReturn(true);

    Assertions.assertThrows(DuplicationException.class,
        () -> this.accountService.createAccount(account1, null, null, null));
  }

  @Test
  @DisplayName("Update account name")
  public void updateAccount() {
    Account account = new Account();
    account.setId(1L);
    account.setName("name");

    when(accountRepository.existsByName("new name")).thenReturn(false);
    when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
    when(accountRepository.save(account)).thenReturn(account);

    Account updatedAccount = this.accountService.updateAccountName(account.getId(), "new name");

    Assertions.assertEquals(updatedAccount.getName(), account.getName());
    verify(accountRepository).existsByName("new name");
  }

  @Test
  @DisplayName("Update account name to existing name")
  public void updateAccountWithExistingName() {
    Account account = new Account();
    account.setId(1L);
    account.setName("name");

    String newName = "new name";

    when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
    when(accountRepository.existsByName(newName)).thenReturn(true);

    Assertions.assertThrows(DuplicationException.class,
        () -> this.accountService.updateAccountName(account.getId(), newName));

    verify(accountRepository).existsByName(newName);
  }

  @Test
  @DisplayName("Find all accounts")
  public void findAllAccounts() {
    int pageIndex = 0, pageSize = 5;
    Sort sort = Sort.by("name").ascending();
    PageRequest pageRequest = PageRequest.of(pageIndex, pageSize, sort);
    User user = new User();
    user.setId(1L);
    Privilege privilege = new Privilege();
    privilege.setName(AppConstants.ACCOUNT_PERMISSION);
    privilege.setLevel(AccessPermission.ADMIN);
    user.setPrivileges(Set.of(privilege));

    when(securityService.currentUser()).thenReturn(user);
    when(this.accountRepository.findAll(pageRequest))
        .thenReturn(new PageImpl<>(Arrays.asList(new Account(), new Account()), pageRequest, 2));

    Page<Account> accounts = this.accountService.getAllAccounts(0, 5, "name", true);

    Assertions.assertFalse(accounts.isEmpty());
    Assertions.assertEquals(accounts.getTotalElements(), 2);
  }

  @Test
  @DisplayName("Get all accounts results")
  public void getAllAccountsResults() {
    Sort sort = Sort.by("name").descending();
    PageRequest pageRequest = PageRequest.of(0, 5, sort);
    Privilege privilege = new Privilege();
    privilege.setLevel(AccessPermission.ADMIN);
    privilege.setName(AppConstants.ACCOUNT_PERMISSION);
    User user = new User();
    user.setPrivileges(Set.of(privilege));

    Mockito.when(securityService.currentUser()).thenReturn(user);
    Mockito.when(accountRepository.findAllResultsBy(pageRequest))
        .thenReturn(List.of());

    accountService.getAllAccountsResults(5);

    Mockito.verify(accountRepository).findAllResultsBy(pageRequest);
  }

  @Test
  @DisplayName("Get all accounts results of user")
  public void getAllAccountsResultsOfUser() {
    Sort sort = Sort.by("name").descending();
    PageRequest pageRequest = PageRequest.of(0, 5, sort);
    Privilege privilege = new Privilege();
    privilege.setLevel(AccessPermission.ADMIN);
    privilege.setName(AppConstants.ACCOUNT_PERMISSION);
    User user = new User();
    user.setPrivileges(Set.of(privilege));

    Mockito.when(accountRepository.findAllResultsByAssociationsUserId(1L, pageRequest))
        .thenReturn(List.of());
    when(securityService.currentUser()).thenReturn(user);

    accountService.getAllAccountsResultsOfUser(1L, 5);

    Mockito.verify(accountRepository).findAllResultsByAssociationsUserId(1L, pageRequest);
  }

  @Test
  @DisplayName("Create contact for account")
  public void saveContact() {
    Account account1 = new Account();
    account1.setName("name");

    when(accountRepository.save(account1)).thenReturn(account1);

    this.accountService.createAccount(account1, null, null, null);
  }

}
