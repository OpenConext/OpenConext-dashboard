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

import nl.surfnet.coin.selfservice.command.LinkRequest;
import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.JiraTask;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.*;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

  @RequestMapping(value = "/linkrequest.shtml", method = RequestMethod.GET)
  public ModelAndView spLinkRequest(@RequestParam String spEntityId, @RequestParam Long compoundSpId,
      @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    Map<String, Object> m = getModelMapWithSP(spEntityId, compoundSpId, selectedidp);
    m.put("linkrequest", new LinkRequest());
    m.put("personAttributeLabels", personAttributeLabelService.getAttributeLabelMap());
    return new ModelAndView("requests/linkrequest", m);
  }

  @RequestMapping(value = "/unlinkrequest.shtml", method = RequestMethod.GET)
  public ModelAndView spUnlinkRequest(@RequestParam String spEntityId, @RequestParam Long compoundSpId,
                                      @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    Map<String, Object> m = getModelMapWithSP(spEntityId, compoundSpId, selectedidp);
    m.put("unlinkrequest", new LinkRequest());
    return new ModelAndView("requests/unlinkrequest", m);
  }

  @RequestMapping(value = "/unlinkrequest.shtml", method = RequestMethod.POST)
  public ModelAndView spUnlinkrequestPost(@RequestParam String spEntityId, @RequestParam Long compoundSpId,
                                          @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp,
                                          @Valid @ModelAttribute("unlinkrequest") LinkRequest unlinkrequest, BindingResult result) {
    Map<String, Object> m = getModelMapWithSP(spEntityId, compoundSpId, selectedidp);
    if (result.hasErrors()) {
      LOG.debug("Errors in data binding, will return to form view: {}", result.getAllErrors());
      return new ModelAndView("requests/unlinkrequest", m);
    } else {
      return new ModelAndView("requests/unlinkrequest-confirm", m);
    }
  }

  @RequestMapping(value = "/linkrequest.shtml", method = RequestMethod.POST)
  public ModelAndView spRequestPost(@RequestParam String spEntityId, @RequestParam Long compoundSpId,
      @Valid @ModelAttribute("linkrequest") LinkRequest linkrequest, BindingResult result,
      @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp, SessionStatus sessionStatus) {

    return doSubmitConfirm(spEntityId, compoundSpId, linkrequest, result, selectedidp, sessionStatus, "requests/linkrequest", JiraTask.Type.LINKREQUEST, "requests/linkrequest-thanks");
  }

  @RequestMapping(value = "/unlinkrequest.shtml", method = RequestMethod.POST, params = "agree=true")
  public ModelAndView spRequestSubmitConfirm(@RequestParam String spEntityId, @RequestParam Long compoundSpId,
                                             @ModelAttribute("unlinkrequest") LinkRequest unlinkrequest, BindingResult result,
                                             @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp, SessionStatus sessionStatus) {
    unlinkrequest.setUnlinkRequest(true);
    return doSubmitConfirm(spEntityId, compoundSpId, unlinkrequest, result, selectedidp, sessionStatus, "requests/unlinkrequest-confirm", JiraTask.Type.UNLINKREQUEST, "requests/unlinkrequest-thanks");
  }

  private ModelAndView doSubmitConfirm(String spEntityId, Long compoundSpId, LinkRequest unlinkrequest, BindingResult result, IdentityProvider selectedidp, SessionStatus sessionStatus, String errorViewName, JiraTask.Type jiraType, String successViewName) {
    Map<String, Object> m = getModelMapWithSP(spEntityId, compoundSpId, selectedidp);
    ServiceProvider selectedSp = (ServiceProvider) m.get("sp");

    if (result.hasErrors()) {
      LOG.debug("Errors in data binding, will return to form view: {}", result.getAllErrors());
      return new ModelAndView(errorViewName, m);
    } else {
      final CoinUser currentUser = SpringSecurity.getCurrentUser();
      String issueKey = null;
      if (createAdministrationJiraTicket) {
        try {
          final JiraTask task = new JiraTask.Builder()
                  .body(currentUser.getEmail() + ("\n\n" + unlinkrequest.getNotes()))
                  .identityProvider(currentUser.getIdp()).serviceProvider(spEntityId)
                  .institution(currentUser.getInstitutionId()).issueType(jiraType)
                  .status(JiraTask.Status.OPEN).build();
          issueKey = jiraService.create(task, currentUser);
          actionsService.registerJiraIssueCreation(issueKey, task, currentUser.getUid(), currentUser.getDisplayName());
          m.put("issueKey", issueKey);
        } catch (IOException e) {
          LOG.debug("Error while trying to create Jira issue. Will return to form view", e);
          m.put("jiraError", e.getMessage());
          return new ModelAndView(errorViewName, m);
        }
      }

      if (sendAdministrationEmail) {
        sendAdministrationEmail(unlinkrequest, selectedidp, selectedSp, currentUser, issueKey);
      }
    }
    sessionStatus.setComplete();
    return new ModelAndView(successViewName, m);
  }

  private void sendAdministrationEmail(LinkRequest unlinkrequest, IdentityProvider idp, ServiceProvider sp, CoinUser currentUser, String issueKey) {
    String action = unlinkrequest.isUnlinkRequest() ? "Delete" : "New";
    String subject = String.format("[Dashboard (" + getHost() + ") request] %s connection from IdP '%s' to SP '%s' (Issue : %s)",
            action, idp.getName(), sp.getName(), issueKey);

    StringBuilder body = new StringBuilder();
    body.append("Domain of Reporter: " + currentUser.getSchacHomeOrganization() + "\n");
    body.append("SP EntityID: " + sp.getId() + "\n");
    body.append("SP Name: " + sp.getName() + "\n");

    body.append("IdP EntityID: " + idp.getId() + "\n");
    body.append("IdP Name: " + idp.getName() + "\n");


    String requestType = unlinkrequest.isUnlinkRequest() ? "Disconnect" : "Connect";
    body.append("Request: " + requestType + "\n");
    body.append("Applicant name: " + currentUser.getDisplayName() + "\n");
    body.append("Applicant email: " + currentUser.getEmail() + " \n");
    body.append("Mail applicant: mailto:"+currentUser.getEmail()+"?CC=surfconext-beheer@surfnet.nl&SUBJECT=["+issueKey+"]%20"+requestType+"%20to%20"+sp.getName()+"&BODY=Beste%20" + currentUser.getDisplayName() + " \n");

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:MM");
    body.append("Time: " + sdf.format(new Date()) + "\n");
    body.append("Remark from User:\n");
    body.append(unlinkrequest.getNotes());
    emailService.sendMail(currentUser.getEmail(), subject.toString(), body.toString());
  }


  private Map<String, Object> getModelMapWithSP(String spEntityId, Long compoundSpId, IdentityProvider selectedidp) {
    Map<String, Object> m = new HashMap<String, Object>();
    final ServiceProvider sp = providerService.getServiceProvider(spEntityId, selectedidp.getId());
    m.put("sp", sp);
    m.put("compoundSpId", compoundSpId);
    return m;
  }

  private String getHost() {
    try {
      return InetAddress.getLocalHost().toString();
    } catch (UnknownHostException e) {
      return "UNKNOWN";
    }
  }


}
