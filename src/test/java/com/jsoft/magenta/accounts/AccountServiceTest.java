package com.jsoft.magenta.accounts;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.exceptions.DuplicationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Optional;

public class AccountServiceTest
{
    @InjectMocks
    AccountService accountService;

    @Mock
    AccountRepository accountRepository;

    @BeforeEach
    public void init()
    {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Save account")
    public void saveAccount()
    {
        Account account1 = new Account();
        account1.setName("name");

        when(accountRepository.save(account1)).thenReturn(account1);

        Account savedAccount = this.accountService.createAccount(account1);
        Assertions.assertEquals(savedAccount.getName(), "Name");
    }

    @Test
    @DisplayName("Save account with existing name")
    public void saveExistingAccount()
    {
        Account account1 = new Account();
        account1.setName("name");

        when(accountRepository.findByName("name")).thenReturn(Optional.of(account1));

        Assertions.assertThrows(DuplicationException.class, () -> this.accountService.createAccount(account1));
    }

    @Test
    @DisplayName("Update account name")
    public void updateAccount()
    {
        Account account1 = new Account();
        account1.setId(1L);
        account1.setName("name");

        when(accountRepository.findByName("new name")).thenReturn(Optional.empty());
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.save(account1)).thenReturn(account1);

        this.accountService.createAccount(account1);

        account1.setName("new name");
        Account updatedAccount = this.accountService.updateAccountName(account1.getId(), account1.getName());

        Assertions.assertEquals(updatedAccount.getName(), account1.getName());
        verify(accountRepository).findByName("name");
    }

    @Test
    @DisplayName("Update account name to existing name")
    public void updateAccountWithExistingName()
    {
        Account account1 = new Account();
        account1.setId(1L);
        account1.setName("name");

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account1));
        when(accountRepository.findByName("name")).thenReturn(Optional.of(account1));

        Assertions.assertThrows(DuplicationException.class,
                () -> this.accountService.updateAccountName(account1.getId(), account1.getName()));

        verify(accountRepository).findByName("name");
    }

    @Test
    @DisplayName("Find account by name")
    public void findAccountByName()
    {
        Account account = new Account();
        account.setName("name");
        when(accountRepository.findByName("name")).thenReturn(Optional.of(account));

        Account result = accountService.getAccountByName("name");

        Assertions.assertEquals(result.getName(), "name");
        verify(accountRepository).findByName("name");
    }

    @Test
    @DisplayName("Find all accounts")
    public void findAllAccounts()
    {
        int pageIndex = 0, pageSize = 5;
        Sort sort = Sort.by("name").ascending();
        PageRequest pageRequest = PageRequest.of(pageIndex, pageSize, sort);

        when(this.accountRepository.findAll(pageRequest))
                .thenReturn(new PageImpl<>(Arrays.asList(new Account(), new Account()), pageRequest, 2));

        Page<Account> accounts = this.accountService.getAllAccounts(0, 5, "name", true);

        Assertions.assertFalse(accounts.isEmpty());
        Assertions.assertEquals(accounts.getTotalElements(), 2);
    }

    @Test
    @DisplayName("Create contact for account")
    public void saveContact()
    {
        Account account1 = new Account();
        account1.setName("name");

        when(accountRepository.save(account1)).thenReturn(account1);

        Account savedAccount = this.accountService.createAccount(account1);
    }

}
