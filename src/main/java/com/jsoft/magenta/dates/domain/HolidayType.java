package com.jsoft.magenta.dates.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HolidayType {
  HOLIDAY(1),
  HOLIDAY_EVE(0.5),
  NATIONAL_DAY(1),
  SPECIAL_EVENT(1);

  private final double span;
}
