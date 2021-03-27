package com.jsoft.magenta.mail;

import com.jsoft.magenta.worktimes.reports.WeeklyHoursReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class MailBuilder {

  private final TemplateEngine templateEngine;

  public String buildWeeklyMail(WeeklyHoursReport weeklyHoursReport) {
    Context context = new Context();
    context.setVariable("name", weeklyHoursReport.getUserName());
    context.setVariable("hoursDetails", weeklyHoursReport.getHoursDetails());
    context.setVariable("weekHours", weeklyHoursReport.getWeekHours());
    context.setVariable("totalHours", weeklyHoursReport.getTotalHours());
    context.setVariable("hoursStatus", weeklyHoursReport.getHoursStatus());
    return templateEngine.process("weeklyMailTemplate", context);
  }
}
