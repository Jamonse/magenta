package com.jsoft.magenta.subprojects;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubProjectRepository extends JpaRepository<SubProject, Long> {
    boolean existsByUsersId(Long userId);

    boolean existsByProjectIdAndName(Long projectId, String subProjectName);

    Optional<Long> findProjectIdById(Long subProjectId);

    Optional<SubProject> findFirstByUsersId(Long userId);

    Optional<SubProject> findByProjectIdAndName(Long projectId, String subProjectName);

    Optional<SubProject> findByIdAndUsersId(Long subProjectId, Long userId);
}
