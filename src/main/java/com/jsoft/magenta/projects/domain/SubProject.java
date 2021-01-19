package com.jsoft.magenta.projects.domain;

import com.jsoft.magenta.users.User;
import com.jsoft.magenta.util.validation.ValidName;
import com.jsoft.magenta.workplans.WorkPlan;
import com.jsoft.magenta.worktimes.WorkTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@Entity
@Table(name = "sub_projects")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class SubProject
{
    @Id
    @SequenceGenerator(
            name = "sp_sequence",
            sequenceName = "sp_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "sp_sequence"
    )
    @Column(name = "sp_id", updatable = false)
    private Long id;

    @Column(name = "sp_name", length = 50, nullable = false)
    @ValidName
    private String name;

    @Column(name = "is_available")
    private boolean available;

    @Column(name = "sp_amount", precision = 2)
    private double amountOfHours;

    @ManyToOne
    @JoinColumn(
            name = "project_id",
            foreignKey = @ForeignKey(name = "FK_sps_project")
    )
    private Project project;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(
            name = "sp_id",
            referencedColumnName = "sp_id",
            foreignKey = @ForeignKey(name = "FK_sp_wt")
    )
    private Set<WorkTime> workTimes;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(
            name = "sp_id",
            referencedColumnName = "sp_id",
            foreignKey = @ForeignKey(name = "FK_sp_wp")
    )
    private Set<WorkPlan> workPlans;

    @ManyToMany(mappedBy = "subProjects")
    private Set<User> users;
}
