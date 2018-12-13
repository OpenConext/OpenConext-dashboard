package dashboard.mail;

import dashboard.service.impl.JiraClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.FileCopyUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;

public class MockMailBox extends MailBox {
    private static final Logger LOG = LoggerFactory.getLogger(MockMailBox.class);

    public MockMailBox(JavaMailSender mailSender, String emailFrom, String administrativeEmails, String mailBaseUrl) {
        super(mailSender, emailFrom, administrativeEmails, mailBaseUrl);
    }

    @Override
    protected void doSendMail(MimeMessage message) {
        //nope
    }

    @Override
    protected void setText(String html, MimeMessageHelper helper, boolean html1) throws MessagingException, IOException {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac os x")) {
            openInBrowser(html);
        } else {
            LOG.info(html);
        }
    }

    private void openInBrowser(String html) throws IOException {
        File tempFile = File.createTempFile("javamail", ".html");
        FileCopyUtils.copy(html.getBytes(), tempFile);
        Runtime.getRuntime().exec("open " + tempFile.getAbsolutePath());
    }
}
