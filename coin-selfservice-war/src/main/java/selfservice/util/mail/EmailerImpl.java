package selfservice.util.mail;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;

public class EmailerImpl implements Emailer {

  public EmailerImpl(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  private final JavaMailSender mailSender;

  @Async
  public void sendAsync(MimeMessagePreparator preparator) throws MailException {
    mailSender.send(preparator);
  }

  @Async
  public void sendAsync(SimpleMailMessage msg) throws MailException {
    mailSender.send(msg);
  }

}
