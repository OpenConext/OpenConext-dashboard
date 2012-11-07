/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.surfnet.coin.selfservice.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import javax.mail.internet.MimeMessage;

import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.shared.service.ErrorMessageMailer;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;

/**
 * FederationProviderServiceTest.java
 * 
 */
public class FederationProviderServiceTest {

  private GreenMail greenMail;
  private static ErrorMessageMailer mailer;

  @BeforeClass
  public static void beforeClass() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
    mailSender.setHost("localhost");
    mailSender.setPort(3025);

    Properties props = new Properties();
    props.put("mail.smtp.auth", false);
    props.put("mail.smtp.starttls.enable", false);

    mailer = new ErrorMessageMailer();
    mailer.setErrorMailTo("dummy@dummy.org");
    mailer.setMailSender(mailSender);
  }

  @Before
  public void before() {
    /*
     * Would have preferred a method clear and a static GreenMail, but this
     * appears the only way to make sure messages from different tests don't
     * pollute the assertions
     */
    greenMail = new GreenMail();
    greenMail.start();
  }

  @After
  public void after() {
    greenMail.stop();
  }

  @Test
  public void test_load_configuration_happy_flow() {
    FederationProviderService service = getFederationProviderService("fedcfg.xml");
    List<IdentityProvider> idps = service.getAllIdentityProviders();
    assertEquals(4, idps.size());
  }

  @Test
  public void test_error_mail_for_unparsable_content() {
    getFederationProviderService("empty_fedcfg.xml");
    assertInbox("empty_fedcfg.xml", 1);
  }

  @Test
  public void test_error_mail_for_not_existent_location() {
    getFederationProviderService("not_exists_fedcfg.xml");
    assertInbox("not_exists_fedcfg.xml", 1);
  }

  private FederationProviderService getFederationProviderService(String configLocation) {
    return new FederationProviderService(configLocation, mailer);
  }

  private void assertInbox(String bodySniplet, int sendMails) {
    MimeMessage[] messages = greenMail.getReceivedMessages();
    assertEquals(sendMails, messages.length);
    String body = GreenMailUtil.getBody(messages[0]);
    assertTrue(body.indexOf(bodySniplet) > 0);
  }
}
