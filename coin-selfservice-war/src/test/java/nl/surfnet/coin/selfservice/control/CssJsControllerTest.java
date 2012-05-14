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

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * Test for {@link CssJsController}
 */
public class CssJsControllerTest {

  @Test
  public void testJs() throws Exception {
    CssJsController controller = new CssJsController();
    String view = controller.js();
    assertEquals("js", view);
  }

  @Test
  public void testCss() throws Exception {
    CssJsController controller = new CssJsController();
    String view = controller.css();
    assertEquals("css", view);
  }
}
