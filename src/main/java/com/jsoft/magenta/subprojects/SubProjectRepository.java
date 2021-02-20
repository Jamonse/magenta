package com.jsoft.magenta.subprojects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubProjectRepository extends JpaRepository<SubProject, Long>
{
    boolean existsByUsersId(Long userId);

    Optional<Long> findProjectIdById(Long subProjectId);

    Optional<SubProject> findFirstByUsersId(Long userId);

    Optional<SubProject> findByProjectIdAndName(Long projectId, String subProjectName);

    Optional<SubProject> findByIdAndUsersId(Long subProjectId, Long userId);

    Page<SubProject> findAllByProjectId(Long id, Pageable pageable);

    List<SubProjectSearchResult> findAllResultsByProjectId(Long projectId, Pageable pageable);

    List<SubProjectSearchResult> findAllResultsByProjectIdAndUsersId(Long projectId, Long userId, Pageable pageable);
}
