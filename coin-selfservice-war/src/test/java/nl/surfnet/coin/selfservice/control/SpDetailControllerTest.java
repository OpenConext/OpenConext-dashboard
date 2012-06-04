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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
import nl.surfnet.coin.selfservice.service.JiraService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class SpDetailControllerTest {


  @InjectMocks
  private SpDetailController spDetailController;

  @Mock
  private JiraService jiraService;

  @Mock
  private ServiceProviderService sps;

  @Mock
  private CoinUser coinUser;

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
  public void questionPostHappy() {
    Question question = new Question();
    BindingResult result = new BeanPropertyBindingResult(question, "question");
    final ModelAndView mav = spDetailController.spQuestionSubmit("foobar", getIdp(), question, result);
    assertTrue(mav.hasView());
    assertThat(mav.getViewName(), is("sp-question-thanks"));
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
