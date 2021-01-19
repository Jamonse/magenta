package com.jsoft.magenta.accounts.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccountAssociationId implements Serializable
{
    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "user_id")
    private Long userId;
}
