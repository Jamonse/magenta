package com.jsoft.magenta.accounts;

import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.accounts.domain.AccountSearchResult;
import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.projects.domain.ProjectSearchResult;
import com.jsoft.magenta.security.model.AccessPermission;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

  boolean existsByName(String accountName);

  boolean existsByAssociationsUserIdAndProjectsIdGreaterThanEqual(Long accountId, int projectId);

  Optional<Account> findByName(String name);

  Page<AccountSearchResult> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

  Page<Account> findAllByAssociationsUserIdAndAssociationsPermissionGreaterThanEqual(
      Long userId, AccessPermission accessPermission, Pageable pageable);

  List<AccountSearchResult> findAllResultsBy(Pageable pageable);

  List<AccountSearchResult> findAllResultsByAssociationsUserId(Long userId, Pageable pageable);

  List<AccountSearchResult> findAllResultsByAssociationsUserIdAndNameContainingIgnoreCase(
      Long id, String nameExample, Pageable pageable);

  List<AccountSearchResult> findAllResultsByNameContainingIgnoreCase(String nameExample,
      Pageable pageable);

  List<ProjectSearchResult> findAllProjectsResultsById(Long accountId, Pageable pageable);

  List<ProjectSearchResult> findAllProjectsResultsByIdAndAssociationsUserId(
      Long accountId, Long id, Pageable pageable);

  List<ProjectSearchResult> findAllProjectsResultsByIdAndNameContainingIgnoreCase(
      Long accountId, String nameExample, Pageable pageable);

  List<ProjectSearchResult> findAllProjectsResultsByIdAndAssociationsUserIdAndNameContainingIgnoreCase(
      Long accountId, Long userId, String nameExample, Pageable pageable);

  Page<Project> findAllProjectsById(Long accountId, PageRequest pageRequest);

  Page<Project> findAllProjectsByIdAndAssociationsUserId(Long accountId, Long id,
      PageRequest pageRequest);
}
