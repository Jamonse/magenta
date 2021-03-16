package com.jsoft.magenta.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.google.common.base.Strings;
import com.jsoft.magenta.accounts.domain.AccountAssociation;
import com.jsoft.magenta.exceptions.AuthorizationException;
import com.jsoft.magenta.files.MagentaImage;
import com.jsoft.magenta.notes.UserNote;
import com.jsoft.magenta.projects.domain.ProjectAssociation;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.security.model.Privilege;
import com.jsoft.magenta.subprojects.SubProject;
import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.util.validation.annotations.ValidName;
import com.jsoft.magenta.util.validation.annotations.ValidPhoneNumber;
import com.jsoft.magenta.worktimes.WorkTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
@Table(
        name = "users",
        uniqueConstraints = { // User email and phone number are unique constrains
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
public class User {
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
    @Email(message = AppConstants.EMAIL_INVALID_MESSAGE)
    private String email;

    @Column(name = "phone_number", length = 20, nullable = false)
    @ValidPhoneNumber
    private String phoneNumber;

    @Column(name = "password", nullable = false)
    //@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = AppConstants.PASSWORD_BLANK_MESSAGE)
    private String password;

    @JoinColumn(
            name = "profile_image",
            foreignKey = @ForeignKey(name = "FK_pimg_user")
    )
    @ManyToOne
    @JsonIgnore
    private MagentaImage profileImage;

    @Column(name = "is_enabled")
    private boolean enabled;

    @Column(name = "pref_theme", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ColorTheme preferredTheme;

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate createdAt;

    @Column(name = "birth_day", nullable = false)
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @NotNull
    private LocalDate birthDay;

    @OneToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Set<AccountAssociation> accounts;

    @OneToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Set<ProjectAssociation> projects;

    @OneToMany(
            cascade = CascadeType.PERSIST,
            fetch = FetchType.LAZY
    )
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Set<WorkTime> workTimes;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private Set<UserNote> notes;

    @ManyToMany(fetch = FetchType.LAZY)
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
    @JsonIgnore
    private Set<SubProject> subProjects;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_privileges",
            joinColumns = @JoinColumn(
                    name = "user_id",
                    referencedColumnName = "user_id",
                    foreignKey = @ForeignKey(name = "FK_user_id")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "privilege_id",
                    referencedColumnName = "privilege_id",
                    foreignKey = @ForeignKey(name = "FK_privilege_id")
            )
    )
    private Set<Privilege> privileges;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "supervised_users",
            joinColumns = @JoinColumn(
                    name = "supervisor_id",
                    referencedColumnName = "user_id",
                    foreignKey = @ForeignKey(name = "FK_supervisor_id")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "supervised_id",
                    referencedColumnName = "user_id",
                    foreignKey = @ForeignKey(name = "FK_supervised_id")
            )
    )
    @JsonIgnore
    private Set<User> supervisedUsers;

    public User(Long userId) {
        this.id = userId;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public boolean hasPermissionGreaterThanEqual(Privilege privilege) {
        if (Strings.isNullOrEmpty(privilege.getName()) || privilege.getLevel() == null)
            return false;
        return this.getPrivileges().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(privilege.getName()) &&
                        p.getLevel().getPermissionLevel() >= privilege.getLevel().getPermissionLevel());
    }

    @JsonIgnore
    public boolean isAccountAdmin() {
        return isAdminOf(AppConstants.ACCOUNT_PERMISSION);
    }

    @JsonIgnore
    public boolean isUserAdmin() {
        return isAdminOf(AppConstants.USER_PERMISSION);
    }

    public boolean isAdminOf(String permissionName) {
        return this.getPrivileges().stream()
                .anyMatch(p -> p.getName().equalsIgnoreCase(permissionName) &&
                        p.getLevel() == AccessPermission.ADMIN);
    }

    @JsonIgnore
    public AccessPermission getAccountsPermission() {
        return getPermission(AppConstants.ACCOUNT_PERMISSION);
    }

    @JsonIgnore
    public AccessPermission getProjectPermission() {
        return getPermission(AppConstants.PROJECT_PERMISSION);
    }

    public boolean isSupervisorOf(User supervised) {
        return getSupervisedUsers().contains(supervised);
    }

    public boolean isSupervisorOf(Long supervisedId) {
        boolean admin = isAdminOf(AppConstants.USER_PERMISSION);
        if (admin)
            return true;
        return getSupervisedUsers().stream()
                .anyMatch(user -> user.getId().equals(supervisedId));
    }

    public void isSupervisorOrOwner(Long ownerId) {
        boolean admin = isAdminOf(AppConstants.USER_PERMISSION);
        if (admin)
            return;
        if (!this.id.equals(ownerId)) {
            boolean isSupervisor = isSupervisorOf(ownerId);
            if (!isSupervisor)
                throw new AuthorizationException("User is not authorized to perform such operation");
        }
    }

    public boolean removeSubProject(SubProject subProject) {
        return getSubProjects().remove(subProject);
    }

    private AccessPermission getPermission(String entityName) {
        AccessPermission accessPermission = getPrivileges().stream()
                .filter(privilege -> privilege.getName().equals(entityName))
                .map(Privilege::getLevel)
                .findFirst()
                .orElse(AccessPermission.READ);
        return accessPermission;
    }

}
