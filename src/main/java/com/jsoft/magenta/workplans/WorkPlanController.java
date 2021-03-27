package com.jsoft.magenta.workplans;

import static com.jsoft.magenta.util.AppDefaults.ASCENDING_SORT;
import static com.jsoft.magenta.util.AppDefaults.PAGE_INDEX;
import static com.jsoft.magenta.util.AppDefaults.PAGE_SIZE;
import static com.jsoft.magenta.util.AppDefaults.WORK_PLANS_DEFAULT_SORT;

import com.jsoft.magenta.security.annotations.users.UserManagePermission;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${application.url}wp")
@UserManagePermission
@RequiredArgsConstructor
public class WorkPlanController {

  private final WorkPlanService workPlanService;

  @PostMapping("{userId}")
  @ResponseStatus(HttpStatus.CREATED)
  public WorkPlan createWorkPlan(
      @PathVariable Long userId,
      @RequestBody @Valid WorkPlan workPlan
  ) {
    return this.workPlanService.createWorkPlan(userId, workPlan);
  }

  @PutMapping
  public WorkPlan updateWorkPlan(@RequestBody @Valid WorkPlan workPlan) {
    return this.workPlanService.updateWorkPlan(workPlan);
  }

  @PatchMapping("{wpId}/start")
  public WorkPlan updateWorkPlanStartDate(
      @PathVariable Long wpId,
      @RequestParam
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newStartDate
  ) {
    return this.workPlanService.updateWorkPlanStartDate(wpId, newStartDate);
  }

  @PatchMapping("{wpId}/end")
  public WorkPlan updateWorkPlanEndDate(
      @PathVariable Long wpId,
      @RequestParam
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newEndDate
  ) {
    return this.workPlanService.updateWorkPlanEndDate(wpId, newEndDate);
  }

  @GetMapping("{userId}")
  public List<WorkPlan> getAllWorkPlansByUserId(
      @PathVariable Long userId,
      @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
      @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
      @RequestParam(required = false, defaultValue = WORK_PLANS_DEFAULT_SORT) String sortBy,
      @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
  ) {
    return this.workPlanService.getAllWorkPlansByUserId(userId, pageIndex, pageSize, sortBy, asc);
  }

  @DeleteMapping("{wpId}")
  public void deleteWorkPlan(@PathVariable Long wpId) {
    this.workPlanService.deleteWorkPlan(wpId);
  }
}

