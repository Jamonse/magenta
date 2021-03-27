package com.jsoft.magenta.projects.domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProjectAssociationId implements Serializable {

  @Column(name = "project_id")
  private Long projectId;

  @Column(name = "user_id")
  private Long userId;
}
