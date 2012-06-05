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

import org.junit.Before;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for {@link LogoutController}
 */
public class LogoutControllerTest {
  private LogoutController controller;

  @Before
  public void setUp() throws Exception {
    controller = new LogoutController();
  }

  @Test
  public void testLogout() throws Exception {
    final ModelAndView modelAndView = controller.logout();
    assertEquals("logout", modelAndView.getViewName());
    assertTrue(modelAndView.getModelMap().isEmpty());
  }
}
