package com.jsoft.magenta.users;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jsoft.magenta.accounts.domain.AccountAssociation;
import com.jsoft.magenta.notes.UserNote;
import com.jsoft.magenta.projects.domain.ProjectAssociation;
import com.jsoft.magenta.projects.domain.SubProject;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.util.validation.ValidName;
import com.jsoft.magenta.worktimes.WorkTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "user_email_unique",
                        columnNames = "email"
                ),
                @UniqueConstraint(
                        name = "user_pn_unique",
                        columnNames = "phone_number"
                )
        }
)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class User
{
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            initialValue = 150,
            allocationSize = 2
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "user_sequence"
    )
    @Column(name = "user_id", updatable = false)
    private Long id;

    @Column(name = "first_name", length = 50, nullable = false)
    @ValidName
    private String firstName;

    @Column(name = "last_name", length = 50, nullable = false)
    @ValidName
    private String lastName;

    @Column(name = "email", length = 50, nullable = false)
    @Email
    private String email;

    @Column(name = "phone_number", length = 20, nullable = false)
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "image")
    private String image;

    @Column(name = "is_enabled")
    private boolean enabled;

    @Column(name = "pref_theme", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ColorTheme preferredTheme;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @Column(name = "birth_day", nullable = false)
    @NotNull
    private LocalDate birthDay;

    @OneToMany(
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE}
    )
    @JoinColumn(name = "uid")
    private Set<AccountAssociation> accounts;

    @OneToMany(
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE}
    )
    @JoinColumn(name = "uid")
    private Set<ProjectAssociation> projects;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private Set<WorkTime> workTimes;

    @OneToMany(
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE}
    )
    @JoinColumn(name = "user_id")
    private Set<UserNote> notes;

    @ManyToMany
    @JoinTable(
            name = "users_sps",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "user_id",
                    foreignKey = @ForeignKey(name = "FK_user_id")),
            inverseJoinColumns = @JoinColumn(
                    name = "sp_id",
                    referencedColumnName = "sp_id",
                    foreignKey = @ForeignKey(name = "FK_sp_id"))
    )
    private Set<SubProject> subProjects;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_privileges",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "user_id",
                    foreignKey = @ForeignKey(name = "FK_user_id")),
            inverseJoinColumns = @JoinColumn(
                    name = "privilege_id",
                    referencedColumnName = "privilege_id",
                    foreignKey = @ForeignKey(name = "FK_privilege_id"))
    )
    private Set<Privilege> privileges;

    public String getName()
    {
        return firstName + " " + lastName;
    }
}
