package com.jsoft.magenta.accounts;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.accounts.domain.AccountSearchResult;
import com.jsoft.magenta.files.MagentaImage;
import com.jsoft.magenta.files.MagentaImageType;
import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.projects.domain.ProjectSearchResult;
import com.jsoft.magenta.security.annotations.accounts.AccountAdminPermission;
import com.jsoft.magenta.security.annotations.accounts.AccountWritePermission;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.util.validation.annotations.ValidName;
import com.jsoft.magenta.util.validation.annotations.ValidPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

import static com.jsoft.magenta.util.AppDefaults.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("${application.url}accounts")
public class AccountController
{
    private final AccountService accountService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @AccountWritePermission
    public Account createAccount(
            @RequestParam(required = false) MultipartFile coverImage,
            @RequestParam(required = false) MultipartFile profileImage,
            @RequestParam(required = false) MultipartFile logoImage,
            @RequestBody @Valid Account account
    )
    {
        return this.accountService.createAccount(account, coverImage, logoImage, profileImage);
    }

    @PostMapping("{accountId}/association/{userId}")
    @AccountWritePermission
    public void createAssociation(
            @PathVariable Long accountId,
            @PathVariable Long userId,
            @RequestBody @ValidPermission String permission
    )
    {
        AccessPermission accessPermission = AccessPermission.valueOf(permission.toUpperCase());
        this.accountService.createAssociation(userId, accountId, accessPermission);
    }

    @PatchMapping("{accountId}/association/{userId}")
    @AccountWritePermission
    public void updateAssociationPermission(
            @PathVariable Long accountId,
            @PathVariable Long userId,
            @RequestBody @ValidPermission String permission
    )
    {
        AccessPermission accessPermission = AccessPermission.valueOf(permission.toUpperCase());
        this.accountService.updateAssociation(userId, accountId, accessPermission);
    }

    @PatchMapping("name/{accountId}")
    @AccountWritePermission
    public Account updateAccountName(
            @PathVariable Long accountId,
            @RequestBody @ValidName String newName
    )
    {
        return this.accountService.updateAccountName(accountId, newName);
    }

    @PatchMapping("cover/{accountId}")
    @AccountWritePermission
    public MagentaImage updateAccountCoverImage(
            @PathVariable("{accountId}") Long accountId,
            @RequestParam MultipartFile coverImage
    )
    {
        return this.accountService.updateAccountImage(accountId, coverImage, MagentaImageType.COVER);
    }

    @PatchMapping("profile/{accountId}")
    @AccountWritePermission
    public MagentaImage updateAccountProfileImage(
            @PathVariable("{accountId}") Long accountId,
            @RequestParam MultipartFile profileImage
    )
    {
        return this.accountService.updateAccountImage(accountId, profileImage, MagentaImageType.PROFILE);
    }

    @PatchMapping("logo/{accountId}")
    @AccountWritePermission
    public MagentaImage updateAccountLogoImage(
            @PathVariable("{accountId}") Long accountId,
            @RequestParam MultipartFile logoImage
    )
    {
        return this.accountService.updateAccountImage(accountId, logoImage, MagentaImageType.LOGO);
    }

    @GetMapping
    @AccountAdminPermission
    public Page<Account> getAllAccounts(
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = ACCOUNTS_DEFAULT_SORT) String sortBy,
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

    @GetMapping("results")
    public List<AccountSearchResult> getAllAccountsResults(
            @RequestParam(required = false, defaultValue = RESULTS_COUNT) int resultsCount
    )
    {
        return this.accountService.getAllAccountsResults(resultsCount);
    }

    @GetMapping("results/{userId}")
    public List<AccountSearchResult> getAllAccountsResultsOfUser(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = RESULTS_COUNT) int resultsCount
    )
    {
        return this.accountService.getAllAccountsResultsOfUser(userId, resultsCount);
    }

    @GetMapping("search")
    public List<AccountSearchResult> getAllAccountsByNameExample(
            @RequestParam String nameExample,
            @RequestParam(required = false, defaultValue = RESULTS_COUNT) int resultsCount
    )
    {
        return this.accountService.getAllAccountsResultsByNameExample(nameExample, resultsCount);
    }

    @GetMapping("{accountId}/projects")
    public Page<Project> getAccountProjects(
            @PathVariable Long accountId,
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = ACCOUNTS_DEFAULT_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    )
    {
        return this.accountService.getAccountProjects(accountId, pageIndex, pageSize, sortBy, asc);
    }

    @GetMapping("{accountId}/projects/results")
    public List<ProjectSearchResult> getAccountProjectResults(
            @PathVariable Long accountId,
            @RequestParam(required = false, defaultValue = RESULTS_COUNT) int resultsCount,
            @RequestParam(required = false) String nameExample
    )
    {
        if(nameExample == null)
            return this.accountService.getAccountProjectResults(accountId, resultsCount);
        return this.accountService.getAccountProjectResultsByNameExample(accountId, nameExample, resultsCount);
    }

    @DeleteMapping("cover/{accountId}/{imageId}")
    @AccountWritePermission
    public void removeAccountCoverImage(
            @PathVariable Long accountId,
            @PathVariable Long imageId
    )
    {
        this.accountService.removeAccountImage(accountId, imageId, MagentaImageType.COVER);
    }

    @DeleteMapping("profile/{accountId}/{imageId}")
    @AccountWritePermission
    public void removeAccountProfileImage(
            @PathVariable Long accountId,
            @PathVariable Long imageId
    )
    {
        this.accountService.removeAccountImage(accountId, imageId, MagentaImageType.PROFILE);
    }

    @DeleteMapping("logo/{accountId}/{imageId}")
    @AccountWritePermission
    public void removeAccountLogoImage(
            @PathVariable Long accountId,
            @PathVariable Long imageId
    )
    {
        this.accountService.removeAccountImage(accountId, imageId, MagentaImageType.LOGO);
    }

    @DeleteMapping("{accountId}")
    @AccountAdminPermission
    public void deleteAccount(@PathVariable Long accountId)
    {
        this.accountService.deleteAccount(accountId);
    }
}
