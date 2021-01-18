package com.jsoft.magenta.projects.domain;


import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@IdClass(ProjectAssociationId.class)
@Table(name = "users_projects")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProjectAssociation implements Serializable
{
    @Id
    private Long projectId;

    @Id
    private Long userId;

    @Column(name = "permission", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private AccessPermission permission;

    @ManyToOne
    @JoinColumn(
            name = "pid",
            nullable = false,
            insertable = false,
            updatable = false,
            referencedColumnName = "project_id",
            foreignKey = @ForeignKey(
                    name = "FK_project_association"
            )
    )
    private Project project;

    @ManyToOne
    @JoinColumn(
            name = "uid",
            nullable = false,
            insertable = false,
            updatable = false,
            referencedColumnName = "user_id",
            foreignKey = @ForeignKey(
                    name = "FK_user_association"
            )
    )
    private User user;
}
