package nl.surfnet.coin.selfservice.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import nl.surfnet.coin.selfservice.service.EmailService;
import nl.surfnet.coin.shared.service.MailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

public class EmailServiceImpl implements EmailService {

  private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

  private String administrativeEmail;

  @Resource(name = "mailService")
  private MailService mailService;

  @Autowired
  private Configuration freemarkerConfiguration;

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

  public void sendTemplatedMultipartEmail(final String subject, final String templateName, final Locale locale,
      final List<String> recipients, final String from, final Map<String, Object> templateVars) {

    // TODO add validation (at least one to/from address etc)

    final String html = composeMailMessage(templateName, templateVars, locale, "html");
    final String plainText = composeMailMessage(templateName, templateVars, locale, "plaintext");

    // TODO add validation html and plaintext available?

    final InternetAddress[] recipientAddressess = new InternetAddress[recipients.size()];
    int i = 0;
    for (String recipientEmail : recipients) {
      try {
        recipientAddressess[i++] = new InternetAddress(recipientEmail);
      } catch (AddressException e) {
        log.error("Exception while constructing email addresses", e);
      }
    }

    MimeMessagePreparator preparator = new MimeMessagePreparator() {
      public void prepare(MimeMessage mimeMessage) throws MessagingException {

        mimeMessage.setFrom(new InternetAddress(from));
        mimeMessage.setRecipients(Message.RecipientType.TO, recipientAddressess);
        mimeMessage.setSubject(subject);

        MimeMultipart rootMixedMultipart = getMimeMultipartMessageBody(plainText, html);
        mimeMessage.setContent(rootMixedMultipart);
      }
    };
    mailService.sendAsync(preparator);
  }

  @Required
  public void setAdministrativeEmail(final String administrativeEmail) {
    this.administrativeEmail = administrativeEmail;
  }

  private MimeMultipart getMimeMultipartMessageBody(String plainText, String html) throws MessagingException {
    MimeMultipart rootMixedMultipart = new MimeMultipart("mixed");
    MimeMultipart nestedRelatedMultipart = new MimeMultipart("related");
    MimeBodyPart relatedBodyPart = new MimeBodyPart();
    relatedBodyPart.setContent(nestedRelatedMultipart);
    rootMixedMultipart.addBodyPart(relatedBodyPart);

    MimeMultipart messageBody = new MimeMultipart("alternative");
    MimeBodyPart bodyPart = null;
    for (int i = 0; i < nestedRelatedMultipart.getCount(); i++) {
      BodyPart bp = nestedRelatedMultipart.getBodyPart(i);
      if (bp.getFileName() == null) {
        bodyPart = (MimeBodyPart) bp;
      }
    }
    if (bodyPart == null) {
      MimeBodyPart mimeBodyPart = new MimeBodyPart();
      nestedRelatedMultipart.addBodyPart(mimeBodyPart);
      bodyPart = mimeBodyPart;
    }
    bodyPart.setContent(messageBody, "text/alternative");

    // Create the plain text part of the message.
    MimeBodyPart plainTextPart = new MimeBodyPart();
    plainTextPart.setText(plainText, "UTF-8");
    messageBody.addBodyPart(plainTextPart);

    // Create the HTML text part of the message.
    MimeBodyPart htmlTextPart = new MimeBodyPart();
    htmlTextPart.setContent(html, "text/html;charset=UTF-8");
    messageBody.addBodyPart(htmlTextPart);
    return rootMixedMultipart;
  }

  private String composeMailMessage(final String template, final Map<String, Object> templateVars, final Locale locale, final String variant) {
    String templateName;
    if ("plaintext".equals(variant)) {
      templateName = template + "-plaintext.ftl";
    } else {
      templateName = template + ".ftl";
    }

    try {
      return FreeMarkerTemplateUtils.processTemplateIntoString(freemarkerConfiguration.getTemplate(templateName, locale), templateVars);
    } catch (IOException e) {
      throw new RuntimeException("Failed to create templated mail", e);
    } catch (TemplateException e) {
      throw new RuntimeException("Failed to create templated mail", e);
    }
  }

}
