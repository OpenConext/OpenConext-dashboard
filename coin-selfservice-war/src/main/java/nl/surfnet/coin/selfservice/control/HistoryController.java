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

package nl.surfnet.coin.selfservice.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Action;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/requests")
public class HistoryController extends BaseController {

  @Resource
  private Csa csa;

  @RequestMapping(value = "/history.shtml")
  public ModelAndView listActions(@ModelAttribute(value = "selectedidp") IdentityProvider selectedidp, HttpServletRequest request) {
    //if an user acutally links to requests-overview we can dismiss the popup
    notificationPopupClosed(request);

    Map<String, Object> model = new HashMap<String, Object>();

    final List<Action> actions = csa.getJiraActions(selectedidp.getId());
    model.put("actionList", actions);
    return new ModelAndView("requests/history", model);
  }

}
