package com.jsoft.magenta.worktimes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkTimeRepository extends JpaRepository<WorkTime, Long>
{
    Optional<Long> findUserIdById(Long wtId);

    List<WorkTime> findAllByUserIdAndDate(Long userId, LocalDate date);
}
