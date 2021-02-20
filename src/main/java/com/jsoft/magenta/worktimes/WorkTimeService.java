package com.jsoft.magenta.worktimes;

import com.jsoft.magenta.events.subprojects.SubProjectRelatedEntityEvent;
import com.jsoft.magenta.exceptions.AuthorizationException;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.exceptions.RedundantWorkTimeException;
import com.jsoft.magenta.subprojects.SubProject;
import com.jsoft.magenta.security.UserEvaluator;
import com.jsoft.magenta.users.User;
import com.jsoft.magenta.util.AppConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkTimeService
{
    private final WorkTimeRepository workTimeRepository;
    private final ApplicationEventPublisher eventPublisher;

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

}
