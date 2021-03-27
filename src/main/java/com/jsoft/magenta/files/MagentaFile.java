package com.jsoft.magenta.files;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public abstract class MagentaFile {

  @Id
  @SequenceGenerator(
      name = "file_sequence",
      sequenceName = "file_sequence",
      initialValue = 100
  )
  @GeneratedValue(
      strategy = GenerationType.SEQUENCE,
      generator = "file_sequence"
  )
  @Column(name = "file_id", updatable = false)
  protected Long id;

  @Column(name = "file_type", nullable = false, updatable = false)
  protected String type;

  @Column(name = "file_name", nullable = false)
  protected String name;
}
