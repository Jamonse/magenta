package com.jsoft.magenta.dates;

import com.jsoft.magenta.dates.domain.Holiday;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface HolidayRepository extends CrudRepository<Holiday, LocalDate> {

  List<Holiday> findAllByDateBetween(LocalDate start, LocalDate end);
}
