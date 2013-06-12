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
import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.csa.model.JiraTask;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.command.AbstractAction;
import nl.surfnet.coin.selfservice.command.LinkRequest;
import nl.surfnet.coin.selfservice.command.Question;
import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.domain.CoinUser;
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
  public ModelAndView spLinkRequest(@RequestParam long serviceId, @ModelAttribute(value = SELECTED_IDP) InstitutionIdentityProvider selectedIdp) {
    Map<String, Object> m = getModelMapWithService(serviceId, selectedIdp);
    m.put("linkrequest", new LinkRequest());
    m.put("personAttributeLabels", personAttributeLabelService.getAttributeLabelMap());
    return new ModelAndView("requests/linkrequest", m);
  }

  @RequestMapping(value = "/unlinkrequest.shtml", method = RequestMethod.GET)
  public ModelAndView spUnlinkRequest(@RequestParam long serviceId,
                                      @ModelAttribute(value = SELECTED_IDP) InstitutionIdentityProvider selectedIdp) {
    Map<String, Object> m = getModelMapWithService(serviceId, selectedIdp);
    m.put("unlinkrequest", new LinkRequest());
    return new ModelAndView("requests/unlinkrequest", m);
  }

  @RequestMapping(value = "/unlinkrequest.shtml", method = RequestMethod.POST)
  public ModelAndView spUnlinkrequestPost(@RequestParam Long serviceId,
                                          @ModelAttribute(value = SELECTED_IDP) InstitutionIdentityProvider selectedIdp,
                                          @Valid @ModelAttribute("unlinkrequest") LinkRequest unlinkrequest, BindingResult result) {
    Map<String, Object> m = getModelMapWithService(serviceId, selectedIdp);
    if (result.hasErrors()) {
      LOG.debug("Errors in data binding, will return to form view: {}", result.getAllErrors());
      return new ModelAndView("requests/unlinkrequest", m);
    } else {
      return new ModelAndView("requests/unlinkrequest-confirm", m);
    }
  }

  @RequestMapping(value = "/linkrequest.shtml", method = RequestMethod.POST)
  public ModelAndView spRequestPost(@Valid @ModelAttribute("linkrequest") LinkRequest linkrequest, BindingResult result,
                                    @ModelAttribute(value = SELECTED_IDP) InstitutionIdentityProvider selectedIdp, SessionStatus sessionStatus) {
    linkrequest.setType(JiraTask.Type.LINKREQUEST);
    return doSubmitConfirm(linkrequest, result, selectedIdp, sessionStatus, "requests/linkrequest", "requests/linkrequest-thanks", "jsp.sp_linkrequest.thankstext");
  }

  /**
   * Controller for question form page.
   */
  @RequestMapping(value = "/question.shtml", method = RequestMethod.GET)
  public ModelAndView spQuestion(@RequestParam long serviceId,
                                 @ModelAttribute(value = SELECTED_IDP) InstitutionIdentityProvider selectedIdp) {
    Map<String, Object> m = getModelMapWithService(serviceId, selectedIdp);
    m.put("question", new Question());
    return new ModelAndView("requests/question", m);
  }

  @RequestMapping(value = "/question.shtml", method = RequestMethod.POST)
  public ModelAndView spQuestionSubmit(@ModelAttribute(value = SELECTED_IDP) InstitutionIdentityProvider selectedIdp,
                                       @Valid @ModelAttribute("question") Question question, BindingResult result, SessionStatus sessionStatus) {

    Map<String, Object> m = new HashMap<String, Object>();
    if (result.hasErrors()) {
      LOG.debug("Errors in data binding, will return to form view: {}", result.getAllErrors());
      return new ModelAndView("requests/question", m);
    } else {
      question.setType(JiraTask.Type.QUESTION);
      return doSubmitConfirm(question, result, selectedIdp, sessionStatus, "requests/question", "requests/linkrequest-thanks", "jsp.sp_question.thankstext");
    }
  }


  @RequestMapping(value = "/unlinkrequest.shtml", method = RequestMethod.POST, params = "confirmation=true")
  public ModelAndView spRequestSubmitConfirm(@ModelAttribute("unlinkrequest") LinkRequest unlinkrequest, BindingResult result,
                                             @ModelAttribute(value = SELECTED_IDP) InstitutionIdentityProvider selectedIdp, SessionStatus sessionStatus) {
    unlinkrequest.setType(JiraTask.Type.UNLINKREQUEST);
    return doSubmitConfirm(unlinkrequest, result, selectedIdp, sessionStatus, "requests/unlinkrequest-confirm", "requests/linkrequest-thanks", "jsp.sp_unlinkrequest.thankstext");
  }

  private ModelAndView doSubmitConfirm(AbstractAction abstractAction, BindingResult result, InstitutionIdentityProvider selectedIdp, SessionStatus sessionStatus, String errorViewName, String successViewName, String thanksTextKey) {
    Map<String, Object> m = new HashMap<String, Object>();
    if (result.hasErrors()) {
      LOG.debug("Errors in data binding, will return to form view: {}", result.getAllErrors());
      return new ModelAndView(errorViewName, m);
    } else {
      final CoinUser currentUser = SpringSecurity.getCurrentUser();
      String content = abstractAction instanceof LinkRequest ? ((LinkRequest) abstractAction).getNotes() : ((Question) abstractAction).getBody();
      Action action = new Action(currentUser.getUid(), currentUser.getEmail(), currentUser.getUsername(), abstractAction.getType(), content, selectedIdp.getId(),
              abstractAction.getServiceProviderId(), selectedIdp.getInstitutionId());
      if (abstractAction.getType().equals(JiraTask.Type.QUESTION)) {
        action.setSubject(((Question) abstractAction).getSubject());
      }
      Action createdAction = csa.createAction(action);
      String issueKey = createdAction.getJiraKey();
      m.put("issueKey", issueKey);
      m.put("abstractAction", abstractAction);
      m.put("thanksTextKey", thanksTextKey);
    }
    sessionStatus.setComplete();
    return new ModelAndView(successViewName, m);
  }

  private Map<String, Object> getModelMapWithService(Long serviceId, InstitutionIdentityProvider selectedIdp) {
    Map<String, Object> m = new HashMap<String, Object>();
    final Service service = csa.getServiceForIdp(selectedIdp.getId(), serviceId);
    m.put("service", service);
    return m;
  }

}
