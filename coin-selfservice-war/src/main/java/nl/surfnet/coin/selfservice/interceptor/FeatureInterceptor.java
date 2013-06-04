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

package nl.surfnet.coin.selfservice.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.surfnet.coin.selfservice.util.SpringSecurity;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor to enable/disable (new) features
 */
public class FeatureInterceptor extends HandlerInterceptorAdapter {
  private boolean developmentMode;
  private boolean crmAvailable;
  private boolean oauthTokensAvailable;
  private boolean statisticsAvailable;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    request.setAttribute("developmentMode", developmentMode); // for use on error page
    request.setAttribute("crmAvailable", crmAvailable);
    request.setAttribute("oauthTokensAvailable", oauthTokensAvailable);
    request.setAttribute("statisticsAvailable", statisticsAvailable);
    return true;
  }

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    super.postHandle(request, response, handler, modelAndView);

    if (modelAndView != null) {
      final ModelMap map = modelAndView.getModelMap();
      map.addAttribute("developmentMode", developmentMode);
      map.addAttribute("crmAvailable", crmAvailable);
      map.addAttribute("oauthTokensAvailable", oauthTokensAvailable);
      map.addAttribute("statisticsAvailable", statisticsAvailable);
      map.addAttribute("roles", SpringSecurity.getCurrentUser().getAuthorities());
    }
  }

  public void setDevelopmentMode(boolean devMode) {
    this.developmentMode = devMode;
  }

  public void setCrmAvailable(boolean crmActive) {
    this.crmAvailable = crmActive;
  }

  public boolean isCrmAvailable() {
    return crmAvailable;
  }

  /**
   * @return the showOauthTokens
   */
  public boolean isOauthTokensAvailable() {
    return oauthTokensAvailable;
  }

  /**
   * @param showOauthTokens the showOauthTokens to set
   */
  public void setOauthTokensAvailable(boolean showOauthTokens) {
    this.oauthTokensAvailable = showOauthTokens;
  }

  /**
   * @return the ebLinkActive
   */
  public boolean isStatisticsAvailable() {
    return statisticsAvailable;
  }

  /**
   * @param statisticsActive the ebLinkActive to set
   */
  public void setStatisticsAvailable(boolean statisticsActive) {
    this.statisticsAvailable = statisticsActive;
  }
  
}
