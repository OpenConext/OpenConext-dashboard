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
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import nl.surfnet.coin.selfservice.command.Question;
import nl.surfnet.coin.selfservice.domain.JiraTask;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.JiraService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;

/**
 * Controller for SP detail pages
 */
@Controller
@RequestMapping("/sp")
public class SpDetailController {

  @Resource(name="providerService")
  private ServiceProviderService providerService;


  @Resource(name="jiraService")
  private JiraService jiraService;
  private static final Logger LOG = LoggerFactory.getLogger(SpDetailController.class);


  /**
   * Controller for detail page.
   * @param spEntityId the entity id
   * @return ModelAndView
   */
  @RequestMapping(value="/detail.shtml")
  public ModelAndView spDetail(@RequestParam String spEntityId) {
    Map<String, Object> m = new HashMap<String, Object>();
    final ServiceProvider sp = providerService.getServiceProvider(spEntityId);
    m.put("sp", sp);
    return new ModelAndView("sp-detail", m);
  }

  /**
   * Controller for question form page.
   * @param spEntityId the entity id
   * @return ModelAndView
   */
  @RequestMapping(value="/question.shtml", method= RequestMethod.GET)
  public ModelAndView spQuestion(@RequestParam String spEntityId) {
    Map<String, Object> m = new HashMap<String, Object>();
    final ServiceProvider sp = providerService.getServiceProvider(spEntityId);
    m.put("question", new Question());
    m.put("sp", sp);
    return new ModelAndView("sp-question", m);
  }

  @RequestMapping(value="/question.shtml", method= RequestMethod.POST)
  public ModelAndView spQuestionSubmit(@RequestParam String spEntityId,
                                       @Valid @ModelAttribute("question") Question question, BindingResult result) {

    Map<String, Object> m = new HashMap<String, Object>();
    m.put("sp", providerService.getServiceProvider(spEntityId));

    if (result.hasErrors()) {
      LOG.debug("Errors in data binding, will return to form view: {}", result.getAllErrors());
      return new ModelAndView("sp-question", m);
    } else {
      final JiraTask task = new JiraTask.Builder()
          .body(question.getSubject() + ("\n\n" + question.getBody()))
              // TODO: add a separate field 'subject' in JiraTask?

          .identityProvider(SpListController.getCurrentUser().getIdp())
          .serviceProvider(spEntityId)
          .institution(SpListController.getCurrentUser().getInstitutionId())
          .issueType(JiraTask.Type.QUESTION)
          .status(JiraTask.Status.OPEN)
          .build();
      try {
        final String issueKey = jiraService.create(task);
        // TODO: log action
        m.put("issueKey", issueKey);
//        m.put("question", question);
        return new ModelAndView("sp-question-thanks", m);
      } catch (IOException e) {
        LOG.debug("Error while trying to create Jira issue. Will return to form view",
            e);
        result.addError(new ObjectError("jira", e.getMessage()));
        return new ModelAndView("sp-question", m);
      }
    }
  }


  /**
   * Controller for request form page.
   * @param spEntityId the entity id
   * @return ModelAndView
   */
  @RequestMapping(value="/requestlink.shtml", method= RequestMethod.GET)
  public ModelAndView spRequest(@RequestParam String spEntityId) {
    Map<String, Object> m = new HashMap<String, Object>();
    final ServiceProvider sp = providerService.getServiceProvider(spEntityId);
    m.put("sp", sp);
    return new ModelAndView("sp-request", m);
  }
}
