package com.jsoft.magenta.worktimes;

import com.jsoft.magenta.users.User;
import com.jsoft.magenta.worktimes.reports.WorkTimeReportResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WorkTimeRepository extends JpaRepository<WorkTime, Long>
{
    Optional<Long> findUserIdById(Long wtId);

    List<WorkTime> findAllByUserIdAndDate(Long userId, LocalDate date);

    List<WorkTimeReportResult> findAllByUserIdAndDateBetween(Long userId, LocalDate startDate, LocalDate endDate);

    Optional<User> getUserByUserId(Long userId);
}
