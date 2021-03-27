package com.jsoft.magenta.projects.domain;


import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.users.User;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "users_projects")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class ProjectAssociation implements Serializable {

  @EmbeddedId
  private ProjectAssociationId id;

  @Column(name = "permission", nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private AccessPermission permission;

  @ManyToOne
  @JoinColumn(
      name = "project_id",
      nullable = false,
      insertable = false,
      updatable = false,
      foreignKey = @ForeignKey(
          name = "FK_project_association"
      )
  )
  private Project project;

  @ManyToOne
  @JoinColumn(
      name = "user_id",
      nullable = false,
      insertable = false,
      updatable = false,
      foreignKey = @ForeignKey(
          name = "FK_user_association"
      )
  )
  private User user;

  public ProjectAssociation(User user, Project project, AccessPermission accessPermission) {
    this.id = new ProjectAssociationId(project.getId(), user.getId());
    this.user = user;
    this.project = project;
    this.permission = accessPermission;
  }

  public ProjectAssociation(Long userId, Long projectId, AccessPermission accessPermission) {
    this.id = new ProjectAssociationId(projectId, userId);
    this.permission = accessPermission;
  }
}
