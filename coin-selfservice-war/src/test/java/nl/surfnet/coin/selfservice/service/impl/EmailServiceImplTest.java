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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import nl.surfnet.coin.selfservice.domain.Article;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.shared.service.MailService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import freemarker.template.Configuration;

public class EmailServiceImplTest {

  @InjectMocks
  private EmailServiceImpl emailService;

  @Mock
  private MailService mailService;

  private Configuration freemarkerConfiguration;

  @Before
  public void setUp() throws Exception {
    emailService = new EmailServiceImpl();
    FreeMarkerConfigurationFactoryBean freemarkerFactory = new FreeMarkerConfigurationFactoryBean();
    freemarkerFactory.setTemplateLoaderPath("classpath:/ftl/");
    freemarkerConfiguration = freemarkerFactory.createConfiguration();
    emailService.setFreemarkerConfiguration(freemarkerConfiguration);
    
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void sendTemplatedMultipartEmailTest() {
    // create parameters for method call
    List<String> recipients = Arrays.asList(new String[] { "recipient1@test.test" });
    
    // create dummy CSP with service and article
    ServiceProvider serviceProvider = new ServiceProvider("testid");
    serviceProvider.setName("testname");
    CompoundServiceProvider csp = CompoundServiceProvider.builder(serviceProvider, new Article("testArticleId"));
    csp.setId(1l);

    // create map with variables for the template
    Map<String, Object> templateVars = new HashMap<String, Object>();
    templateVars.put("compoundSp", csp);
    templateVars.put("appstoreURL", "https://selfservice.test.surfconext.nl/");
    templateVars.put("recommendPersonalNote", "This is a personal note");
    templateVars.put("invitername", "testname");
    
    emailService.sendTemplatedMultipartEmail("test", EmailServiceImpl.RECOMMENTATION_EMAIL_TEMPLATE, Locale.getDefault(), recipients,
        "sender@test.test", templateVars);
    
  }
}
