package com.jsoft.magenta.worktimes;

import com.jsoft.magenta.projects.domain.SubProject;
import com.jsoft.magenta.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Table(name = "work_times")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class WorkTime
{
    @Id
    @SequenceGenerator(
            name = "wt_sequence",
            sequenceName = "wt_sequence"
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "wt_sequence"
    )
    @Column(name = "wt_id", updatable = false)
    private Long id;

    @Column(name = "wt_date", nullable = false)
    private LocalDate date;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            foreignKey = @ForeignKey(name = "FK_wts_user")
    )
    private User user;

    @ManyToOne
    @JoinColumn(
            name = "sp_id",
            foreignKey = @ForeignKey(name = "FK_wts_sp")
    )
    private SubProject subProject;

    @Column(name = "wt_amount", nullable = false, precision = 2)
    private double amount;

    @Column(name = "wt_note", nullable = false)
    private String note;
}
