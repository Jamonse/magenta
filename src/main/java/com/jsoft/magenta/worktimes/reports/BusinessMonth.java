package com.jsoft.magenta.worktimes.reports;

import com.jsoft.magenta.util.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;

@Getter
@AllArgsConstructor
public final class BusinessMonth
{
    private Year year;
    private Month month;

    public BusinessMonth(YearMonth yearMonth)
    {
        this.year = Year.of(yearMonth.getYear());
        this.month = yearMonth.getMonth();
    }

    public int getYearValue()
    {
        return this.year.getValue();
    }

    public int getMonthValue()
    {
        return this.month.getValue();
    }

    public int getTotalBusinessHours()
    {
        Set<LocalDate> monthDates = getMonthDates();
        int totalHours = 0;
        for(LocalDate localDate : monthDates)
        {
            if(localDate.getDayOfWeek() == AppConstants.FIRST_WD_DAY ||
                    localDate.getDayOfWeek() == AppConstants.SECOND_WD_DAY)
                continue;
            else if(localDate.getDayOfWeek() == AppConstants.SHORT_DAY)
                totalHours += AppConstants.SHORT_BUSINESS_DAY_HOURS;
            else
                totalHours += AppConstants.BUSINESS_DAY_HOURS;
        }
        return totalHours;
    }

    public LocalDate getFirstDate()
    {
        return LocalDate.of(getYearValue(), getMonthValue(), 1);
    }

    public LocalDate getLastDate()
    {
        int year = getYearValue();
        int month = getMonthValue();
        YearMonth yearMonth = YearMonth.of(year, month);
        return LocalDate.of(year, month, yearMonth.lengthOfMonth());
    }

    public int getMonthLength()
    {
        return getYearMonth().lengthOfMonth();
    }

    public Set<LocalDate> getMonthDates()
    {
        int year = getYearValue();
        int month = getMonthValue();
        YearMonth yearMonth = YearMonth.of(year, month);
        int monthLength = yearMonth.lengthOfMonth();
        Set<LocalDate> monthDates = new HashSet();
        for(int i = 1; i <= monthLength; i++)
            monthDates.add(LocalDate.of(year, month, i));
        return monthDates;
    }

    private YearMonth getYearMonth()
    {
        return YearMonth.of(getYearValue(), getMonthValue());
    }
}
