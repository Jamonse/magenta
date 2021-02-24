package com.jsoft.magenta.dates;

import com.jsoft.magenta.dates.domain.Holiday;
import com.jsoft.magenta.security.annotations.SystemConfigurator;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("${application.url}holidays")
@RequiredArgsConstructor
public class HolidayController
{
    private final HolidayService holidayService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SystemConfigurator
    public Holiday setHoliday(@RequestBody @Valid Holiday holiday)
    {
        return this.holidayService.setHoliday(holiday);
    }

    @PutMapping
    @SystemConfigurator
    public Holiday updateHoliday(@RequestBody @Valid Holiday holiday)
    {
        return this.holidayService.updateHoliday(holiday);
    }

    @GetMapping
    public boolean isHoliday(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    )
    {
        return this.holidayService.isHoliday(date);
    }

    @GetMapping
    public List<Holiday> getAllHolidaysBetween(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    )
    {
        return this.holidayService.getAllHolidaysBetween(startDate, endDate);
    }

    @DeleteMapping
    @SystemConfigurator
    public void removeHoliday(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    )
    {
        this.holidayService.removeHoliday(date);
    }
}
