package com.jsoft.magenta.mail;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class WeeklyHoursReport
{
    private String userName;
    private List<HoursDetail> hoursDetails;
    private double totalHours;
    private double weekHours;
    private HoursStatus hoursStatus;
}
