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

package nl.surfnet.coin.selfservice.control;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.servlet.ModelAndView;

import nl.surfnet.coin.selfservice.command.Question;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.JiraTask;
import nl.surfnet.coin.selfservice.service.ActionsService;
import nl.surfnet.coin.selfservice.service.JiraService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;
import nl.surfnet.coin.shared.service.MailService;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SpDetailControllerTest {


  @InjectMocks
  private SpDetailController spDetailController;

  @Mock
  private JiraService jiraService;

  @Mock
  private ServiceProviderService sps;

  @Mock
  private ActionsService actionsService;

  @Mock
  private CoinUser coinUser;

  @Mock
  private MailService mailService;

  @Before
  public void before() {
    spDetailController = new SpDetailController();
    MockitoAnnotations.initMocks(this);
    SecurityContextHolder.getContext().setAuthentication(getAuthentication());
  }

  private Authentication getAuthentication() {
    return new TestingAuthenticationToken(coinUser, "");
  }

  @Test
  public void details() {
    final ModelAndView mav = spDetailController.spDetail("foobar", getIdp());
    assertTrue(mav.hasView());
    assertThat(mav.getViewName(), is("sp-detail"));
  }
  @Test
  public void questionGET() {
    final ModelAndView mav = spDetailController.spQuestion("foobar", getIdp());
    assertTrue(mav.hasView());
    assertThat(mav.getViewName(), is("sp-question"));
    assertTrue(mav.getModel().containsKey("question"));
  }

  @Test
  public void questionPostHappy() throws IOException {
    when(jiraService.create(Matchers.<JiraTask>any(), Matchers.<CoinUser>any())).thenReturn("TEST-001");
    Question question = new Question();
    BindingResult result = new BeanPropertyBindingResult(question, "question");
    final ModelAndView mav = spDetailController.spQuestionSubmit("foobar", getIdp(), question, result);
    verify(jiraService).create((JiraTask) anyObject(), (CoinUser) anyObject());
    verify(mailService).sendAsync((SimpleMailMessage) anyObject());
    assertTrue(mav.hasView());
    assertThat(mav.getViewName(), is("sp-question-thanks"));
  }

  @Test
  public void questionThrowsJiraError() throws IOException {
    Question question = new Question();
    BindingResult result = new BeanPropertyBindingResult(question, "question");
    when(jiraService.create((JiraTask) anyObject(), Matchers.<CoinUser>any())).thenThrow(new IOException("An IOException on purpose"));
    final ModelAndView mav = spDetailController.spQuestionSubmit("foobar", getIdp(), question, result);
    verify(actionsService, never()).registerJiraIssueCreation(anyString(), (JiraTask) anyObject());
    assertTrue(mav.hasView());
    assertThat("in case of error the form view should be returned", mav.getViewName(), is("sp-question"));
  }

  @Test
  public void questionPostWithValidationError() {
    Question question = new Question();
    BindingResult result = new BeanPropertyBindingResult(question, "question");
    result.addError(new ObjectError("question", "foo 123 is required"));
    final ModelAndView mav = spDetailController.spQuestionSubmit("foobar", getIdp(), question, result);
    assertTrue(mav.hasView());
    assertThat(mav.getViewName(), is("sp-question"));
  }

  private static IdentityProvider getIdp() {
    return new IdentityProvider("idp", null, "idp");
  }
}
