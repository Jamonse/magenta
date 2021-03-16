package com.jsoft.magenta.accounts.domain;

import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "users_accounts")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class AccountAssociation implements Serializable {
    @EmbeddedId
    private AccountAssociationId id;

    @Column(name = "permission", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private AccessPermission permission;

    @ManyToOne
    @JoinColumn(
            name = "account_id",
            nullable = false,
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(
                    name = "FK_account_association"
            )
    )
    private Account account;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(
                    name = "FK_user_association"
            )
    )
    private User user;

    public AccountAssociation(User user, Account account, AccessPermission accessPermission) {
        this.id = new AccountAssociationId(account.getId(), user.getId());
        this.account = account;
        this.user = user;
        this.permission = accessPermission;
    }

    public AccountAssociation(Long userId, Long accountId, AccessPermission read) {
        this.id = new AccountAssociationId(accountId, userId);
    }
}
