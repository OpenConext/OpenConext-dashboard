package selfservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;

import selfservice.service.EmailService;
import selfservice.service.impl.EmailServiceImpl;
import selfservice.util.mail.Emailer;
import selfservice.util.mail.EmailerImpl;
import selfservice.util.mail.MockEmailerImpl;

@Configuration
public class EmailConfig {

  @Bean
  @Profile("!dev")
  public EmailService emailService(JavaMailSender mailSender, @Value("${coin-administrative-email}") String administrativeEmail) {
    return new EmailServiceImpl(administrativeEmail, new EmailerImpl(mailSender));
  }

  @Bean
  @Profile("dev")
  public EmailService mockEmailService(@Value("${coin-administrative-email}") String administrativeEmail, Emailer emailer) {
    return new EmailServiceImpl(administrativeEmail, emailer);
  }

  @Bean
  @Profile("!dev")
  public Emailer emailer(JavaMailSender mailSender) {
    return new EmailerImpl(mailSender);
  }

  @Bean
  @Profile("dev")
  public Emailer mockEmailer() {
    return new MockEmailerImpl();
  }

}
