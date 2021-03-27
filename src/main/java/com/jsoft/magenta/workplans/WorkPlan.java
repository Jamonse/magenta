package com.jsoft.magenta.workplans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.util.validation.annotations.ValidTitle;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "work_plans")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class WorkPlan {

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
  @ValidTitle
  private String title;

  @Column(name = "start_date", nullable = false)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @NotNull(message = "Start date is required")
  private LocalDateTime startDate;

  @Column(name = "end_date", nullable = false)
  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
  @JsonSerialize(using = LocalDateTimeSerializer.class)
  @NotNull(message = "End date is required")
  private LocalDateTime endDate;

  @ManyToOne
  @JoinColumn(
      name = "sp_id",
      foreignKey = @ForeignKey(name = "FK_wps_user")
  )
  @JsonIgnore
  private User user;
}
