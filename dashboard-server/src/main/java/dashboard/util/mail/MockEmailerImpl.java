package dashboard.util.mail;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;

public class MockEmailerImpl implements Emailer {

    @Override
    public void sendAsync(SimpleMailMessage msg) throws MailException {
        System.out.println(msg.toString());
    }
}
