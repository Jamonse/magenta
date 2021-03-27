package com.jsoft.magenta.workplans;

import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkPlanRepository extends JpaRepository<WorkPlan, Long> {

  List<WorkPlan> findAllByUserId(Long userId, Pageable pageable);
}
