package com.jsoft.magenta.security.model;

import com.jsoft.magenta.util.validation.annotations.ValidName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@Table(name = "privileges_groups")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class PrivilegesGroup {
    @Id
    @SequenceGenerator(
            name = "pg_sequence",
            sequenceName = "pg_sequence",
            initialValue = 200,
            allocationSize = 5
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "pg_sequence"
    )
    @Column(name = "pg_id", updatable = false)
    private Long id;

    @Column(name = "pg_name", length = 50, nullable = false, unique = true)
    @ValidName
    private String name;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "privileges_vs_groups",
            joinColumns = @JoinColumn(
                    name = "pg_id",
                    referencedColumnName = "pg_id",
                    foreignKey = @ForeignKey(name = "FK_group_privilege")
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "privilege_id",
                    referencedColumnName = "privilege_id",
                    foreignKey = @ForeignKey(name = "FK_privilege_group")
            )
    )
    @Valid
    private Set<Privilege> privileges = new HashSet<>();

    public void addAll(Set<Privilege> privileges) {
        privileges.removeIf(privilege -> privileges.contains(privilege));
        this.privileges.addAll(privileges);
    }
}
