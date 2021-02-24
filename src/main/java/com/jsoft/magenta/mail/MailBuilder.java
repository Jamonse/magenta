package com.jsoft.magenta.mail;

import com.jsoft.magenta.worktimes.reports.HoursReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

@Service
@RequiredArgsConstructor
public class MailBuilder
{
    private final TemplateEngine templateEngine;

    public String buildWeeklyMail(HoursReport weeklyHoursReport)
    {
        return null;
    }
}
