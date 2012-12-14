package nl.surfnet.coin.selfservice.service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface EmailService {
  void sendMail(String issueKey, String from, String subject, String body);

  /**
   * Send a (multipart) email message using a (freemarker) template. This method
   * will look up the html and plaintext freemarker template and will replace
   * the placeholders with the values in the templateVars. A Multipart email
   * message will be created and send.
   * 
   * @param subject
   *          The subject of the email
   * @param templateName
   *          the name of the template (it will lookup templateName.ftl and
   *          templateName-plaintext.ftl in the path from the freemarker
   *          configuration)
   * @param locale
   *          The locale to use for the template
   * @param recipients
   *          List of recipients (being email addresses)
   * @param from
   *          The 'from' email address of the sender
   * @param templateVars
   *          a map with variables to use in the Freemarker template
   */
  void sendTemplatedMultipartEmail(String subject, String templateName, Locale locale, List<String> recipients, String from,
      Map<String, Object> templateVars);

}
