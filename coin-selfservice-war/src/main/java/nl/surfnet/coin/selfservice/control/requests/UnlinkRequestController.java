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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import nl.surfnet.coin.selfservice.command.UnlinkRequest;
import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.JiraTask;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.ActionsService;
import nl.surfnet.coin.selfservice.service.JiraService;
import nl.surfnet.coin.selfservice.service.EmailService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

@Controller
@RequestMapping("/requests")
@SessionAttributes(value = "unlinkrequest")
public class UnlinkRequestController extends BaseController {

  private static final Logger LOG = LoggerFactory.getLogger(UnlinkRequestController.class);

  @Resource(name = "providerService")
  private ServiceProviderService providerService;

  @Resource(name = "jiraService")
  private JiraService jiraService;

  @Resource(name = "actionsService")
  private ActionsService actionsService;

  @Resource(name = "emailService")
  private EmailService emailService;

  /**
   * Controller for unlink request form page.
   *
   * @param spEntityId the entity id
   * @return ModelAndView
   */
  @RequestMapping(value = "/unlinkrequest.shtml", method = RequestMethod.GET)
  public ModelAndView spUnlinkRequest(@RequestParam String spEntityId, @RequestParam Long compoundSpId,
                                      @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    Map<String, Object> m = new HashMap<String, Object>();
    final ServiceProvider sp = providerService.getServiceProvider(spEntityId, selectedidp.getId());
    m.put("sp", sp);
    m.put("compoundSpId", compoundSpId);
    m.put("unlinkrequest", new UnlinkRequest());
    return new ModelAndView("requests/unlinkrequest", m);
  }


  @RequestMapping(value = "/unlinkrequest.shtml", method = RequestMethod.POST)
  public ModelAndView spUnlinkrequestPost(@RequestParam String spEntityId, @RequestParam Long compoundSpId,
                                          @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp,
                                          @Valid @ModelAttribute("unlinkrequest") UnlinkRequest unlinkrequest,
                                          BindingResult result) {
    Map<String, Object> m = new HashMap<String, Object>();
    final ServiceProvider sp = providerService.getServiceProvider(spEntityId, selectedidp.getId());
    m.put("sp", sp);
    m.put("compoundSpId", compoundSpId);

    if (result.hasErrors()) {
      LOG.debug("Errors in data binding, will return to form view: {}", result.getAllErrors());
      return new ModelAndView("requests/unlinkrequest", m);
    } else {
      return new ModelAndView("requests/unlinkrequest-confirm", m);
    }
  }


  @RequestMapping(value = "/unlinkrequest.shtml", method = RequestMethod.POST, params="confirmed=true")
  public ModelAndView spRequestSubmitConfirm(@RequestParam String spEntityId, @RequestParam Long compoundSpId,
                                             @Valid @ModelAttribute("unlinkrequest") UnlinkRequest unlinkrequest,
                                             BindingResult result,
                                             @RequestParam(value = "confirmed") boolean confirmed,
                                             @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp,
                                             SessionStatus sessionStatus) {

    Map<String, Object> m = new HashMap<String, Object>();
    m.put("sp", providerService.getServiceProvider(spEntityId, selectedidp.getId()));
    m.put("compoundSpId", compoundSpId);

    if (result.hasErrors()) {
      LOG.debug("Errors in data binding, will return to form view: {}", result.getAllErrors());
      return new ModelAndView("requests/linkrequest-confirm", m);
    } else {
      final CoinUser currentUser = SpringSecurity.getCurrentUser();
      final JiraTask task = new JiraTask.Builder()
          .body(currentUser.getEmail() + ("\n\n" + unlinkrequest.getNotes()))
          .identityProvider(currentUser.getIdp())
          .serviceProvider(spEntityId)
          .institution(currentUser.getInstitutionId())
          .issueType(JiraTask.Type.UNLINKREQUEST)
          .status(JiraTask.Status.OPEN)
          .build();
      try {
        final String issueKey = jiraService.create(task, currentUser);
        actionsService.registerJiraIssueCreation(issueKey, task, currentUser.getUid(), currentUser.getDisplayName());
        m.put("issueKey", issueKey);
        sessionStatus.setComplete();
        return new ModelAndView("requests/unlinkrequest-thanks", m);
      } catch (IOException e) {
        LOG.debug("Error while trying to create Jira issue. Will return to form view",
            e);
        m.put("jiraError", e.getMessage());
        return new ModelAndView("requests/unlinkrequest-confirm", m);
      }
    }
  }

}
