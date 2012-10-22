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

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller to display the logout page. DOES NOT EXTEND BaseController (no logged in user object)
 */
@Controller
public class LogoutController {

  @RequestMapping("/logout.shtml")
  public ModelAndView logout(HttpServletRequest request, SessionStatus status) {
    status.setComplete();
    Map<String, Object> m = new HashMap<String, Object>();
    request.getSession().invalidate();
    SecurityContextHolder.getContext().setAuthentication(null);
    return new ModelAndView("logout", m);
  }
}
