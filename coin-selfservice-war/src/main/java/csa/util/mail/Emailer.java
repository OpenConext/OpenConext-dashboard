package csa.util.mail;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

public interface Emailer {
  void sendAsync(SimpleMailMessage msg) throws MailException;
}
