package com.jsoft.magenta.accounts;

import com.jsoft.magenta.accounts.domain.AccountAssociation;
import com.jsoft.magenta.accounts.domain.AccountAssociationId;
import com.jsoft.magenta.security.model.AccessPermission;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountAssociationRepository extends CrudRepository<AccountAssociation, AccountAssociationId> {
    boolean existsByUserIdAndAccountId(Long userId, Long accountId);

    Optional<AccountAssociation> findByUserIdAndAccountId(Long userId, Long accountId);

    Optional<AccessPermission> findAccessPermissionByUserIdAndAccountId(Long id, Long accountId);
}
