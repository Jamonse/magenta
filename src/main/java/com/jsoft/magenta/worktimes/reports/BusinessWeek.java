package com.jsoft.magenta.worktimes.reports;

import lombok.Getter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
public final class BusinessWeek {
    private LocalDate startDate;
    private LocalDate endDate;

    public BusinessWeek(LocalDate startDate) {
        setStartDate(startDate);
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        setEndDate(startDate);
    }

    private void setEndDate(LocalDate startDate) {
        this.endDate = this.startDate.plusDays(6);
    }

    public Set<LocalDate> getWeekDates() {
        Set<LocalDate> weekDates = new HashSet<>();
        for (int i = 0; i < 7; i++)
            weekDates.add(startDate.plusDays(i));
        return weekDates;
    }
}
