package com.jsoft.magenta.accounts;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.accounts.domain.AccountAssociation;
import com.jsoft.magenta.events.accounts.AccountAssociationUpdateEvent;
import com.jsoft.magenta.exceptions.DuplicationException;
import com.jsoft.magenta.security.UserEvaluator;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.users.UserRepository;
import com.jsoft.magenta.util.AppConstants;
import org.junit.jupiter.api.*;
import org.mockito.*;

import static org.mockito.Mockito.*;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class AccountServiceTest
{
    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountAssociationRepository accountAssociationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private static MockedStatic<UserEvaluator> mockedStatic;

    @BeforeAll
    public static void initStatic()
    {
        mockedStatic = Mockito.mockStatic(UserEvaluator.class);
    }

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

        Account savedAccount = this.accountService.createAccount(account1, null, null, null);
        Assertions.assertEquals(savedAccount.getName(), "Name");
    }

    @Test
    @DisplayName("Create association")
    public void createAssociation()
    {
        User user = new User();
        user.setId(1L);
        Account account = new Account();
        account.setId(1L);
        Privilege privilege = new Privilege();
        privilege.setName(AppConstants.ACCOUNT_PERMISSION);
        privilege.setLevel(AccessPermission.ADMIN);
        user.setPrivileges(Set.of(privilege));
        AccountAssociation accountAssociation = new AccountAssociation();
        accountAssociation.setAccount(account);
        accountAssociation.setUser(user);
        accountAssociation.setPermission(AccessPermission.MANAGE);

        mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);
        Mockito.when(accountAssociationRepository.findByUserIdAndAccountId(user.getId(), account.getId()))
                .thenReturn(Optional.empty());
        Mockito.when(accountAssociationRepository.save(accountAssociation))
                .thenReturn(accountAssociation);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        Mockito.when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));

        accountService.createAssociation(user.getId(), account.getId(), AccessPermission.MANAGE);

        Mockito.verify(accountAssociationRepository).findByUserIdAndAccountId(user.getId(), account.getId());
        Mockito.verify(accountAssociationRepository).save(accountAssociation);
        Mockito.verify(userRepository).findById(user.getId());
        Mockito.verify(accountRepository).findById(account.getId());
    }

    @Test
    @DisplayName("Update association")
    public void updateAssociation()
    {
        User user = new User();
        user.setId(1L);
        Account account = new Account();
        account.setId(1L);
        Privilege privilege = new Privilege();
        privilege.setName(AppConstants.ACCOUNT_PERMISSION);
        privilege.setLevel(AccessPermission.ADMIN);
        user.setPrivileges(Set.of(privilege));
        AccountAssociation accountAssociation = new AccountAssociation();
        accountAssociation.setAccount(account);
        accountAssociation.setUser(user);
        accountAssociation.setPermission(AccessPermission.MANAGE);

        mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);
        Mockito.when(accountAssociationRepository.findByUserIdAndAccountId(user.getId(), account.getId()))
                .thenReturn(Optional.of(accountAssociation));
        Mockito.when(accountAssociationRepository.save(accountAssociation))
                .thenReturn(accountAssociation);
        Mockito.when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        Mockito.doNothing().when(eventPublisher).publishEvent(Mockito.any(AccountAssociationUpdateEvent.class));

        accountService.updateAssociation(user.getId(), account.getId(), AccessPermission.MANAGE);

        Mockito.verify(accountAssociationRepository).findByUserIdAndAccountId(user.getId(), account.getId());
        Mockito.verify(accountAssociationRepository).save(accountAssociation);
    }

    @Test
    @DisplayName("Save account with existing name")
    public void saveExistingAccount()
    {
        Account account1 = new Account();
        account1.setName("name");

        when(accountRepository.findByName("name")).thenReturn(Optional.of(account1));

        Assertions.assertThrows(DuplicationException.class, () -> this.accountService.createAccount(account1, null, null, null));
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

        this.accountService.createAccount(account1, null, null, null);

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
    @DisplayName("Get all accounts results")
    public void getAllAccountsResults()
    {
        Sort sort = Sort.by("name").descending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);
        Privilege privilege = new Privilege();
        privilege.setLevel(AccessPermission.ADMIN);
        privilege.setName(AppConstants.ACCOUNT_PERMISSION);
        User user = new User();
        user.setPrivileges(Set.of(privilege));

        Mockito.when(accountRepository.findAllResultsBy(pageRequest))
                .thenReturn(List.of());
        mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);

        accountService.getAllAccountsResults(5);

        Mockito.verify(accountRepository).findAllResultsBy(pageRequest);
    }

    @Test
    @DisplayName("Get all accounts results of user")
    public void getAllAccountsResultsOfUser()
    {
        Sort sort = Sort.by("name").descending();
        PageRequest pageRequest = PageRequest.of(0, 5, sort);
        Privilege privilege = new Privilege();
        privilege.setLevel(AccessPermission.ADMIN);
        privilege.setName(AppConstants.USER_PERMISSION);
        User user = new User();
        user.setPrivileges(Set.of(privilege));

        Mockito.when(accountRepository.findAllResultsBy(pageRequest))
                .thenReturn(List.of());
        mockedStatic.when(UserEvaluator::currentUser).thenReturn(user);

        accountService.getAllAccountsResultsOfUser(1L, 5);

        Mockito.verify(accountRepository).findAllResultsBy(pageRequest);
    }

    @Test
    @DisplayName("Create contact for account")
    public void saveContact()
    {
        Account account1 = new Account();
        account1.setName("name");

        when(accountRepository.save(account1)).thenReturn(account1);

        this.accountService.createAccount(account1, null, null, null);
    }

}
