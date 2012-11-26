package nl.surfnet.coin.selfservice.service.impl;

import javax.annotation.Resource;

import nl.surfnet.coin.selfservice.service.EmailService;
import nl.surfnet.coin.shared.service.MailService;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.mail.SimpleMailMessage;

public class EmailServiceImpl implements EmailService {

  private String administrativeEmail;

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
