/*
 * Copyright 2013 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package csa.api.control;

import csa.domain.IdentityProvider;
import csa.domain.ServiceProvider;
import csa.interceptor.AuthorityScopeInterceptor;
import csa.model.Action;
import csa.service.ActionsService;
import csa.service.EmailService;
import csa.service.IdentityProviderService;
import csa.service.ServiceProviderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping
public class JiraController extends BaseApiController {

  @Autowired
  private ActionsService actionsService;

  @Autowired
  private EmailService emailService;

  @Autowired
  private ServiceProviderService serviceProviderService;

  @Autowired
  private IdentityProviderService identityProviderService;

  @Value("${administration.email.enabled}")
  private boolean sendAdministrationEmail;

  @Value("${administration.jira.ticket.enabled}")
  private boolean createAdministrationJiraTicket;

  @RequestMapping(method = RequestMethod.GET, value = "/api/protected/actions.json")
  @ResponseBody
  public List<Action> listActions(@RequestParam("idpEntityId") String idpEntityId, HttpServletRequest request) throws IOException {
    verifyScope(request, AuthorityScopeInterceptor.OAUTH_CLIENT_SCOPE_ACTIONS);
    return actionsService.getActions(idpEntityId);
  }

  @RequestMapping(method = RequestMethod.POST, value = "/api/protected/action.json")
  @ResponseBody
  public Action newAction(HttpServletRequest request, @RequestBody Action action) throws IOException {
    verifyScope(request, AuthorityScopeInterceptor.OAUTH_CLIENT_SCOPE_ACTIONS);

    ServiceProvider serviceProvider = serviceProviderService.getServiceProvider(action.getSpId());
    IdentityProvider identityProvider = identityProviderService.getIdentityProvider(action.getIdpId());
    action.setSpName(serviceProvider.getName());
    action.setIdpName(identityProvider.getName());

    String issueKey = null;
    if (createAdministrationJiraTicket) {
      actionsService.registerJiraIssueCreation(action);
    }
    action = actionsService.registerAction(action);
    if (sendAdministrationEmail) {
      sendAdministrationEmail(serviceProvider, identityProvider, issueKey, action);
    }
    return action;
  }

  private void sendAdministrationEmail(ServiceProvider serviceProvider, IdentityProvider identityProvider, String issueKey, Action action) {
    String subject = String.format("[Csa (" + getHost() + ") request] %s connection from IdP '%s' to SP '%s' (Issue : %s)",
      action.getType().name(), action.getIdpId(), action.getSpId(), issueKey);

    StringBuilder body = new StringBuilder();
    body.append("Domain of Reporter: " + action.getInstitutionId() + "\n");
    body.append("SP EntityID: " + serviceProvider.getId() + "\n");
    body.append("SP Name: " + serviceProvider.getName() + "\n");

    body.append("IdP EntityID: " + identityProvider.getId() + "\n");
    body.append("IdP Name: " + identityProvider.getName() + "\n");


    body.append("Request: " + action.getType().name() + "\n");
    body.append("Applicant name: " + action.getUserName() + "\n");
    body.append("Applicant email: " + action.getUserEmail() + " \n");
    body.append("Mail applicant: mailto:" + action.getUserEmail() + "?CC=surfconext-beheer@surfnet.nl&SUBJECT=[" + issueKey + "]%20" + action.getType().name() + "%20to%20" + serviceProvider.getName() + "&BODY=Beste%20" + action.getUserName() + " \n");

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:MM");
    body.append("Time: " + sdf.format(new Date()) + "\n");
    body.append("Remark from User:\n");
    body.append(action.getBody());
    emailService.sendMail(action.getUserEmail(), subject.toString(), body.toString());
  }

  private String getHost() {
    try {
      return InetAddress.getLocalHost().toString();
    } catch (UnknownHostException e) {
      return "UNKNOWN";
    }
  }

}
