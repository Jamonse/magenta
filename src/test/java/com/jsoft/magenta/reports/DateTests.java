package com.jsoft.magenta.reports;

import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.worktimes.reports.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.util.List;

public class DateTests
{
    @Test
    @DisplayName("Create business week using YearMonth")
    public void createBusinessMonthUsingYearMonth()
    {
        YearMonth yearMonth = YearMonth.of(2020, 12);
        BusinessMonth businessMonth = new BusinessMonth(yearMonth);

        Assertions.assertThat(businessMonth.getMonth()).isEqualTo(Month.DECEMBER);
        Assertions.assertThat(businessMonth.getTotalBusinessHours()).isEqualTo(202);
        Assertions.assertThat(businessMonth.getMonthValue()).isEqualTo(12);
        Assertions.assertThat(businessMonth.getMonthLength()).isEqualTo(31);
        Assertions.assertThat(businessMonth.getYearValue()).isEqualTo(2020);
        Assertions.assertThat(businessMonth.getMonthDates())
                .hasSize(businessMonth.getMonthLength())
                .contains(LocalDate.of(2020, 12, 01));
    }

    @Test
    @DisplayName("Create business month using values")
    public void createBusinessMonthUsingValues()
    {
        BusinessMonth businessMonth = new BusinessMonth(Year.of(2020), Month.DECEMBER);

        Assertions.assertThat(businessMonth.getMonth()).isEqualTo(Month.DECEMBER);
        Assertions.assertThat(businessMonth.getTotalBusinessHours()).isEqualTo(202);
        Assertions.assertThat(businessMonth.getMonthValue()).isEqualTo(12);
        Assertions.assertThat(businessMonth.getMonthLength()).isEqualTo(31);
        Assertions.assertThat(businessMonth.getYearValue()).isEqualTo(2020);
        Assertions.assertThat(businessMonth.getMonthDates())
                .hasSize(businessMonth.getMonthLength())
                .contains(LocalDate.of(2020, 12, 01));
    }

    @Test
    @DisplayName("Create business week using values")
    public void createBusinessWeek()
    {
        LocalDate startDate = LocalDate.of(2020, 12, 01);
        LocalDate endDate = LocalDate.of(2020, 12, 07);
        BusinessWeek businessWeek = new BusinessWeek(startDate);

        Assertions.assertThat(businessWeek.getStartDate()).isEqualTo(startDate);
        Assertions.assertThat(businessWeek.getEndDate()).isEqualTo(endDate);
        Assertions.assertThat(businessWeek.getWeekDates())
                .hasSize(7)
                .contains(startDate);

        LocalDate newStartDate = LocalDate.of(2020, 12, 8);
        LocalDate newEndDate = LocalDate.of(2020, 12, 14);

        businessWeek.setStartDate(newStartDate);

        Assertions.assertThat(businessWeek.getStartDate()).isEqualTo(newStartDate);
        Assertions.assertThat(businessWeek.getEndDate()).isEqualTo(newEndDate);
    }

    @Test
    @DisplayName("Create weekly hours report")
    public void createWeeklyHoursReport()
    {
        WeeklyHoursReport weeklyHoursReport = new WeeklyHoursReport(new BusinessWeek(LocalDate.now()));
        weeklyHoursReport.setUserName("user");
        weeklyHoursReport.setWeekHours(40);
        weeklyHoursReport.setHoursDetails(List.of());

        Assertions.assertThat(weeklyHoursReport.getHoursStatus()).isEqualTo(HoursStatus.MISSING);
        Assertions.assertThat(weeklyHoursReport.getTotalHours()).isEqualTo(0);
        Assertions.assertThat(weeklyHoursReport.getWeekHours()).isEqualTo(40);
    }

    @Test
    @DisplayName("Set weekly hours report with invalid value - should throw exception")
    public void setWeeklyHoursReportWithInvalidValues()
    {
        WeeklyHoursReport weeklyHoursReport = new WeeklyHoursReport(new BusinessWeek(LocalDate.now()));

        Assertions.assertThatThrownBy(() -> weeklyHoursReport.setWeekHours(AppConstants.HOURS_IN_WEEK + 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Create monthly hours report")
    public void createMonthlyHoursReport()
    {
        MonthlyHoursReport monthlyHoursReport = new MonthlyHoursReport(new BusinessMonth(Year.now(), Month.FEBRUARY));
        monthlyHoursReport.setMonthHours(180);
        monthlyHoursReport.setUserName("user");

        Assertions.assertThat(monthlyHoursReport.getHoursStatus()).isEqualTo(HoursStatus.MISSING);
    }

    @Test
    @DisplayName("Set monthly hours report with invalid value - should throw exception")
    public void setMonthlyHoursReportWithInvalidValues()
    {
        MonthlyHoursReport monthlyHoursReport = new MonthlyHoursReport(new BusinessMonth(Year.now(), Month.FEBRUARY));

        Assertions.assertThatThrownBy(() -> monthlyHoursReport.setMonthHours(800))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
