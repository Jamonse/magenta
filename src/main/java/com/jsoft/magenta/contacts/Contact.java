package com.jsoft.magenta.contacts;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.util.validation.annotations.ValidName;
import com.jsoft.magenta.util.validation.annotations.ValidPhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "contacts")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Contact {
    @Id
    @SequenceGenerator(
            name = "ac_sequence",
            sequenceName = "ac_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "ac_sequence"
    )
    @Column(name = "contact_id", updatable = false)
    private Long id;

    @Column(name = "first_name", length = 50, nullable = false)
    @ValidName
    private String firstName;

    @Column(name = "last_name", length = 50, nullable = false)
    @ValidName
    private String lastName;

    @ManyToOne
    @JoinColumn(
            name = "account_id",
            foreignKey = @ForeignKey(name = "FK_contacts_account")
    )
    @JsonIgnore
    private Account account;

    @Column(name = "email", length = 50, nullable = false)
    @Email
    @NotNull
    private String email;

    @Column(name = "phone_number", length = 50, nullable = false)
    @ValidPhoneNumber
    private String phoneNumber;
}
