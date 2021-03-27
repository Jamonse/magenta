package com.jsoft.magenta.mail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class MailIntegrationTest {

  @Autowired
  private MailService mailService;

  @Test
  @DisplayName("Send weekly mail")
  public void sendWeeklyMail() {
    mailService.weeklyMailsJob();
  }
}
