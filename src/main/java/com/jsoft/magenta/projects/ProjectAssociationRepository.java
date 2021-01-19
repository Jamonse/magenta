package com.jsoft.magenta.projects;

import com.jsoft.magenta.projects.domain.ProjectAssociation;
import com.jsoft.magenta.projects.domain.ProjectAssociationId;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProjectAssociationRepository extends CrudRepository<ProjectAssociation, ProjectAssociationId>
{
    Optional<ProjectAssociation> findByUserIdAndProjectId(Long userId, Long projectId);
}
