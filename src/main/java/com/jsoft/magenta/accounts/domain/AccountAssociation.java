package com.jsoft.magenta.accounts.domain;

import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@IdClass(AccountAssociation.class)
@Table(name = "users_accounts")
@NoArgsConstructor
@AllArgsConstructor
public class AccountAssociation implements Serializable
{
    @Id
    private Long accountId;

    @Id
    private Long userId;

    @Column(name = "permission", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private AccessPermission permission;

    @ManyToOne
    @JoinColumn(
            name = "acc_id",
            nullable = false,
            insertable = false,
            updatable = false,
            referencedColumnName = "account_id",
            foreignKey = @ForeignKey(
                    name = "FK_account_association"
            )
    )
    private Account account;

    @ManyToOne
    @JoinColumn(
            name = "uid",
            nullable = false,
            insertable = false,
            updatable = false,
            referencedColumnName = "user_id",
            foreignKey = @ForeignKey(
                    name = "FK_user_association"
            )
    )
    private User user;
}
