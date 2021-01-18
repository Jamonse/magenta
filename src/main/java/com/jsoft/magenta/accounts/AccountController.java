package com.jsoft.magenta.accounts;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.accounts.domain.AccountSearchResult;
import com.jsoft.magenta.accounts.domain.Contact;
import com.jsoft.magenta.security.annotations.accounts.AccountAdminPermission;
import com.jsoft.magenta.security.annotations.accounts.AccountManagePermission;
import com.jsoft.magenta.security.annotations.accounts.AccountReadPermission;
import com.jsoft.magenta.security.annotations.accounts.AccountWritePermission;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.util.validation.ValidName;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.jsoft.magenta.util.AppDefaults.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${application.url}accounts")
public class AccountController
{
    private final AccountService accountService;
    private final ContactService contactService;
    private final String DEFAULT_ACCOUNT_SORT = "name";
    private final String DEFAULT_CONTACT_SORT = "firstName";

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AccountWritePermission
    public Account createAccount(@RequestBody @Valid Account account)
    {
        return this.accountService.createAccount(account);
    }

    @PatchMapping("{accountId}")
    @AccountWritePermission
    public Account updateAccountName(
            @PathVariable Long accountId,
            @RequestBody @ValidName String newName
    )
    {
        return this.accountService.updateAccountName(accountId, newName);
    }

    @GetMapping
    @AccountAdminPermission
    public Page<Account> getAllAccounts(
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = DEFAULT_ACCOUNT_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    )
    {
        return this.accountService.getAllAccounts(pageIndex, pageSize, sortBy, asc);
    }

    @GetMapping("{accountId}")
    @AccountWritePermission
    public Account getAccountById(@PathVariable Long accountId)
    {
        return this.accountService.getAccountById(accountId);
    }

    @GetMapping("associated/{accountId}")
    @AccountManagePermission
    public Account getAccountByIdAndAssociation(@PathVariable Long accountId)
    {
        return this.accountService.getAssociatedAccountById(accountId);
    }

    @GetMapping("manage")
    @AccountManagePermission
    public Page<Account> getManagementAssociatedAccounts(
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue =  DEFAULT_ACCOUNT_SORT)String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    )
    {
        return this.accountService.getAllAccountsByPermissionLevel(
                pageIndex, pageSize, sortBy, asc, AccessPermission.MANAGE, false);
    }

    @GetMapping("edit")
    @AccountWritePermission
    public Page<Account> getEditAssociatedAccounts(
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = DEFAULT_ACCOUNT_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    )
    {
        return this.accountService.getAllAccountsByPermissionLevel(
                pageIndex, pageSize, sortBy, asc, AccessPermission.WRITE, false);
    }

    @GetMapping("allowed")
    @AccountReadPermission
    public List<AccountSearchResult> getAllowedAssociatedAccounts(
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int maxResultsCount
    )
    {
        return this.accountService.getAllAccountsByPermissionLevel(
                AccessPermission.READ, maxResultsCount, true);
    }

    @DeleteMapping("{accountId}")
    @AccountAdminPermission
    public void deleteAccount(@PathVariable Long accountId)
    {
        this.accountService.deleteAccount(accountId);
    }

    @PostMapping("contacts/{accountId}")
    @ResponseStatus(HttpStatus.CREATED)
    @AccountManagePermission
    public Contact createContact(
            @PathVariable Long accountId,
            @RequestBody @Valid Contact contact
    )
    {
        return this.contactService.createContact(accountId, contact);
    }

    @PutMapping("contacts/{accountId}")
    @AccountManagePermission
    public Contact updateContact(
            @PathVariable Long accountId,
            @RequestBody @Valid Contact contact
    )
    {
        return this.contactService.updateContact(accountId, contact);
    }

    @GetMapping("contacts/{accountId}")
    @AccountReadPermission
    public Page<Contact> getAllContacts(
            @PathVariable Long accountId,
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = DEFAULT_CONTACT_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    )
    {
        return this.contactService.getAllContacts(accountId, pageIndex, pageSize, sortBy, asc);
    }

    @DeleteMapping("contacts/{contactId}")
    @AccountManagePermission
    public void deleteContact(@PathVariable Long contactId)
    {
        this.contactService.deleteContact(contactId);
    }

}
