package com.jsoft.magenta.worktimes.reports;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
public class Week
{
    private LocalDate startDate;
    private LocalDate endDate;

    public Week(LocalDate startDate)
    {
        this.startDate = startDate;
        setEndDate(startDate);
    }

    public void setStartDate(LocalDate startDate)
    {
        this.startDate = startDate;
        setEndDate(startDate);
    }

    private void setEndDate(LocalDate startDate)
    {
        this.endDate = this.startDate.plusWeeks(1);
    }
}
