package com.jsoft.magenta.mail;

import com.jsoft.magenta.users.UserRepository;
import com.jsoft.magenta.users.UserSearchResult;
import com.jsoft.magenta.util.AppConstants;
import com.jsoft.magenta.worktimes.WorkTimeService;
import com.jsoft.magenta.worktimes.reports.BusinessWeek;
import com.jsoft.magenta.worktimes.reports.WeeklyHoursReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService
{
    @Value("${application.mail.address}")
    private String from;

    private final MailBuilder mailBuilder;
    private final JavaMailSender mailSender;
    private final WorkTimeService workTimeService;
    private final UserRepository userRepository;

    @Scheduled(cron = "${application.mail.weekly-mail}")
    public void weeklyMailsJob()
    { // Fetch user details
        List<UserSearchResult> users = this.userRepository.findAllResultsBy();
        users.forEach(user -> {
            try { // Send weekly email to each user
                sendWeeklyMail(
                        user.getId(),
                        user.getEmail(),
                        String.format("%s %s", user.getFirstName(), user.getLastName()));
            } catch (MessagingException e) {
                log.error("Error during email sending to user %s", user.getEmail());
            }
        });
    }

    public void sendWeeklyMail(Long userId, String userEmail, String userName) throws MessagingException
    { // Create mime message and mime message helper in order to send HTML template
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        // Fetch weekly hours report for specified user
        BusinessWeek businessWeek = new BusinessWeek(LocalDate.now().minusWeeks(1));
        WeeklyHoursReport weeklyHoursReport = workTimeService.getWeeklyHoursReport(userId, userName, businessWeek);
        // Build email data
        mimeMessageHelper.setFrom(from);
        mimeMessageHelper.setSubject(String.format(AppConstants.WEEKLY_MAIL_MESSAGE, userName));
        mimeMessageHelper.setText(mailBuilder.buildWeeklyMail(weeklyHoursReport));
        mimeMessageHelper.setTo(userEmail);
        // Send mail
        mailSender.send(mimeMessage);
    }
}
