package nl.surfnet.coin.selfservice.service.impl;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

import nl.surfnet.coin.selfservice.service.NotificationService;
import nl.surfnet.coin.shared.service.MailService;

public class NotificationServiceImpl implements NotificationService {

  private String administrativeEmail;

  private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);

  @Resource(name = "mailService")
  private MailService mailService;

  public void sendMail(String issueKey, String from, String subject, String body) {
    StringBuilder emailSubject = new StringBuilder("(");
    emailSubject.append(issueKey);
    emailSubject.append(") ");
    emailSubject.append(subject);

    StringBuilder content = new StringBuilder("The following question was posted on self service portal:\n\n");
    content.append(body);

    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
    simpleMailMessage.setFrom(from);
    simpleMailMessage.setTo(administrativeEmail);
    simpleMailMessage.setSubject(emailSubject.toString());
    simpleMailMessage.setText(content.toString());

    mailService.sendAsync(simpleMailMessage);
  }

  @Required
  public void setAdministrativeEmail(final String administrativeEmail) {
    this.administrativeEmail = administrativeEmail;
  }
}
