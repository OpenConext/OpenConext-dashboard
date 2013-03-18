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

package nl.surfnet.coin.selfservice.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class InstanceTypeContextListenerTest {

  InstanceTypeContextListener l = new InstanceTypeContextListener();
  ServletContextEvent sce = mock(ServletContextEvent.class);
  ServletContext servletContext = mock(ServletContext.class);

  @Before
  public void before() {
    when(sce.getServletContext()).thenReturn(servletContext);
  }

  @Test
  public void testContextInitializedWithContextInitParam() throws Exception {
    when(servletContext.getInitParameter("instanceType")).thenReturn("dashboard");
    l.contextInitialized(sce);
    verify(servletContext).setAttribute("instanceType", "dashboard");
  }

  @Test
  public void testContextInitializedWithSystemProperty() throws Exception {
    System.setProperty("instanceType", "dashboard");
    l.contextInitialized(sce);
    verify(servletContext).setAttribute("instanceType", "dashboard");
  }

  @Test
  public void testContextInitializedWithBothSet() throws Exception {
    when(servletContext.getInitParameter("instanceType")).thenReturn("type1");
    System.setProperty("instanceType", "type2");
    l.contextInitialized(sce);
    verify(servletContext).setAttribute("instanceType", "type1");
  }
}
