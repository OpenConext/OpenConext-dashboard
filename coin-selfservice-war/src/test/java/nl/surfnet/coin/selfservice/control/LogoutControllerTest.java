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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.bind.support.SimpleSessionStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Test class for {@link LogoutController}
 */
public class LogoutControllerTest {
  private LogoutController controller = new LogoutController();

  @Test
  public void testLogout() throws Exception {
    SessionStatus status = new SimpleSessionStatus();
    final ModelAndView modelAndView = controller.logout( new MockHttpServletRequest(),status);
    assertEquals("logout", modelAndView.getViewName());
    assertTrue(modelAndView.getModelMap().isEmpty());
  }
}
