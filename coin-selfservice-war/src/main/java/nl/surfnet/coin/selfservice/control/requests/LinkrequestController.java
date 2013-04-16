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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import nl.surfnet.coin.selfservice.command.LinkRequest;
import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.JiraTask;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.ActionsService;
import nl.surfnet.coin.selfservice.service.EmailService;
import nl.surfnet.coin.selfservice.service.JiraService;
import nl.surfnet.coin.selfservice.service.PersonAttributeLabelService;
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
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/requests")
@SessionAttributes(value = "linkrequest")
public class LinkrequestController extends BaseController {

  private static final Logger LOG = LoggerFactory.getLogger(LinkrequestController.class);

  @Resource(name = "providerService")
  private ServiceProviderService providerService;

  @Resource(name = "jiraService")
  private JiraService jiraService;

  @Resource(name = "emailService")
  private EmailService emailService;

  @Resource(name = "actionsService")
  private ActionsService actionsService;

  @Value("${administration.email.enabled:true}")
  private boolean sendAdministrationEmail;

  @Value("${administration.jira.ticket.enabled:false}")
  private boolean createAdministrationJiraTicket;

  private PersonAttributeLabelService personAttributeLabelService = new PersonAttributeLabelServiceJsonImpl(
      "classpath:person_attributes.json");

  /**
   * Controller for request form page.
   * 
   * @param spEntityId
   *          the entity id
   * @return ModelAndView
   */
  @RequestMapping(value = "/linkrequest.shtml", method = RequestMethod.GET)
  public ModelAndView spLinkRequest(@RequestParam String spEntityId, @RequestParam Long compoundSpId,
      @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    Map<String, Object> m = new HashMap<String, Object>();
    final ServiceProvider sp = providerService.getServiceProvider(spEntityId, selectedidp.getId());
    m.put("sp", sp);
    m.put("compoundSpId", compoundSpId);
    m.put("linkrequest", new LinkRequest());
    m.put("personAttributeLabels", personAttributeLabelService.getAttributeLabelMap());
    return new ModelAndView("requests/linkrequest", m);
  }

  @RequestMapping(value = "/linkrequest.shtml", method = RequestMethod.POST)
  public ModelAndView spRequestPost(@RequestParam String spEntityId, @RequestParam Long compoundSpId,
      @Valid @ModelAttribute("linkrequest") LinkRequest linkrequest, BindingResult result,
      @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp, SessionStatus sessionStatus) {
    Map<String, Object> m = new HashMap<String, Object>();
    final ServiceProvider sp = providerService.getServiceProvider(spEntityId, selectedidp.getId());
    m.put("sp", sp);
    m.put("compoundSpId", compoundSpId);

    if (result.hasErrors()) {
      LOG.debug("Errors in data binding, will return to form view: {}", result.getAllErrors());
      return new ModelAndView("requests/linkrequest", m);
    } else {
      final CoinUser currentUser = SpringSecurity.getCurrentUser();
      if (createAdministrationJiraTicket) {
        final JiraTask task = new JiraTask.Builder().body(currentUser.getEmail() + ("\n\n" + linkrequest.getNotes()))
            .identityProvider(currentUser.getIdp()).serviceProvider(spEntityId)
            .institution(currentUser.getInstitutionId()).issueType(JiraTask.Type.LINKREQUEST)
            .status(JiraTask.Status.OPEN).build();
        try {
          final String issueKey = jiraService.create(task, currentUser);
          actionsService.registerJiraIssueCreation(issueKey, task, currentUser.getUid(), currentUser.getDisplayName());
          m.put("issueKey", issueKey);
        } catch (IOException e) {
          LOG.debug("Error while trying to create Jira issue. Will return to form view", e);
          m.put("jiraError", e.getMessage());
          return new ModelAndView("requests/linkrequest", m);
        }
      }

      if (sendAdministrationEmail) {
        StringBuilder subject = new StringBuilder();
        subject.append("[Self Service Portal request] New connection for IdP "); 
        subject.append(selectedidp.getName());
        subject.append(" to SP ");
        subject.append(sp.getName());

        StringBuilder body = new StringBuilder();
        body.append("Domain of Reporter: " + currentUser.getSchacHomeOrganization() + "\n");
        body.append("SP EntityID: " + spEntityId + "\n");
        body.append("IdP EntityID: " + selectedidp.getId() + "\n");
        body.append("\n");
        body.append("Request: Link Request\n");
        body.append("applicant name: " + currentUser.getDisplayName() + "\n");
        body.append("applicant email: " + currentUser.getEmail() + " \n");
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:MM");
        body.append("Time: " + sdf.format(new Date()) + "\n");
        body.append("Remark from User:\n");
        body.append(linkrequest.getNotes());
        emailService.sendMail("no-reply@surfconext.nl", subject.toString(), body.toString());
      }

      sessionStatus.setComplete();
      return new ModelAndView("requests/linkrequest-thanks", m);
    }
  }

}
