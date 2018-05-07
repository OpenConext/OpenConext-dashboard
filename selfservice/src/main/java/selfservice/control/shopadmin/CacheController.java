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

package selfservice.control.shopadmin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.view.RedirectView;
import selfservice.cache.ServicesCache;
import selfservice.manage.Manage;

@Controller
@RequestMapping(value = "/shopadmin")
public class CacheController extends BaseController {
  private static final Logger log = LoggerFactory.getLogger(CacheController.class);

  @Autowired
  private Manage manage;

  @Autowired
  private ServicesCache servicesCache;

  @RequestMapping(value = "/clean-cache", method = RequestMethod.GET)
  public RedirectView cleanCrmCache() {
    log.info("Cleaning caches");
    manage.refreshMetaData();
    servicesCache.evict();
    return new RedirectView("all-spslmng.shtml", true);
  }

}
