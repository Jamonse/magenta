package com.jsoft.magenta.worktimes.reports;

import com.jsoft.magenta.util.AppConstants;
import java.util.List;
import lombok.Getter;

@Getter
public class WeeklyHoursReport extends HoursReport {

  private BusinessWeek week;
  private double weekHours;

  public WeeklyHoursReport(BusinessWeek businessWeek) {
    this.week = businessWeek;
  }

  public WeeklyHoursReport(
      String userName, List<HoursDetail> hoursDetails,
      BusinessWeek week, double weekHours
  ) {
    super(userName, hoursDetails);
    this.week = week;
    setWeekHours(weekHours);
  }

  public void setWeek(BusinessWeek businessWeek) {
    this.week = businessWeek;
  }

  public void setWeekHours(double weekHours) {
    if (weekHours > AppConstants.HOURS_IN_WEEK || weekHours < 0) {
      throw new IllegalArgumentException(
          String.format("Number of week hours cannot exceed %d or be negative",
              AppConstants.HOURS_IN_WEEK));
    }
    this.weekHours = weekHours;
    setHoursStatus();
  }

  protected void setHoursStatus() {
    double hoursRemainder = this.weekHours - this.totalHours;
    if (hoursRemainder > 0) {
      this.hoursStatus = HoursStatus.MISSING;
    } else if (hoursRemainder < 0) {
      this.hoursStatus = HoursStatus.EXTRA;
    } else {
      this.hoursStatus = HoursStatus.EXACT;
    }
  }
}
