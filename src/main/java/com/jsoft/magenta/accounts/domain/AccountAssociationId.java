package com.jsoft.magenta.accounts.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class AccountAssociationId implements Serializable
{
    private Long accountId;
    private Long userId;
}
