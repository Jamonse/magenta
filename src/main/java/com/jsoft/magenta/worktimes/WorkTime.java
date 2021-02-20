package com.jsoft.magenta.worktimes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import com.jsoft.magenta.subprojects.SubProject;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.util.validation.annotations.ValidContent;
import com.jsoft.magenta.util.validation.annotations.ValidHoursAmount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
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

    @Column(name = "wt_date", nullable = false, updatable = false)
    @NotNull(message = "Work time date must not be null")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate date;

    @Column(name = "start_time")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime startTime;

    @Column(name = "end_time")
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_wts_user")
    )
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(
            name = "sp_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "FK_wts_sp")
    )
    @JsonIgnore
    private SubProject subProject;

    @Column(name = "wt_amount", nullable = false, precision = 2)
    @ValidHoursAmount
    private Double amount;

    @Column(name = "wt_note", nullable = false)
    @ValidContent
    private String note = "";
}
