package com.jsoft.magenta.accounts.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.util.validation.ValidName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
@Table(
        name = "accounts",
        uniqueConstraints = @UniqueConstraint(
                name = "account_name_unique",
                columnNames = "account_name")
)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Account
{
    @Id
    @SequenceGenerator(
            name = "account_sequence",
            sequenceName = "account_sequence",
            initialValue = 100
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "account_sequence"
    )
    @Column(name = "account_id", updatable = false)
    private Long id;

    @Column(name = "account_name", nullable = false, length = 50)
    @ValidName
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate createdAt;

    @Column(name = "account_image", nullable = false)
    private String image;

    @Column(name = "account_background_image", nullable = false)
    private String backgroundImage;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(
            name = "account_id",
            referencedColumnName = "account_id",
            foreignKey = @ForeignKey(name = "FK_account_projects")
    )
    private Set<Project> projects;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(
            name = "account_id",
            referencedColumnName = "account_id",
            foreignKey = @ForeignKey(name = "FK_account_contacts")
    )
    private Set<Contact> contacts;

    @OneToMany(mappedBy = "account")
    private Set<AccountAssociation> associations;
}
