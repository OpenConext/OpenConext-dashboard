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

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Action;
import nl.surfnet.coin.csa.model.JiraTask;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.command.LinkRequest;
import nl.surfnet.coin.selfservice.command.Question;
import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.service.PersonAttributeLabelService;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/requests")
@SessionAttributes(value = "linkrequest")
public class LinkrequestController extends BaseController {

  private static final Logger LOG = LoggerFactory.getLogger(LinkrequestController.class);

  @Resource
  private Csa csa;

  private PersonAttributeLabelService personAttributeLabelService = new PersonAttributeLabelServiceJsonImpl(
          "classpath:person_attributes.json");

  @RequestMapping(value = "/linkrequest.shtml", method = RequestMethod.GET)
  public ModelAndView spLinkRequest(@RequestParam long serviceId, @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    Map<String, Object> m = getModelMapWithService(serviceId, selectedidp);
    m.put("linkrequest", new LinkRequest());
    m.put("personAttributeLabels", personAttributeLabelService.getAttributeLabelMap());
    return new ModelAndView("requests/linkrequest", m);
  }

  @RequestMapping(value = "/unlinkrequest.shtml", method = RequestMethod.GET)
  public ModelAndView spUnlinkRequest(@RequestParam long serviceId,
                                      @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    Map<String, Object> m = getModelMapWithService(serviceId, selectedidp);
    m.put("unlinkrequest", new LinkRequest());
    return new ModelAndView("requests/unlinkrequest", m);
  }

  @RequestMapping(value = "/unlinkrequest.shtml", method = RequestMethod.POST)
  public ModelAndView spUnlinkrequestPost(@RequestParam Long serviceId,
                                          @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp,
                                          @Valid @ModelAttribute("unlinkrequest") LinkRequest unlinkrequest, BindingResult result) {
    Map<String, Object> m = getModelMapWithService(serviceId, selectedidp);
    if (result.hasErrors()) {
      LOG.debug("Errors in data binding, will return to form view: {}", result.getAllErrors());
      return new ModelAndView("requests/unlinkrequest", m);
    } else {
      return new ModelAndView("requests/unlinkrequest-confirm", m);
    }
  }

  @RequestMapping(value = "/linkrequest.shtml", method = RequestMethod.POST)
  public ModelAndView spRequestPost(@Valid @ModelAttribute("linkrequest") LinkRequest linkrequest, BindingResult result,
                                    @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp, SessionStatus sessionStatus) {
    linkrequest.setType(JiraTask.Type.LINKREQUEST);
    return doSubmitConfirm(linkrequest, result, selectedidp, sessionStatus, "requests/linkrequest", "requests/linkrequest-thanks", "jsp.sp_linkrequest.thankstext");
  }

  /**
   * Controller for question form page.
   */
  @RequestMapping(value = "/question.shtml", method = RequestMethod.GET)
  public ModelAndView spQuestion(@RequestParam long serviceId,
                                 @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    Map<String, Object> m = new HashMap<String, Object>();
    // FIXME: validate serviceId
    final Service service = csa.getServiceForIdp(selectedidp.getId(), serviceId);
    m.put("question", new Question());
    m.put("service", service);
    m.put("serviceId", serviceId);
    return new ModelAndView("requests/question", m);
  }

  @RequestMapping(value = "/question.shtml", method = RequestMethod.POST)
  public ModelAndView spQuestionSubmit(@RequestParam long serviceId,
                                       @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp,
                                       @Valid @ModelAttribute("question") Question question, BindingResult result) {

    Map<String, Object> m = new HashMap<String, Object>();
    return new ModelAndView("requests/question-thanks", m);
  }


//  @RequestMapping(value = "/unlinkrequest.shtml", method = RequestMethod.POST, params = "agree=true")
//  public ModelAndView spRequestSubmitConfirm(@RequestParam String spEntityId, @RequestParam Long compoundSpId,
//                                             @ModelAttribute("unlinkrequest") LinkRequest unlinkrequest, BindingResult result,
//                                             @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp, SessionStatus sessionStatus) {
//    unlinkrequest.setUnlinkRequest(true);
//    return doSubmitConfirm(spEntityId, compoundSpId, unlinkrequest, result, selectedidp, sessionStatus, "requests/unlinkrequest-confirm", JiraTask.Type.UNLINKREQUEST, "requests/unlinkrequest-thanks");
//  }

  private ModelAndView doSubmitConfirm(LinkRequest linkRequest, BindingResult result, IdentityProvider selectedidp, SessionStatus sessionStatus, String errorViewName, String successViewName, String thanksTextKey) {
    Map<String, Object> m = new HashMap<String, Object>();
    if (result.hasErrors()) {
      LOG.debug("Errors in data binding, will return to form view: {}", result.getAllErrors());
      return new ModelAndView(errorViewName, m);
    } else {
      final CoinUser currentUser = SpringSecurity.getCurrentUser();
      Action action = new Action(currentUser.getUid(), currentUser.getEmail(), currentUser.getUsername(), linkRequest.getType(), linkRequest.getNotes(), selectedidp.getId(),
              linkRequest.getServiceProviderId(), selectedidp.getInstitutionId());
      Action createdAction = csa.createAction(action);
      String issueKey = createdAction.getJiraKey();
      m.put("issueKey", issueKey);
      m.put("linkRequest", linkRequest);
      m.put("thanksTextKey", thanksTextKey);
    }
    sessionStatus.setComplete();
    return new ModelAndView(successViewName, m);
  }

  private Map<String, Object> getModelMapWithService(Long serviceId, IdentityProvider selectedidp) {
    Map<String, Object> m = new HashMap<String, Object>();
    final Service service = csa.getServiceForIdp(selectedidp.getId(), serviceId);
    m.put("service", service);
    return m;
  }

}
