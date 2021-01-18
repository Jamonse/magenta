package com.jsoft.magenta.projects;

import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.security.model.AccessPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long>
{
    Optional<Project> findByIdAndAssociationsUserId(Long projectId, Long userId);

    Page<Project> findAllByAssociationsUserIdAndAssociationsPermission(
            Long userId, AccessPermission accessPermission, Pageable pageable);
}
