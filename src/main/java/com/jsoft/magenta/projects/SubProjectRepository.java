package com.jsoft.magenta.projects;

import com.jsoft.magenta.projects.domain.SubProject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubProjectRepository extends JpaRepository<SubProject, Long>
{
    Optional<SubProject> findFirstByUsersId(Long userId);
}
