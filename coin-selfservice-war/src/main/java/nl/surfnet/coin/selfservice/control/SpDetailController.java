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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import nl.surfnet.coin.selfservice.command.Question;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;

/**
 * Controller for SP detail pages
 */
@Controller
@RequestMapping("/sp")
public class SpDetailController {

  @Resource(name="providerService")
  private ServiceProviderService providerService;

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
    m.put("sp", sp);
    return new ModelAndView("sp-question", m);
  }

  @RequestMapping(value="/question.shtml", method= RequestMethod.POST)
  public ModelAndView spQuestionSubmit(@RequestParam String spEntityId, @ModelAttribute("question") Question
      question, BindingResult result) {
    // TODO: save with JiraService
    // if result.hasErrors()....
    Map<String, Object> m = new HashMap<String, Object>();
    final ServiceProvider sp = providerService.getServiceProvider(spEntityId);
    m.put("sp", sp);
    m.put("question", question);
    return new ModelAndView("sp-question-thanks", m);
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
