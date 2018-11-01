package dashboard;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import dashboard.service.EmailService;
import dashboard.service.impl.EmailServiceImpl;
import dashboard.util.mail.Emailer;
import dashboard.util.mail.EmailerImpl;
import dashboard.util.mail.MockEmailerImpl;

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
