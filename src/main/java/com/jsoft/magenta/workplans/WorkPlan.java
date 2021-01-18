package com.jsoft.magenta.workplans;

import com.jsoft.magenta.projects.domain.SubProject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "work_plans")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class WorkPlan
{
    @Id
    @SequenceGenerator(
            name = "wp_sequence",
            sequenceName = "wp_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "wp_sequence"
    )
    @Column(name = "wp_id", updatable = false)
    private Long id;

    @Column(name = "title", length = 50)
    @Size(min = 2, max = 50)
    private String title;

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(
            name = "sp_id",
            foreignKey = @ForeignKey(name = "FK_wps_sp")
    )
    private SubProject subProject;
}
