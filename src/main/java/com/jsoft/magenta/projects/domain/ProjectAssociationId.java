package com.jsoft.magenta.projects.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProjectAssociationId implements Serializable
{
    private Long projectId;
    private Long userId;
}
