package com.jsoft.magenta.workplans;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkPlanRepository extends JpaRepository<WorkPlan, Long> {
    List<WorkPlan> findAllByUserId(Long userId, Pageable pageable);
}
