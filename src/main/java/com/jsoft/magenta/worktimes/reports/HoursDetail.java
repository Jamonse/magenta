package com.jsoft.magenta.worktimes.reports;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HoursDetail
{
    private String account;
    private String project;
    private double hours;
}
