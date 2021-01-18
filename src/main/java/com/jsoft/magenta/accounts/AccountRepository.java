package com.jsoft.magenta.accounts;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.accounts.domain.AccountSearchResult;
import com.jsoft.magenta.security.model.AccessPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long>
{
    Optional<Account> findByName(String name);

    Optional<Account> findByAssociationsUserIdAndId(Long userId, Long accountId);

    List<AccountSearchResult> findResultsByAssociationsUserIdAndAssociationsPermission(
            Long userId, AccessPermission accessPermission, Pageable pageable);

    List<AccountSearchResult> findResultsByAssociationsUserIdAndAssociationsPermissionGreaterThanEqual(
            Long userId, AccessPermission accessPermission, Pageable pageable);

    Page<Account> findAllByAssociationsUserIdAndAssociationsPermission(
            Long userId, AccessPermission accessPermission, Pageable pageable);

    Page<Account> findAllByAssociationsUserIdAndAssociationsPermissionLessThanEqual(
            Long userId, AccessPermission accessPermission, Pageable pageable);

    Page<AccountSearchResult> findResultsByAssociationsUserIdAndAssociationsPermissionLessThanEqual(
            Long userId, AccessPermission accessPermission, Pageable pageable);

    Page<AccountSearchResult> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<AccountSearchResult> findAllByNameContainingIgnoreCaseAndAssociationsUserIdAndAssociationsPermission(
            String name, Long userId, AccessPermission accessPermission, Pageable pageable);
}
