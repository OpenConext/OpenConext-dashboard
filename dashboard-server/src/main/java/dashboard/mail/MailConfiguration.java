package dashboard.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

@Configuration
@EnableConfigurationProperties(MailProperties.class)
public class MailConfiguration {

    @Value("${systemEmail}")
    private String emailFrom;

    @Value("${coin-administrative-email}")
    private String administrativeEmail;

    @Value("${mailBaseUrl}")
    private String mailBaseUrl;

    @Autowired
    private JavaMailSender mailSender;

    @Bean
    public MailBox mailBox() {
        return new MailBox(mailSender, emailFrom, administrativeEmail, mailBaseUrl);
    }

}
