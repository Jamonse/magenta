package com.jsoft.magenta.projects;

import com.jsoft.magenta.projects.domain.ProjectAssociation;
import com.jsoft.magenta.projects.domain.ProjectAssociationId;
import com.jsoft.magenta.security.model.AccessPermission;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ProjectAssociationRepository extends CrudRepository<ProjectAssociation, ProjectAssociationId> {
    boolean existsByUserIdAndProjectId(Long userId, Long projectId);

    boolean existsByUserIdAndProjectAccountIdGreaterThanEqual(Long associatedUserId, Long accountId);

    Optional<ProjectAssociation> findByUserIdAndProjectId(Long userId, Long projectId);

    Optional<AccessPermission> findAccessPermissionByUserIdAndProjectId(Long userId, Long projectId);
}
