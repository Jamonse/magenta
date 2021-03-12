package com.jsoft.magenta.worktimes;

import com.jsoft.magenta.dates.HolidayService;
import com.jsoft.magenta.events.subprojects.SubProjectRelatedEntityEvent;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.exceptions.RedundantWorkTimeException;
import com.jsoft.magenta.subprojects.SubProject;
import com.jsoft.magenta.security.UserEvaluator;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.worktimes.reports.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkTimeService
{
    private final WorkTimeRepository workTimeRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final HolidayService holidayService;

    public WorkTime createWorkTime(Long subProjectId, WorkTime workTime)
    { // Validate work time type and values, set amount in case of date type work time
        validateAndCheckType(workTime);
        this.eventPublisher.publishEvent(new SubProjectRelatedEntityEvent(subProjectId));
        User user = UserEvaluator.currentUser();
        SubProject subProject = new SubProject(subProjectId);
        workTime.setSubProject(subProject);
        workTime.setUser(user);
        return this.workTimeRepository.save(workTime);
    }

    public WorkTime createWorkTime(Long userId, Long subProjectId, WorkTime workTime)
    {
        validateAndCheckType(workTime);
        this.eventPublisher.publishEvent(new SubProjectRelatedEntityEvent(subProjectId));
        SubProject subProject = new SubProject(subProjectId);
        workTime.setSubProject(subProject);
        workTime.setUser(new User(userId));
        return this.workTimeRepository.save(workTime);
    }

    public WorkTime updateWorkTimeNote(Long wtId, String newNote)
    {
        isSupervisorOrOwner(wtId);
        WorkTime workTime = findWorkTimeReference(wtId);
        workTime.setNote(newNote);
        return this.workTimeRepository.save(workTime);
    }

    public WorkTime updateWorkTimeSubProject(Long wtId, Long spId)
    {
        isSupervisorOrOwner(wtId);
        WorkTime workTime = findWorkTimeReference(wtId);
        this.eventPublisher.publishEvent(new SubProjectRelatedEntityEvent(spId));
        workTime.setSubProject(new SubProject(spId));
        return this.workTimeRepository.save(workTime);
    }

    public WorkTime updateWorkTimeStartTime(Long wtId, LocalTime newStartTime)
    {
        isSupervisorOrOwner(wtId);
        WorkTime workTime = findWorkTime(wtId);
        workTime.setStartTime(newStartTime);
        validateAndCheckType(workTime);
        return this.workTimeRepository.save(workTime);
    }

    public WorkTime updateWorkTimeEndTime(Long wtId, LocalTime newEndTime)
    {
        isSupervisorOrOwner(wtId);
        WorkTime workTime = findWorkTime(wtId);
        workTime.setEndTime(newEndTime);
        validateAndCheckType(workTime);
        return this.workTimeRepository.save(workTime);
    }

    public WorkTime updateWorkTimeAmount(Long wtId, Double newAmount)
    {
        isSupervisorOrOwner(wtId);
        WorkTime workTime = findWorkTime(wtId);
        boolean isAmountType = !validateAndCheckType(workTime);
        if(!isAmountType)
            throw new DateTimeException("Cannot update date type work time amount");
        workTime.setAmount(newAmount);
        return this.workTimeRepository.save(workTime);
    }

    public List<WorkTime> getAllWorkTimesByDate(LocalDate localDate)
    {
        Long userId = UserEvaluator.currentUserId();
        return getAllWorkTimesByUserAndDate(userId, localDate);
    }

    public List<WorkTime> getAllWorkTimesByUserAndDate(Long userId, LocalDate localDate)
    {
        return this.workTimeRepository.findAllByUserIdAndDate(userId, localDate);
    }

    public WeeklyHoursReport getWeeklyHoursReport(Long userId, String userName, BusinessWeek businessWeek)
    { // Create report
        WeeklyHoursReport weeklyHoursReport = new WeeklyHoursReport(businessWeek);
        double weekHours = this.holidayService.getBusinessHoursInWeek(businessWeek); // Get amount of hours excluding holidays
        List<WorkTimeReportResult> workTimes = this.workTimeRepository // Get all work times of user in that week
                .findAllByUserIdAndDateBetween(userId, businessWeek.getStartDate(), businessWeek.getEndDate());
        List<HoursDetail> hoursDetails = mapWorkTimeToHourDetails(workTimes); // Map work times to hour details
        // Set fetched and processed details to the report
        weeklyHoursReport.setUserName(userName);
        weeklyHoursReport.setHoursDetails(hoursDetails);
        weeklyHoursReport.setWeekHours(weekHours);
        // Return the report
        return weeklyHoursReport;
    }

    public MonthlyHoursReport getMonthlyHoursReport(Long userId, String userName, BusinessMonth businessMonth)
    {
        MonthlyHoursReport monthlyHoursReport = new MonthlyHoursReport(businessMonth);
        double monthHours = this.holidayService.getBusinessHoursInMonth(businessMonth); // Get amount of hours excluding holidays
        List<WorkTimeReportResult> workTimes = this.workTimeRepository // Get all work times of user in that month
                .findAllByUserIdAndDateBetween(userId, businessMonth.getFirstDate(), businessMonth.getLastDate());
        List<HoursDetail> hoursDetails = mapWorkTimeToHourDetails(workTimes); // Map work times to hour details
        // Set fetched and processed details to the report
        monthlyHoursReport.setUserName(userName);
        monthlyHoursReport.setHoursDetails(hoursDetails);
        monthlyHoursReport.setMonthHours(monthHours);
        // Return the report
        return monthlyHoursReport;
    }

    public void deleteWorkTime(Long wtId)
    {
        isSupervisorOrOwner(wtId);
        boolean exist = this.workTimeRepository.existsById(wtId);
        if(exist)
            this.workTimeRepository.deleteById(wtId);
    }

    private boolean validateAndCheckType(WorkTime workTime)
    {
        LocalTime startTime = workTime.getStartTime();
        LocalTime endTime = workTime.getEndTime();

        if(startTime == null)
            if(endTime == null)
                if(workTime.getAmount() == null) // Both times are null, verify that there is an hors amount set
                    throw new RedundantWorkTimeException("Work time must contain date range or amount of hours");
                else // Amount type work time
                    return false;
            else // Start time is null but end date is not
                throw new DateTimeException("Work time that contains end date must contain start date");
        else if(endTime == null) // End time is null but start date is not
            throw new DateTimeException("Work time that contains start date must contain end date");
        else if(startTime.isAfter(endTime)) // Both times are not null, verify correct values
            throw new DateTimeException("Start time of a work time cannot be after its end time");
        double amount = (double) workTime.getStartTime() // Date type work time
                .until(workTime.getEndTime(), ChronoUnit.MINUTES) / 60;
        workTime.setAmount(amount);
        return true;
    }

    private WorkTime findWorkTime(Long wtId)
    {
        return this.workTimeRepository
                .findById(wtId)
                .orElseThrow(() -> new NoSuchElementException("Work time not found"));
    }

    private WorkTime findWorkTimeReference(Long wtId)
    {
        return this.workTimeRepository.getOne(wtId);
    }

    private void isSupervisorOrOwner(Long wtId)
    {
        User user = UserEvaluator.currentUser();
        Long ownerId = findOwnerUserId(wtId);
        user.isSupervisorOrOwner(ownerId);
    }

    private Long findOwnerUserId(Long wtId)
    {
        return this.workTimeRepository
                .findUserIdById(wtId)
                .orElseThrow(() -> new NoSuchElementException("Work time not found"));
    }

    private List<HoursDetail> mapWorkTimeToHourDetails(List<WorkTimeReportResult> workTimes)
    {
        Map<HoursDetail, Double> hoursDetailsMap = new HashMap<>(); // Create hour details - amount of hours map
        // Iterate over all work times and combine them into matching hour details (same account and project)
        for(WorkTimeReportResult workTime : workTimes)
        {
            HoursDetail hoursDetail = new HoursDetail(
                    workTime.getSubProjectProjectAccountName(),
                    workTime.getSubProjectProjectName(),
                    workTime.getAmount());
            if(hoursDetailsMap.containsKey(hoursDetail)) // Hour details exist - add new one hours
                hoursDetailsMap.put(hoursDetail, hoursDetailsMap.get(hoursDetail) + hoursDetail.getHours());
            else // Hour details does not exist - create new one
                hoursDetailsMap.put(hoursDetail, hoursDetail.getHours());
        }// Iterate over hour details - amount of hours map and collect totals to list
        List<HoursDetail> hoursDetails = hoursDetailsMap.keySet().stream()
                .map(hoursDetail -> new HoursDetail(
                        hoursDetail.getAccount(),
                        hoursDetail.getProject(),
                        hoursDetailsMap.get(hoursDetail)))
                .collect(Collectors.toList());
        return hoursDetails;
    }

}
