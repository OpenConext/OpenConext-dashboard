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

package nl.surfnet.coin.selfservice.control.requests;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import nl.surfnet.coin.selfservice.command.Question;
import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.JiraTask;
import nl.surfnet.coin.selfservice.domain.PersonAttributeLabel;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.JiraService;
import nl.surfnet.coin.selfservice.service.EmailService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for SP detail pages
 */
@Controller
@SessionAttributes(value = { "linkrequest", "unlinkrequest" })
@RequestMapping("/requests")
public class QuestionController extends BaseController {

  @Resource(name = "providerService")
  private ServiceProviderService providerService;

  @Resource(name = "jiraService")
  private JiraService jiraService;

  @Resource(name = "emailService")
  private EmailService emailService;

  @Resource(name = "personAttributeLabelService")
  private PersonAttributeLabelServiceJsonImpl personAttributeLabelService;

  @Value("${administration.email.enabled:true}")
  private boolean sendAdministrationEmail;

  @Value("${administration.jira.ticket.enabled:false}")
  private boolean createAdministrationJiraTicket;

  private static final Logger LOG = LoggerFactory.getLogger(QuestionController.class);

  @ModelAttribute(value = "personAttributeLabels")
  public Map<String, PersonAttributeLabel> getPersonAttributeLabels() {
    return personAttributeLabelService.getAttributeLabelMap();
  }

  /**
   * Controller for question form page.
   * 
   * @param spEntityId
   *          the entity id
   * @return ModelAndView
   */
  @RequestMapping(value = "/question.shtml", method = RequestMethod.GET)
  public ModelAndView spQuestion(@RequestParam String spEntityId, @RequestParam Long compoundSpId,
      @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    Map<String, Object> m = new HashMap<String, Object>();
    final ServiceProvider sp = providerService.getServiceProvider(spEntityId, selectedidp.getId());
    m.put("question", new Question());
    m.put("sp", sp);
    m.put("compoundSpId", compoundSpId);
    return new ModelAndView("requests/question", m);
  }

  @RequestMapping(value = "/question.shtml", method = RequestMethod.POST)
  public ModelAndView spQuestionSubmit(@RequestParam String spEntityId, @RequestParam Long compoundSpId,
      @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp,
      @Valid @ModelAttribute("question") Question question, BindingResult result) {

    Map<String, Object> m = new HashMap<String, Object>();
    m.put("sp", providerService.getServiceProvider(spEntityId, selectedidp.getId()));
    m.put("compoundSpId", compoundSpId);

    if (result.hasErrors()) {
      LOG.debug("Errors in data binding, will return to form view: {}", result.getAllErrors());
      return new ModelAndView("requests/question", m);
    } else {
      final CoinUser currentUser = SpringSecurity.getCurrentUser();
      if (createAdministrationJiraTicket) {
        final JiraTask task = new JiraTask.Builder().body(question.getSubject() + ("\n\n" + question.getBody()))
            .identityProvider(currentUser.getIdp()).serviceProvider(spEntityId)
            .institution(currentUser.getInstitutionId()).issueType(JiraTask.Type.QUESTION).status(JiraTask.Status.OPEN)
            .build();

        try {
          jiraService.create(task, currentUser);
        } catch (IOException e) {
          LOG.debug("Error while trying to create Jira issue. Will return to form view", e);
          m.put("jiraError", e.getMessage());
          return new ModelAndView("requests/question", m);
        }
      }

      if (sendAdministrationEmail) {
        final String emailFrom = currentUser.getEmail();
        emailService.sendMail(emailFrom, question.getSubject(), question.getBody());
      }
      return new ModelAndView("requests/question-thanks", m);

    }
  }

  /**
   * Used from unit testing to change the behavior of this controller
   * @param sendAdministrationEmail toggle the administration email
   */
  void setSendAdministrationEmail(boolean sendAdministrationEmail) {
    this.sendAdministrationEmail = sendAdministrationEmail;
  }

  /**
   * Used from unit testing to change the behavior of this controller
   * @param createAdministrationJirraTicket toggle the creation of a Jira Ticket
   */
  void setCreateAdministrationJiraTicket(boolean createAdministrationJiraTicket) {
    this.createAdministrationJiraTicket = createAdministrationJiraTicket;
  }
  
}
