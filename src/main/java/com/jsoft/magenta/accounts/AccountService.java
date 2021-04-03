package com.jsoft.magenta.accounts;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.accounts.domain.AccountAssociation;
import com.jsoft.magenta.accounts.domain.AccountSearchResult;
import com.jsoft.magenta.events.accounts.AccountAssociatedEntityEvent;
import com.jsoft.magenta.events.accounts.AccountAssociationCreationEvent;
import com.jsoft.magenta.events.accounts.AccountAssociationUpdateEvent;
import com.jsoft.magenta.events.projects.ProjectAssociationCreationEvent;
import com.jsoft.magenta.events.projects.ProjectAssociationRemovalEvent;
import com.jsoft.magenta.exceptions.AuthorizationException;
import com.jsoft.magenta.exceptions.DuplicationException;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.exceptions.RedundantAssociationException;
import com.jsoft.magenta.files.MagentaImage;
import com.jsoft.magenta.files.MagentaImageService;
import com.jsoft.magenta.files.MagentaImageType;
import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.projects.domain.ProjectSearchResult;
import com.jsoft.magenta.security.SecurityService;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.util.AppDefaults;
import com.jsoft.magenta.util.pagination.PageRequestBuilder;
import com.jsoft.magenta.util.WordFormatter;
import java.time.LocalDate;
import java.util.List;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

  private final AccountRepository accountRepository;
  private final AccountAssociationRepository accountAssociationRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final MagentaImageService imageService;
  private final SecurityService securityService;

  public Account createAccount(
      Account account, MultipartFile coverImage, MultipartFile logoImage,
      MultipartFile profileImage) {
    verifyAccountNameUnique(account.getName());
    String name = WordFormatter.capitalize(account.getName());
    account.setName(name);
    account.setCreatedAt(LocalDate.now());
    MagentaImage cover =
        coverImage != null ? imageService.uploadImage(name, coverImage, MagentaImageType.COVER)
            : null;
    MagentaImage logo =
        logoImage != null ? imageService.uploadImage(name, logoImage, MagentaImageType.LOGO) : null;
    MagentaImage profile = profileImage != null ? imageService.uploadImage(name, profileImage,
        MagentaImageType.PROFILE) : null;
    account.setCoverImage(cover);
    account.setLogo(logo);
    account.setProfileImage(profile);
    return this.accountRepository.save(account);
  }

  public void createAssociation(Long userId, Long accountId, AccessPermission accessPermission) {
    if (accessPermission == AccessPermission.READ) {
      throw new RedundantAssociationException( // READ permission only is redundant
          "READ permission with account without project / sub-projects is redundant");
    }
    isAssociationExists(userId, accountId);
    User user = securityService.currentUser();
    AccessPermission accountsPermission = user.getAccountsPermission();
    switch (accountsPermission) { // Verify requester permission
      case READ: // Must be at least WRITE permission
      case MANAGE:
        throw new AuthorizationException("User is not authorized to perform such operation");
      case WRITE: // Verify that WRITE permission association do exist
        AccessPermission associationPermission = findAssociation(user, accountId);
        if (associationPermission.getPermissionLevel() < AccessPermission.WRITE
            .getPermissionLevel()) {
          throw new AuthorizationException(
              "User is not authorized to perform such operation with specified account");
        }
      case ADMIN: // Create the association
        handleAssociationCreation(userId, accountId, accessPermission);
    }
  }

  public void updateAssociation(Long userId, Long accountId,
      AccessPermission accessPermission) { // Verify that
    // association does exist
    AccountAssociation accountAssociation = findAssociation(userId, accountId);
    if (accessPermission
        == AccessPermission.READ) // If new permission is READ, verify that there are associated
    // projects
    {
      this.eventPublisher
          .publishEvent(new AccountAssociationUpdateEvent(accountId, userId, accessPermission));
    }
    User user = securityService.currentUser();
    AccessPermission accountsPermission = user.getAccountsPermission();
    switch (accountsPermission) { // Verify requester permission
      case READ: // Must be at least WRITE permission
      case MANAGE:
        throw new AuthorizationException("User is not authorized to perform such operation");
      case WRITE: // Verify that WRITE permission association do exist
        AccessPermission associationPermission = findAssociation(user, accountId);
        if (associationPermission.getPermissionLevel() < AccessPermission.WRITE
            .getPermissionLevel()) {
          throw new AuthorizationException(
              "User is not authorized to perform such operation with specified account");
        }
      case ADMIN: // Verify that user has greater than or equal permission of requested association permission
        this.eventPublisher.publishEvent(new AccountAssociationCreationEvent(accountId, userId,
            accessPermission));
        accountAssociation.setPermission(accessPermission);
        this.accountAssociationRepository.save(accountAssociation);
    }
  }

  public Account updateAccountName(Long accountId, String newName) {
    Account accountToUpdate = findAccount(accountId);
    if (!newName.equalsIgnoreCase(accountToUpdate.getName())) {
      verifyAccountNameUnique(newName);
    }
    accountToUpdate.setName(WordFormatter.capitalizeFormat(newName));
    return this.accountRepository.save(accountToUpdate);
  }

  public MagentaImage updateAccountImage(Long accountId, MultipartFile accountImage,
      MagentaImageType imageType) {
    // Fetch account if exists
    Account accountToUpdate = findAccount(accountId);
    MagentaImage image = null;
    switch (imageType) { // Fetch image by type
      case COVER:
        image = accountToUpdate.getCoverImage();
        break;
      case PROFILE:
        image = accountToUpdate.getProfileImage();
        break;
      case LOGO:
        image = accountToUpdate.getLogo();
        break;
      case THUMBNAIL:
        throw new IllegalArgumentException("Account does not have thumbnail image");
    }
    if (image == null) { // Image does not exists -> upload a new one
      MagentaImage magentaImage = this.imageService
          .uploadImage(accountToUpdate.getName(), accountImage,
              imageType);
      switch (imageType) {
        case COVER:
          accountToUpdate.setCoverImage(magentaImage);
          break;
        case PROFILE:
          accountToUpdate.setProfileImage(magentaImage);
          break;
        case LOGO:
          accountToUpdate.setLogo(magentaImage);
          break;
        case THUMBNAIL:
          throw new IllegalArgumentException("Account does not have thumbnail image");
      }
      this.accountRepository.save(accountToUpdate);
      return magentaImage;
    } // Image exists -> perform update
    return this.imageService
        .updateImage(image.getId(), accountToUpdate.getName(), accountImage, imageType);
  }

  private Account findAccount(Long accountId) {
    return this.accountRepository
        .findById(accountId)
        .orElseThrow(() -> new NoSuchElementException("Account not found"));
  }

  public Account getAccountById(Long accountId) {
    User user = securityService.currentUser();
    AccessPermission accountsPermission = user.getAccountsPermission();
    switch (accountsPermission) { // Verify requester permission
      case READ: // Must be at least MANAGE permission
        throw new AuthorizationException("User is not authorized to perform such operation");
      case MANAGE:
      case WRITE: // Verify that WRITE permission association do exist
        AccessPermission associationPermission = findAssociation(user, accountId);
        if (associationPermission.getPermissionLevel() < AccessPermission.MANAGE
            .getPermissionLevel()) {
          throw new AuthorizationException(
              "User is not authorized to perform such operation with specified account");
        }
      case ADMIN:
        return findAccount(accountId);
      default:
        throw new UnsupportedOperationException("User is not authorized with accounts");
    }
  }

  public Page<Account> getAllAccounts(int pageIndex, int pageSize, String sortBy, boolean asc) {
    User user = securityService.currentUser();
    AccessPermission accessPermission = user.getAccountsPermission();
    Page<Account> result;
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
    switch (accessPermission) {
      case READ: // READ permission cannot get accounts
        throw new AuthorizationException("Cannot get accounts with such permission");
      case MANAGE: // MANAGE and WRITE can get only accounts with MANAGE and WRITE associated with them
      case WRITE:
        result = this.accountRepository
            .findAllByAssociationsUserIdAndAssociationsPermissionGreaterThanEqual(
                user.getId(), AccessPermission.MANAGE, pageRequest);
        break;
      case ADMIN: // ADMIN can get all accounts
        result = this.accountRepository.findAll(pageRequest);
        break;
      default:
        return Page.empty();
    }
    return new PageImpl<>(result.getContent(), pageRequest, result.getTotalElements());
  }

  public List<AccountSearchResult> getAllAccountsResults(int resultsCount) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(
        0, resultsCount, AppDefaults.ACCOUNTS_DEFAULT_SORT, false);
    User user = securityService.currentUser();
    boolean isAdmin = user.isAccountAdmin();
    if (!isAdmin) {
      return this.accountRepository.findAllResultsByAssociationsUserId(user.getId(), pageRequest);
    }
    return this.accountRepository.findAllResultsBy(pageRequest);
  }

  public List<AccountSearchResult> getAllAccountsResultsByNameExample(String nameExample,
      int resultsCount) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(
        0, resultsCount, AppDefaults.ACCOUNTS_DEFAULT_SORT, false);
    User user = securityService.currentUser();
    AccessPermission accessPermission = user.getAccountsPermission();
    switch (accessPermission) {
      case READ:
        throw new AuthorizationException("User is not authorized to get such information");
      case MANAGE:
      case WRITE:
        return this.accountRepository.findAllResultsByAssociationsUserIdAndNameContainingIgnoreCase(
            user.getId(), nameExample, pageRequest);
      case ADMIN:
        return this.accountRepository
            .findAllResultsByNameContainingIgnoreCase(nameExample, pageRequest);
      default:
        return List.of();
    }
  }

  public List<AccountSearchResult> getAllAccountsResultsOfUser(Long userId, int resultsCount) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(
        0, resultsCount, AppDefaults.ACCOUNTS_DEFAULT_SORT, false);
    return this.accountRepository.findAllResultsByAssociationsUserId(userId, pageRequest);
  }

  public Page<Project> getAccountProjects(Long accountId, int pageIndex, int pageSize,
      String sortBy, boolean asc) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(
        pageIndex, pageSize, sortBy, asc);
    User user = securityService.currentUser();
    AccessPermission accessPermission = user.getAccountsPermission();
    Page<Project> results;
    if (accessPermission == AccessPermission.ADMIN) {
      results = this.accountRepository.findAllProjectsById(accountId, pageRequest);
    } else {
      AccessPermission associationPermission = findAssociation(user, accountId);
      if (associationPermission == AccessPermission.READ) {
        results = this.accountRepository
            .findAllProjectsByIdAndAssociationsUserId(accountId, user.getId(),
                pageRequest);
      } else {
        results = this.accountRepository.findAllProjectsById(accountId, pageRequest);
      }
    }
    return new PageImpl<>(results.getContent(), pageRequest, results.getTotalElements());
  }

  public List<ProjectSearchResult> getAccountProjectResults(Long accountId, int resultsCount) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(
        0, resultsCount, AppDefaults.ACCOUNTS_DEFAULT_SORT, false);
    User user = securityService.currentUser();
    AccessPermission accessPermission = user.getAccountsPermission();
    if (accessPermission == AccessPermission.ADMIN) {
      return this.accountRepository.findAllProjectsResultsById(accountId, pageRequest);
    }
    AccessPermission associationPermission = findAssociation(user, accountId);
    if (associationPermission == AccessPermission.READ) {
      return this.accountRepository
          .findAllProjectsResultsByIdAndAssociationsUserId(accountId, user.getId(),
              pageRequest);
    }
    return this.accountRepository.findAllProjectsResultsById(accountId, pageRequest);
  }

  public List<ProjectSearchResult> getAccountProjectResultsByNameExample(Long accountId,
      String nameExample,
      int resultsCount) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(
        0, resultsCount, AppDefaults.ACCOUNTS_DEFAULT_SORT, false);
    User user = securityService.currentUser();
    AccessPermission accessPermission = user.getAccountsPermission();
    if (accessPermission == AccessPermission.ADMIN) {
      return this.accountRepository.findAllProjectsResultsByIdAndNameContainingIgnoreCase(accountId,
          nameExample, pageRequest);
    }
    AccessPermission associationPermission = findAssociation(user, accountId);
    if (associationPermission == AccessPermission.READ) {
      return this.accountRepository
          .findAllProjectsResultsByIdAndAssociationsUserIdAndNameContainingIgnoreCase(
              accountId, user.getId(), nameExample, pageRequest);
    }
    return this.accountRepository
        .findAllProjectsResultsByIdAndNameContainingIgnoreCase(accountId, nameExample,
            pageRequest);
  }

  public void removeAccountImage(Long accountId, Long imageId, MagentaImageType imageType) {
    Account account = findAccount(accountId);
    switch (imageType) { // Remove image from account
      case COVER:
        account.setCoverImage(null);
        break;
      case PROFILE:
        account.setProfileImage(null);
        break;
      case LOGO:
        account.setLogo(null);
        break;
      case THUMBNAIL:
        throw new IllegalArgumentException("Account does not have thumbnail image");
    }
    this.accountRepository.save(account);
    // Delete the image from DB
    this.imageService.removeImage(imageId, imageType);
  }

  public void deleteAccount(Long accountId) {
    User user = securityService.currentUser();
    AccessPermission accessPermission = user.getAccountsPermission();
    isAccountExists(accountId);
    switch (accessPermission) {
      case READ:
      case MANAGE:
        throw new AuthorizationException("User is not authorized to perform such action");
      case WRITE:
        AccessPermission associationPermission = findAssociation(user, accountId);
        if (associationPermission.getPermissionLevel() < AccessPermission.WRITE
            .getPermissionLevel()) {
          throw new AuthorizationException("User is not authorized to perform such action");
        }
      case ADMIN:
        this.accountRepository.deleteById(accountId);
    }
    this.accountRepository.deleteById(accountId);
  }

  @EventListener
  public void handleProjectAssociationCreationEvent(
      ProjectAssociationCreationEvent associationCreationEvent) { //
    // Will check for association upon project association creation event
    Long accountId = associationCreationEvent.getPayload();
    Long userId = associationCreationEvent.getAssociatedUserId();
    createAssociationIfNotExists(userId, accountId);
  }

  @EventListener
  public void handleProjectAssociationRemovalEvent(
      ProjectAssociationRemovalEvent associationCreationEvent) { //
    // Will check for redundant association upon project association removal event
    Account account = associationCreationEvent.getPayload().getAccount();
    Long userId = associationCreationEvent.getAssociatedUserId();
    removeRedundantAssociation(userId, account.getId());
  }

  @EventListener
  public void handleAssociatedEntityEvent(AccountAssociatedEntityEvent associatedEntityEvent) {
    Long accountId = associatedEntityEvent.getPayload();
    isAccountExists(accountId);
    User user = securityService.currentUser();
    AccessPermission accessPermission = user.getAccountsPermission();
    switch (accessPermission) {
      case READ:
        throw new AuthorizationException("User cannot perform such operation");
      case MANAGE:
      case WRITE:
        AccessPermission associationPermission = findAssociation(user, accountId);
        if (associationPermission == AccessPermission.READ) {
          throw new AuthorizationException(
              "User association with account is not allowing to perform such " +
                  "operation");
        }
      case ADMIN:
        break;
      default:
        throw new UnsupportedOperationException("User is not associated with accounts");
    }
  }

  private void isAccountExists(Long accountId) {
    boolean exists = this.accountRepository.existsById(accountId);
    if (!exists) {
      throw new NoSuchElementException("Account not found");
    }
  }

  private void isAssociationExists(Long userId, Long accountId) {
    boolean exists = this.accountAssociationRepository // Check if association already exists
        .existsByUserIdAndAccountId(userId, accountId);
    if (exists) {
      throw new DuplicationException("Association between user and account already exists");
    }
  }

  private void verifyAccountNameUnique(String accountName) {
    boolean exists = this.accountRepository
        .existsByName(accountName);
    if (exists) {
      throw new DuplicationException(
          String.format("Account with name %s already exists", accountName));
    }
  }

  private void createAssociationIfNotExists(Long userId, Long accountId) {
    boolean exists = this.accountAssociationRepository
        .existsByUserIdAndAccountId(userId, accountId);
    if (!exists) {
      createReadAssociation(userId, accountId);
    }
  }

  private void createReadAssociation(Long userId, Long accountId) {
    isAccountExists(accountId);
    AccountAssociation accountAssociation = new AccountAssociation(userId, accountId,
        AccessPermission.READ);
    this.eventPublisher.publishEvent(
        new AccountAssociationCreationEvent(accountId, userId, AccessPermission.READ));
    this.accountAssociationRepository.save(accountAssociation);
  }

  private void handleAssociationCreation(Long userId, Long accountId,
      AccessPermission accessPermission) { //
    // Verify association permission is allowed
    this.eventPublisher
        .publishEvent(new AccountAssociationCreationEvent(accountId, userId, accessPermission));
    AccountAssociation accountAssociation = new AccountAssociation(userId, accountId,
        accessPermission);
    this.accountAssociationRepository.save(accountAssociation);
  }

  private AccessPermission findAssociation(User user, Long accountId) {
    return this.accountAssociationRepository
        .findAccessPermissionByUserIdAndAccountId(user.getId(), accountId)
        .orElseThrow(
            () -> new NoSuchElementException("User specified is not associated with account"));
  }

  private AccountAssociation findAssociation(Long userId, Long accountId) {
    AccountAssociation accountAssociation = this.accountAssociationRepository
        .findByUserIdAndAccountId(userId, accountId)
        .orElseThrow(
            () -> new NoSuchElementException("User specified is not associated with account"));
    return accountAssociation;
  }

  private void removeRedundantAssociation(Long userId,
      Long accountId) { // Check if association exists
    this.accountAssociationRepository // If it is, check for redundancy
        .findByUserIdAndAccountId(userId, accountId)
        .ifPresent(this::checkAssociationRedundancy);
  }

  private void checkAssociationRedundancy(
      AccountAssociation accountAssociation) { // Get association permission level
    AccessPermission accessPermission = accountAssociation.getPermission();
    if (accessPermission
        == AccessPermission.READ) { // For READ permission, check for associations with other
      // projects of same account
      boolean associatedWithOtherProjects = this.accountRepository
          .existsByAssociationsUserIdAndProjectsIdGreaterThanEqual(
              accountAssociation.getId().getAccountId(), 0);
      if (!associatedWithOtherProjects) // If there are no other associations,
      {
        this.accountAssociationRepository // Association is redundant, perform delete operation
            .delete(accountAssociation);
      }
    }
  }

}
