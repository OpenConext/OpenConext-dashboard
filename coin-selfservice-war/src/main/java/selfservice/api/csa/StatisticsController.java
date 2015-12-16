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

package selfservice.api.csa;


import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import selfservice.domain.Statistics;
import selfservice.interceptor.AuthorityScopeInterceptor;
import selfservice.service.StatisticsService;


@Controller
@RequestMapping
public class StatisticsController extends BaseApiController {

  private static final Logger LOG = LoggerFactory.getLogger(StatisticsController.class);

  @Autowired
  private StatisticsService statisticsService;

  @RequestMapping(method = RequestMethod.GET, value = "/api/protected/stats.json")
  public
  @ResponseBody
  Statistics getCSAStatistics(@RequestParam(value = "month") int month, @RequestParam(value = "year") int year, final HttpServletRequest request) {
    verifyScope(request, AuthorityScopeInterceptor.OAUTH_CLIENT_SCOPE_STATISTICS);
    LOG.info("returning statistics for CSA");
    return statisticsService.getStatistics(month, year);
  }
}
