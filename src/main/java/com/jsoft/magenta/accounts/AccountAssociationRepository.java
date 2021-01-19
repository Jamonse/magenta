package com.jsoft.magenta.accounts;

import com.jsoft.magenta.accounts.domain.AccountAssociation;
import com.jsoft.magenta.accounts.domain.AccountAssociationId;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountAssociationRepository extends CrudRepository<AccountAssociation, AccountAssociationId>
{
    Optional<AccountAssociation> findByUserIdAndAccountId(Long userId, Long accountId);
}
