package selfservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import selfservice.service.EmailService;
import selfservice.service.impl.EmailServiceImpl;
import selfservice.util.mail.Emailer;
import selfservice.util.mail.EmailerImpl;
import selfservice.util.mail.MockEmailerImpl;

@Configuration
public class EmailConfig {

    @Bean
    public EmailService emailService(JavaMailSender mailSender,
                                     @Value("${coin-administrative-email}") String administrativeEmail,
                                     @Value("${dashboard.feature.mail}") boolean mailEnabled) {
        Emailer emailer = mailEnabled ? new EmailerImpl(mailSender) : new MockEmailerImpl();
        return new EmailServiceImpl(administrativeEmail, emailer);
    }

}
