package com.jsoft.magenta.dates;

import com.jsoft.magenta.dates.domain.Holiday;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.worktimes.reports.BusinessMonth;
import com.jsoft.magenta.worktimes.reports.BusinessWeek;
import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class HolidayService {

  private final HolidayRepository holidayRepository;

  public Holiday setHoliday(Holiday holiday) {
    validateHolidayCreation(holiday);
    return this.holidayRepository.save(holiday);
  }

  public Holiday updateHoliday(Holiday holiday) {
    validateHolidayUpdate(holiday);
    return this.holidayRepository.save(holiday);
  }

  public boolean isHoliday(LocalDate localDate) {
    return this.holidayRepository.existsById(localDate);
  }

  public List<Holiday> getAllHolidaysBetween(LocalDate start, LocalDate end) {
    return this.holidayRepository.findAllByDateBetween(start, end);
  }

  public List<Holiday> getAllWeekHolidays(BusinessWeek week) {
    return getAllHolidaysBetween(week.getStartDate(), week.getEndDate());
  }

  public List<Holiday> getAllMonthHolidays(BusinessMonth businessMonth) {
    return getAllHolidaysBetween(businessMonth.getFirstDate(), businessMonth.getLastDate());
  }

  public double getBusinessHoursInWeek(BusinessWeek week) { // Get all holidays of week
    List<Holiday> holidays = getAllWeekHolidays(week);
    if (holidays.isEmpty()) // No Holidays
    {
      return AppConstants.HOURS_IN_BUSINESS_WEEK;
    }
    Set<LocalDate> weekDates = week.getWeekDates();
    double weekHours = weekDates
        .stream() // Extract amount of hours from each day excluding holidays span and sum
        .mapToDouble(localDate -> getBusinessHoursOfDate(localDate, holidays))
        .sum();
    return weekHours;
  }

  public double getBusinessHoursInMonth(BusinessMonth businessMonth) {
    List<Holiday> holidays = getAllMonthHolidays(businessMonth);
    if (holidays.isEmpty()) {
      return businessMonth.getTotalBusinessHours();
    }
    Set<LocalDate> monthDates = businessMonth.getMonthDates();
    double monthHours = monthDates.stream()
        .mapToDouble(localDate -> getBusinessHoursOfDate(localDate, holidays))
        .sum();
    return monthHours;
  }

  public void removeHoliday(LocalDate localDate) {
    boolean exist = isHoliday(localDate);
    if (!exist) {
      throw new NoSuchElementException("Holiday at specified date does not exist");
    }
    this.holidayRepository.deleteById(localDate);
  }

  private void validateHolidayCreation(Holiday holiday) {
    LocalDate localDate = holiday.getDate();
    boolean exist = isHoliday(localDate);
    if (exist) {
      throw new NoSuchElementException("Holiday with same date already exists");
    }
    validateDayOfWeek(localDate);
  }

  private void validateHolidayUpdate(Holiday holiday) {
    LocalDate localDate = holiday.getDate();
    boolean exist = isHoliday(localDate);
    if (!exist) {
      throw new NoSuchElementException("Holiday with specified date does not exist");
    }
    validateDayOfWeek(localDate);
  }

  private void validateDayOfWeek(LocalDate localDate) {
    DayOfWeek dayOfWeek = localDate.getDayOfWeek();
    if (dayOfWeek == AppConstants.FIRST_WD_DAY || dayOfWeek == AppConstants.SECOND_WD_DAY) {
      throw new DateTimeException("Holiday at friday or saturday is redundant");
    }
  }

  private double getBusinessHoursOfDate(LocalDate localDate, List<Holiday> weekHolidays) {
    double hoursInDay;
    if (localDate.getDayOfWeek() == AppConstants.SHORT_DAY) {
      hoursInDay = AppConstants.SHORT_BUSINESS_DAY_HOURS; // Normal day
    } else // Short day (usually thursday)
    {
      hoursInDay = AppConstants.BUSINESS_DAY_HOURS;
    }
    int index = weekHolidays.indexOf(localDate);
    if (index != -1) { // Date is holiday, get its hours amount
      double holidaySpan = weekHolidays
          .get(index)
          .getHolidayType()
          .getSpan();
      hoursInDay -= (holidaySpan * hoursInDay);
    }
    return hoursInDay;
  }
}
