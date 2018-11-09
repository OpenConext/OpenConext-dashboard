package dashboard.mail;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import dashboard.domain.Action;
import dashboard.domain.InviteRequest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MailBox {

    private JavaMailSender mailSender;
    private String emailFrom;
    private List<String> administrativeEmails;
    private String mailBaseUrl;

    private final MustacheFactory mustacheFactory = new DefaultMustacheFactory();

    public MailBox(JavaMailSender mailSender, String emailFrom, String administrativeEmails, String mailBaseUrl) {
        this.administrativeEmails = Arrays.asList(administrativeEmails.split(","));
        this.emailFrom = emailFrom;
        this.mailSender = mailSender;
        this.mailBaseUrl = mailBaseUrl;
    }

    public void sendInviteMail(InviteRequest inviteRequest, Action action) throws MessagingException, IOException {
        Map<String, Object> variables = new HashMap<>();
        variables.put("title", "Uitnodiging voor een nieuwe SURFconext koppeling");
        variables.put("inviteRequest", inviteRequest);
        variables.put("action", action);
        variables.put("mailBaseUrl", mailBaseUrl);
        List<String> emails = inviteRequest.getContactPersons().stream().map(cp -> cp.getEmailAddress()).collect(Collectors.toList());
        String html = this.mailTemplate("invite_request_nl.html", variables);
        sendMail(html, "Uitnodiging voor een nieuwe SURFconext koppeling", emails, true);
    }

    public void sendAdministrativeMail(String body, String subject) throws MessagingException, IOException {
        sendMail(body, subject, administrativeEmails, false);
    }

    private void sendMail(String html, String subject, List<String> to, boolean inHtml) throws MessagingException, IOException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false);
        helper.setSubject(subject);
        helper.setTo(to.toArray(new String[]{}));
        setText(html, helper, inHtml);
        helper.setFrom(emailFrom);
        doSendMail(message);
    }

    protected void setText(String html, MimeMessageHelper helper, boolean isHtml) throws MessagingException, IOException {
        helper.setText(html, isHtml);
    }

    protected void doSendMail(MimeMessage message) {
        new Thread(() -> mailSender.send(message)).start();
    }

    private String mailTemplate(String templateName, Map<String, Object> context) throws IOException {
        return mustacheFactory.compile("mail_templates/" + templateName).execute(new StringWriter(), context).toString();
    }

}
