package com.jsoft.magenta.worktimes.reports;

import com.jsoft.magenta.util.AppConstants;
import lombok.Getter;

import java.time.Month;
import java.time.Year;
import java.util.List;

@Getter
public class MonthlyHoursReport extends HoursReport
{
    private Month month;
    private Year year;
    private double monthHours;

    public MonthlyHoursReport(BusinessMonth businessMonth)
    {
        this.year = businessMonth.getYear();
        this.month = businessMonth.getMonth();
    }

    public MonthlyHoursReport(
            String userName, List<HoursDetail> hoursDetails, Month month, Year year, double monthHours)
    {
        super(userName, hoursDetails);
        this.month = month;
        this.year = year;
        this.monthHours = monthHours;
        setHoursStatus();
    }

    public void setYear(Year year)
    {
        this.year = year;
    }

    public void setMonth(Month month)
    {
        this.month = month;
    }

    public void setMonthHours(double monthHours)
    {
        if(monthHours > AppConstants.MAX_HOURS_IN_MONTH || monthHours < 0)
            throw new IllegalArgumentException(
                    String.format("Number of month hours cannot exceed %d or be negative", AppConstants.MAX_HOURS_IN_MONTH));
        this.monthHours = monthHours;
        setHoursStatus();
    }

    protected void setHoursStatus()
    {
        double hoursRemainder = this.monthHours - this.totalHours;
        if(hoursRemainder > 0)
            this.hoursStatus = HoursStatus.MISSING;
        else if(hoursRemainder < 0)
            this.hoursStatus = HoursStatus.EXTRA;
        else
            this.hoursStatus = HoursStatus.EXACT;
    }
}
