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

import java.util.Collection;
import java.util.List;

import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.Menu;
import nl.surfnet.coin.selfservice.domain.MenuItem;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor to manipulate the {@link CompoundServiceProvider} objects for display
 */
public class AuthorityScopeInterceptor extends HandlerInterceptorAdapter {

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
      throws Exception {

    if (modelAndView != null) {
      final ModelMap map = modelAndView.getModelMap();
      
    }
  }

  
}
