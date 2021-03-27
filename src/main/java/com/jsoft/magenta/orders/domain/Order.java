package com.jsoft.magenta.orders.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.util.validation.annotations.PositiveNumber;
import com.jsoft.magenta.util.validation.annotations.ValidContent;
import com.jsoft.magenta.util.validation.annotations.ValidTitle;
import java.time.LocalDate;
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
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Order {

  @Id
  @SequenceGenerator(
      name = "order_sequence",
      sequenceName = "order_sequence",
      initialValue = 100
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "order_sequence"
  )
  @Column(name = "order_id", updatable = false)
  private Long id;

  @Column(name = "title", length = 50, nullable = false)
  @ValidTitle
  private String title;

  @Column(name = "description", nullable = false)
  @ValidContent
  private String description;

  @Column(name = "created_at", nullable = false, updatable = false)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate createdAt;

  @ManyToOne
  @JoinColumn(
      name = "project_id",
      foreignKey = @ForeignKey(name = "FK_orders_project")
  )
  @JsonIgnore
  private Project project;

  @Column(name = "order_amount", nullable = false, precision = 2)
  @PositiveNumber
  @NotNull
  private double amount;
}
