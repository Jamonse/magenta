package com.jsoft.magenta.projects;

import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.projects.domain.ProjectSearchResult;
import com.jsoft.magenta.security.model.AccessPermission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long>
{
    Optional<Project> findByAccountIdAndName(Long accountId, String name);

    Optional<Project> findByIdAndAssociationsUserId(Long projectId, Long userId);

    Page<Project> findAllByAssociationsUserIdAndAssociationsPermission(
            Long userId, AccessPermission accessPermission, Pageable pageable);

    List<ProjectSearchResult> findAllResultsByAssociationsUserIdAndAssociationsPermission(
            Long userId, AccessPermission accessPermission, Pageable pageable);

    List<ProjectSearchResult> findAllByAssociationsUserId(
            Long userId, Pageable pageable);

    List<ProjectSearchResult> findAllResultsBy(Pageable pageable);
}
