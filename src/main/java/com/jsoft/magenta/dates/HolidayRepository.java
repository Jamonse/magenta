package com.jsoft.magenta.dates;

import com.jsoft.magenta.dates.domain.Holiday;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface HolidayRepository extends CrudRepository<Holiday, LocalDate>
{
    List<Holiday> findAllByDateBetween(LocalDate start, LocalDate end);
}
