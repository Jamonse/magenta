package com.jsoft.magenta.worktimes.reports;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(of = {"account", "project"})
public class HoursDetail
{
    private String account;
    private String project;
    private double hours;
}
