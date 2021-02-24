package com.jsoft.magenta.dates;

import com.jsoft.magenta.dates.domain.Holiday;
import com.jsoft.magenta.exceptions.NoSuchElementException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class HolidayService
{
    private final HolidayRepository holidayRepository;

    public Holiday setHoliday(Holiday holiday)
    {
        validateHolidayCreation(holiday);
        return this.holidayRepository.save(holiday);
    }

    public Holiday updateHoliday(Holiday holiday)
    {
        validateHolidayUpdate(holiday);
        return this.holidayRepository.save(holiday);
    }

    public boolean isHoliday(LocalDate localDate)
    {
        return this.holidayRepository.existsById(localDate);
    }

    public List<Holiday> getAllHolidaysBetween(LocalDate start, LocalDate end)
    {
        return this.holidayRepository.findAllByIdBetween(start, end);
    }

    public void removeHoliday(LocalDate localDate)
    {
        boolean exist = isHoliday(localDate);
        if(!exist)
            throw new NoSuchElementException("Holiday at specified date does not exist");
        this.holidayRepository.deleteById(localDate);
    }

    private void validateHolidayCreation(Holiday holiday)
    {
        LocalDate localDate = holiday.getDate();
        boolean exist = isHoliday(localDate);
        if(exist)
            throw new NoSuchElementException("Holiday with same date already exists");
        validateDayOfWeek(localDate);
    }

    private void validateHolidayUpdate(Holiday holiday)
    {
        LocalDate localDate = holiday.getDate();
        boolean exist = isHoliday(localDate);
        if(!exist)
            throw new NoSuchElementException("Holiday with specified date does not exist");
        validateDayOfWeek(localDate);
    }

    private void validateDayOfWeek(LocalDate localDate)
    {
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        if(dayOfWeek == DayOfWeek.FRIDAY || dayOfWeek == DayOfWeek.SATURDAY)
            throw new DateTimeException("Holiday at friday or saturday is redundant");
    }
}
