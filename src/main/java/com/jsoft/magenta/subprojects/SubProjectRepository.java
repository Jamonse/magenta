package com.jsoft.magenta.subprojects;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubProjectRepository extends JpaRepository<SubProject, Long> {

  boolean existsByUsersId(Long userId);

  boolean existsByProjectIdAndName(Long projectId, String subProjectName);

  Optional<Long> findProjectIdById(Long subProjectId);

  Optional<SubProject> findFirstByUsersId(Long userId);

  Optional<SubProject> findByProjectIdAndName(Long projectId, String subProjectName);

  Optional<SubProject> findByIdAndUsersId(Long subProjectId, Long userId);
}
