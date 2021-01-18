package com.jsoft.magenta.accounts;

import com.google.common.base.Strings;
import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.accounts.domain.AccountSearchResult;
import com.jsoft.magenta.exceptions.DuplicationException;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.exceptions.UpdateViolationException;
import com.jsoft.magenta.security.UserEvaluator;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.util.PageRequestBuilder;
import com.jsoft.magenta.util.WordFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService
{
    @Value("${application.images.default.account-image}")
    private String accountDefaultImage;

    @Value("${application.images.default.account-background-image}")
    private String accountDefaultBackgroundImage;

    private final AccountRepository accountRepository;

    public Account createAccount(Account account)
    {
        verifyAccountName(account);
        account.setName(WordFormatter.capitalize(account.getName()));
        account.setCreatedAt(LocalDate.now());
        if(Strings.isNullOrEmpty(account.getImage()))
            account.setImage(accountDefaultImage);
        if(Strings.isNullOrEmpty(account.getBackgroundImage()))
            account.setBackgroundImage(accountDefaultBackgroundImage);
        return this.accountRepository.save(account);
    }

    public Account updateAccountName(Long accountId, String newName)
    {
        Account accountToUpdate = this.accountRepository
                .findById(accountId)
                .orElseThrow(() -> new UpdateViolationException("Cannot update id"));
        verifyAccountName(newName);
        accountToUpdate.setName(WordFormatter.capitalizeFormat(newName));
        return this.accountRepository.save(accountToUpdate);
    }

    public Account getAccountByName(String name)
    {
        return this.accountRepository
                .findByName(name)
                .orElseThrow(() -> new NoSuchElementException("Account not found"));
    }

    public Account getAccountById(Long accountId)
    {
        return this.accountRepository
                .findById(accountId)
                .orElseThrow(() -> new NoSuchElementException("Account not found"));
    }

    public Account getAssociatedAccountById(Long accountId)
    {
        Long userId = UserEvaluator.currentUserId();
        return this.accountRepository
                .findByAssociationsUserIdAndId(userId, accountId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Specified account or association with account does not exist"));
    }

    public Page<Account> getAllAccounts(int pageIndex, int pageSize, String sortBy, boolean asc)
    {
        PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
        Page<Account> pageResult = this.accountRepository.findAll(pageRequest);
        return new PageImpl<>(pageResult.getContent(), pageRequest, pageResult.getTotalElements());
    }

    public Page<Account> getAllAccountsByPermissionLevel(
            int pageIndex, int pageSize, String sortBy, boolean asc,
            AccessPermission accessPermission, boolean andLessThan)

    {
        Long userId = UserEvaluator.currentUserId();
        PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
        Page<Account> pageResult =  andLessThan ?
                this.accountRepository
                        .findAllByAssociationsUserIdAndAssociationsPermissionLessThanEqual(userId, accessPermission, pageRequest) :
                this.accountRepository
                        .findAllByAssociationsUserIdAndAssociationsPermission(userId, accessPermission, pageRequest);
        return new PageImpl<>(pageResult.getContent(), pageRequest, pageResult.getTotalElements());
    }

    public List<AccountSearchResult> getAllAccountsByPermissionLevel(
            AccessPermission accessPermission, int maxResultsCount, boolean andGreaterThan)
    {
        PageRequest pageRequest = PageRequest.of(0, maxResultsCount);
        Long userId = UserEvaluator.currentUserId();
        return andGreaterThan ?
                this.accountRepository
                        .findResultsByAssociationsUserIdAndAssociationsPermissionGreaterThanEqual(userId, accessPermission, pageRequest) :
                this.accountRepository
                        .findResultsByAssociationsUserIdAndAssociationsPermission(userId, accessPermission, pageRequest);
    }

    public void deleteAccount(Long accountId)
    {
        getAccountById(accountId);
        this.accountRepository.deleteById(accountId);
    }

    private void verifyAccountName(Account account)
    {
        this.accountRepository
                .findByName(account.getName())
                .ifPresent(this::throwAccountNameExistsException);
    }

    private void verifyAccountName(String accountName)
    {
        this.accountRepository
                .findByName(accountName)
                .ifPresent(this::throwAccountNameExistsException);
    }

    private void throwAccountNameExistsException(Account account)
    {
        throw new DuplicationException(
                String.format("Account with name %s already exists", account.getName()));
    }

}
