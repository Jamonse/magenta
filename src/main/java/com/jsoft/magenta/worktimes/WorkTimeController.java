package com.jsoft.magenta.worktimes;

import com.jsoft.magenta.security.annotations.users.SupervisorOrOwner;
import com.jsoft.magenta.util.validation.annotations.PositiveNumber;
import com.jsoft.magenta.util.validation.annotations.ValidContent;
import com.jsoft.magenta.util.validation.annotations.ValidMonth;
import com.jsoft.magenta.util.validation.annotations.ValidYear;
import com.jsoft.magenta.worktimes.reports.BusinessMonth;
import com.jsoft.magenta.worktimes.reports.MonthlyHoursReport;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

@Validated
@RestController
@RequestMapping("${application.url}wt")
@RequiredArgsConstructor
public class WorkTimeController
{
    private final WorkTimeService workTimeService;

    @PostMapping("{spId}")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkTime createWorkTime(
            @PathVariable Long spId,
            @RequestBody @Valid WorkTime workTime
    )
    {
        return this.workTimeService.createWorkTime(spId, workTime);
    }

    @PostMapping("{userId}/sp/{spId}")
    @ResponseStatus(HttpStatus.CREATED)
    public WorkTime createWorkTime(
            @PathVariable @SupervisorOrOwner Long userId,
            @PathVariable Long spId,
            @RequestBody @Valid WorkTime workTime
    )
    {
        return this.workTimeService.createWorkTime(userId, spId, workTime);
    }

    @PatchMapping("{wtId}/note")
    public WorkTime updateWorkTimeNote(
            @PathVariable Long wtId,
            @RequestBody @ValidContent String newNote
    )
    {
        return this.workTimeService.updateWorkTimeNote(wtId, newNote);
    }

    @PatchMapping("{wtId}/sp/{spId}")
    public WorkTime updateWorkTimeSubProject(
            @PathVariable Long wtId,
            @PathVariable Long spId
    )
    {
        return this.workTimeService.updateWorkTimeSubProject(wtId, spId);
    }

    @PatchMapping("{wtId}/start")
    public WorkTime updateWorkTimeStartTime(
            @PathVariable Long wtId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime newStartTime
    )
    {
        return this.workTimeService.updateWorkTimeStartTime(wtId, newStartTime);
    }

    @PatchMapping("{wtId}/end")
    public WorkTime updateWorkTimeEndTime(
            @PathVariable Long wtId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime newEndTime
    )
    {
        return this.workTimeService.updateWorkTimeEndTime(wtId, newEndTime);
    }

    @PatchMapping("{wtId}/amount")
    public WorkTime updateWorkTimeAmount(
            @PathVariable Long wtId,
            @RequestBody @PositiveNumber Double newAmount
    )
    {
        return this.workTimeService.updateWorkTimeAmount(wtId, newAmount);
    }

    @GetMapping
    public List<WorkTime> getAllWorkTimesByDate(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    )
    {
        return this.workTimeService.getAllWorkTimesByDate(date);
    }

    @GetMapping("{userId}")
    public List<WorkTime> getAllWorkTimesOfUserByDate(
            @PathVariable @SupervisorOrOwner Long userId,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    )
    {
        return this.workTimeService.getAllWorkTimesByUserAndDate(userId, date);
    }

    @GetMapping("report/{userId}/year/{year}/month/{month}")
    public MonthlyHoursReport getMonthlyHoursReport(
            @PathVariable @SupervisorOrOwner Long userId,
            @PathVariable @ValidYear Integer year,
            @PathVariable @ValidMonth Integer month,
            @RequestParam @NotBlank String userName
    )
    {
        BusinessMonth businessMonth = new BusinessMonth(YearMonth.of(year, month));
        return this.workTimeService.getMonthlyHoursReport(userId, userName, businessMonth);
    }

    @DeleteMapping("{wtId}")
    public void deleteWorkTime(@PathVariable Long wtId)
    {
        this.workTimeService.deleteWorkTime(wtId);
    }

}
