package com.jsoft.magenta.subprojects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.util.validation.annotations.PositiveNumber;
import com.jsoft.magenta.util.validation.annotations.ValidName;
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
    @PositiveNumber
    private Double amountOfHours;

    @ManyToOne
    @JoinColumn(
            name = "project_id",
            foreignKey = @ForeignKey(name = "FK_sps_project")
    )
    @JsonIgnore
    private Project project;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})
    @JoinColumn(
            name = "sp_id",
            referencedColumnName = "sp_id",
            foreignKey = @ForeignKey(name = "FK_sp_wt")
    )
    @JsonIgnore
    private Set<WorkTime> workTimes;

    @ManyToMany(mappedBy = "subProjects")
    @JsonIgnore
    private Set<User> users;

    public SubProject(Long spId)
    {
        this.id = spId;
    }

    public void removeAssociation(Long userId)
    {
        getUsers().removeIf(user -> user.getId().equals(userId));
    }
}
