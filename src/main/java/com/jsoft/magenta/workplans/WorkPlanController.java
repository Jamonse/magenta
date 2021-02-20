package com.jsoft.magenta.workplans;

import com.jsoft.magenta.security.annotations.users.UserManagePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static com.jsoft.magenta.util.AppDefaults.*;
import static com.jsoft.magenta.util.AppDefaults.ASCENDING_SORT;

@RestController
@RequestMapping("${application.url}wp")
@UserManagePermission
@RequiredArgsConstructor
public class WorkPlanController
{
    private final WorkPlanService workPlanService;

    @PostMapping("{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkPlan createWorkPlan(
            @PathVariable Long userId,
            @RequestBody @Valid WorkPlan workPlan
    )
    {
        return this.workPlanService.createWorkPlan(userId, workPlan);
    }

    @PutMapping
    public WorkPlan updateWorkPlan(@RequestBody @Valid WorkPlan workPlan)
    {
        return this.workPlanService.updateWorkPlan(workPlan);
    }

    @PatchMapping("{wpId}/start")
    public WorkPlan updateWorkPlanStartDate(
            @PathVariable Long wpId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newStartDate
    )
    {
        return this.workPlanService.updateWorkPlanStartDate(wpId, newStartDate);
    }

    @PatchMapping("{wpId}/end")
    public WorkPlan updateWorkPlanEndDate(
            @PathVariable Long wpId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime newEndDate
    )
    {
        return this.workPlanService.updateWorkPlanEndDate(wpId, newEndDate);
    }

    @GetMapping("{userId}")
    public List<WorkPlan> getAllWorkPlansByUserId(
            @PathVariable Long userId,
            @RequestParam(required = false, defaultValue = PAGE_INDEX) int pageIndex,
            @RequestParam(required = false, defaultValue = PAGE_SIZE) int pageSize,
            @RequestParam(required = false, defaultValue = WORK_PLANS_DEFAULT_SORT) String sortBy,
            @RequestParam(required = false, defaultValue = ASCENDING_SORT) boolean asc
    )
    {
        return this.workPlanService.getAllWorkPlansByUserId(userId, pageIndex, pageSize, sortBy, asc);
    }

    @DeleteMapping("{wpId}")
    public void deleteWorkPlan(@PathVariable Long wpId)
    {
        this.workPlanService.deleteWorkPlan(wpId);
    }
}

