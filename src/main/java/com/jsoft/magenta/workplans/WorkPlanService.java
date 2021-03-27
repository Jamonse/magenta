package com.jsoft.magenta.workplans;

import com.jsoft.magenta.events.workplans.WorkPlanCreationEvent;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.security.SecurityService;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.util.PageRequestBuilder;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkPlanService {

  private final WorkPlanRepository workPlanRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final SecurityService securityService;

  public WorkPlan createWorkPlan(Long userId, WorkPlan workPlan) {
    isSupervisor(userId);
    validateDates(workPlan);
    this.eventPublisher.publishEvent(new WorkPlanCreationEvent(userId));
    return this.workPlanRepository.save(workPlan);
  }

  public WorkPlan updateWorkPlan(WorkPlan workPlan) {
    WorkPlan workPlanToUpdate = findWorkPlan(workPlan.getId());
    validateDates(workPlan);
    workPlanToUpdate.setTitle(workPlan.getTitle());
    workPlanToUpdate.setStartDate(workPlan.getStartDate());
    workPlanToUpdate.setEndDate(workPlan.getEndDate());
    return this.workPlanRepository.save(workPlanToUpdate);
  }

  public WorkPlan updateWorkPlanStartDate(Long wpId, LocalDateTime startDate) {
    WorkPlan workPlan = findWorkPlan(wpId);
    workPlan.setStartDate(startDate);
    validateDates(workPlan);
    return this.workPlanRepository.save(workPlan);
  }

  public WorkPlan updateWorkPlanEndDate(Long wpId, LocalDateTime endDate) {
    WorkPlan workPlan = findWorkPlan(wpId);
    workPlan.setEndDate(endDate);
    validateDates(workPlan);
    return this.workPlanRepository.save(workPlan);
  }

  public List<WorkPlan> getAllWorkPlansByUserId(Long userId, int pageIndex, int pageSize,
      String sortBy,
      boolean asc) {
    PageRequest pageRequest = PageRequestBuilder.buildPageRequest(pageIndex, pageSize, sortBy, asc);
    return this.workPlanRepository.findAllByUserId(userId, pageRequest);
  }

  public void deleteWorkPlan(Long wpId) {
    this.workPlanRepository.deleteById(wpId);
  }

  private WorkPlan findWorkPlan(Long wpId) {
    return this.workPlanRepository
        .findById(wpId)
        .orElseThrow(() -> new NoSuchElementException("Work plan not found"));
  }

  private void isSupervisor(Long userId) {
    User user = securityService.currentUser();
    user.isSupervisorOf(userId);
  }

  private void validateDates(WorkPlan workPlan) {
    LocalDateTime start = workPlan.getStartDate();
    LocalDateTime end = workPlan.getEndDate();
    if (start.isAfter(end)) {
      throw new DateTimeException("Start date of a work plan cannot exceed its end date");
    }
  }

}
