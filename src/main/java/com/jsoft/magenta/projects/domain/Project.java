package com.jsoft.magenta.projects.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsoft.magenta.accounts.domain.Account;
import com.jsoft.magenta.orders.domain.Order;
import com.jsoft.magenta.subprojects.SubProject;
import com.jsoft.magenta.util.validation.annotations.ValidName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Set;

@Data
@Entity
@Table(name = "projects")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Project
{
    @Id
    @SequenceGenerator(
            name = "project_sequence",
            sequenceName = "project_sequence",
            allocationSize = 100,
            initialValue = 2
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "project_sequence"
    )
    @Column(name = "project_id", updatable = false)
    private Long id;

    @Column(name = "project_name", length = 50, nullable = false)
    @ValidName
    private String name;

    @Column(name = "is_available", nullable = false)
    private boolean available;

    @ManyToOne
    @JoinColumn(
            name = "account_id",
            foreignKey = @ForeignKey(name = "FK_projects_account")
    )
    private Account account;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(
            name = "project_id",
            referencedColumnName = "project_id",
            foreignKey = @ForeignKey(name = "FK_project_order")
    )
    @Valid
    private Set<Order> orders;

    @OneToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REMOVE
    })
    @JoinColumn(
            name = "project_id",
            referencedColumnName = "project_id",
            foreignKey = @ForeignKey(name = "FK_project_sp")
    )
    @Valid
    private Set<SubProject> subProjects;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REMOVE})
    @JoinColumn(name = "project_id")
    @JsonIgnore
    private Set<ProjectAssociation> associations;

    public Project(Long projectId)
    {
        this.id = projectId;
    }
}
