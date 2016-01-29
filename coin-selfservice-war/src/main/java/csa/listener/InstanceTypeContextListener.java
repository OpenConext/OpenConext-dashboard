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

package csa.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Define a property ${instanceType}in the servlet context that says what type of instance of self service we're running.
 *
 */
public class InstanceTypeContextListener implements ServletContextListener {

  private static final Logger LOG = LoggerFactory.getLogger(InstanceTypeContextListener.class);

  @Override
  public void contextInitialized(ServletContextEvent sce) {
    String initParameter = sce.getServletContext().getInitParameter("instanceType");
    if (initParameter != null) {
      setType(sce, initParameter);
      LOG.debug("Set instanceType to '{}', based on init parameter", initParameter);
    } else {
      String property = System.getProperty("instanceType");
      setType(sce, property);
      LOG.debug("Set instanceType to '{}', based on system property", property);
    }
  }

  private void setType(ServletContextEvent sce, String instanceType) {
    sce.getServletContext().setAttribute("instanceType", instanceType);
  }

  @Override
  public void contextDestroyed(ServletContextEvent sce) {
    // No implementation.
  }

}
