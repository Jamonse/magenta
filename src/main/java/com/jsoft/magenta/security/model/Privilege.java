package com.jsoft.magenta.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.util.validation.annotations.ValidName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "privileges")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Privilege {
    @Id
    @SequenceGenerator(
            name = "privilege_sequence",
            sequenceName = "privilege_sequence",
            initialValue = 200,
            allocationSize = 5
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "privilege_sequence"
    )
    @Column(name = "privilege_id", updatable = false)
    private Long id;

    @Column(name = "privilege_name", length = 50, nullable = false)
    @ValidName
    private String name;

    @Column(name = "privilege_level", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private AccessPermission level;

    @ManyToMany(mappedBy = "privileges")
    @JsonIgnore
    private Set<User> privilegedUsers;

    @ManyToMany(mappedBy = "privileges")
    @JsonIgnore
    private Set<PrivilegesGroup> groups;
}
