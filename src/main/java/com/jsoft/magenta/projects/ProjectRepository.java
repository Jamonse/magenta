package com.jsoft.magenta.projects;

import com.jsoft.magenta.projects.domain.Project;
import com.jsoft.magenta.projects.domain.ProjectSearchResult;
import com.jsoft.magenta.security.model.AccessPermission;
import com.jsoft.magenta.subprojects.SubProject;
import com.jsoft.magenta.subprojects.SubProjectSearchResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    boolean existsByAccountIdAndName(Long accountId, String projectName);

    boolean existsByAssociationsUserIdAndSubProjectsIdGreaterThanEqual(Long userId, int subProjectsId);

    Optional<Long> findAccountIdById(Long projectId);

    Optional<Project> findByAccountIdAndName(Long accountId, String name);

    Optional<Project> findByIdAndAssociationsUserId(Long projectId, Long userId);

    Page<Project> findAllByAccountId(Long accountId, PageRequest pageRequest);

    Page<Project> findAllByAssociationsUserIdAndAssociationsPermission(
            Long userId, AccessPermission accessPermission, Pageable pageable);

    Page<Project> findAllByAssociationsUserIdAndAssociationsPermissionGreaterThanEqual(
            Long userId, AccessPermission accessPermission, Pageable pageable);

    Page<Project> findAllByAccountIdAndAssociationsUserIdAndAssociationsPermission(
            Long accountId, Long userId, AccessPermission accessPermission, PageRequest pageRequest);

    Page<Project> findAllByAccountIdAndAssociationsUserIdAndAssociationsPermissionGreaterThanEqual(
            Long accountId, Long userId, AccessPermission accessPermission, PageRequest pageRequest);

    List<ProjectSearchResult> findAllResultsByAssociationsUserIdAndAssociationsPermission(
            Long userId, AccessPermission accessPermission, Pageable pageable);

    List<ProjectSearchResult> findAllResultsByAccountIdAndAssociationsUserId(
            Long accountId, Long userId, Pageable pageable);

    List<ProjectSearchResult> findAllResultsByAccountId(Long accountId, PageRequest pageRequest);

    List<ProjectSearchResult> findAllByAssociationsUserId(
            Long userId, Pageable pageable);

    List<ProjectSearchResult> findAllResultsBy(Pageable pageable);

    List<ProjectSearchResult> findAllResultsByAssociationsUserIdAndNameContainingIgnoreCaseAndAssociationsPermission(
            Long userId, String nameExample, AccessPermission accessPermission, Pageable pageable);

    List<ProjectSearchResult> findAllResultsByAssociationsUserIdAndNameContainingIgnoreCaseAndAssociationsPermissionGreaterThanEqual(
            Long userId, String nameExample, AccessPermission accessPermission, Pageable pageable);

    List<ProjectSearchResult> findAllResultsByNameContainingIgnoreCase(String nameExample, Pageable pageable);

    List<ProjectSearchResult> findAllResultsByAccountIdAndAssociationsUserIdAndNameContainingIgnoreCase(
            Long accountId, Long userId, String nameExample, Pageable pageable);

    List<ProjectSearchResult> findAllResultsByAccountIdAndNameContainingIgnoreCase(
            Long accountId, String nameExample, Pageable pageable);

    Page<SubProject> findAllSubProjectsById(Long projectId, Pageable pageable);

    Page<SubProject> findAllSubProjectsByIdAndAssociationsUserId(Long projectId, Long id, Pageable pageable);

    List<SubProjectSearchResult> findAllSubProjectsResultsById(Long projectId, Pageable pageable);

    List<SubProjectSearchResult> findAllSubProjectsResultsByIdAndAssociationsUserId(
            Long projectId, Long id, Pageable pageable);

    List<SubProjectSearchResult> findAllSubProjectsResultsByIdAndNameContainingIgnoreCase(
            Long projectId, String nameExample, Pageable pageable);

    List<SubProjectSearchResult> findAllSubProjectsResultsByIdAndAssociationsUserIdAndNameContainingIgnoreCase(
            Long projectId, Long id, String nameExample, Pageable pageable);
}
