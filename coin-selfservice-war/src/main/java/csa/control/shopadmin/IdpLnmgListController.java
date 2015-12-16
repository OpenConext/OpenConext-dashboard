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

package csa.control.shopadmin;

import csa.api.cache.CrmCache;
import csa.api.cache.ServicesCache;
import csa.command.LmngIdentityBinding;
import csa.service.CrmService;
import csa.api.cache.ProviderCache;
import csa.control.BaseController;
import csa.dao.LmngIdentifierDao;
import csa.domain.IdentityProvider;
import csa.service.IdentityProviderService;
import csa.service.ServiceProviderService;
import csa.service.impl.LmngUtil;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/shopadmin/*")
public class IdpLnmgListController extends BaseController {
  private static final Logger log = LoggerFactory.getLogger(IdpLnmgListController.class);

  @Resource
  private IdentityProviderService idpService;

  @Resource
  private CrmService licensingService;

  @Autowired
  private LmngIdentifierDao lmngIdentifierDao;

  @Resource
  private ServicesCache servicesCache;

  @Resource
  private ProviderCache providerCache;

  @Resource
  private CrmCache crmCache;

  @Resource
  private ServiceProviderService serviceProviderService;

  private LmngUtil lmngUtil = new LmngUtil();

  @RequestMapping(value = "/all-idpslmng")
  public ModelAndView listAllIdps(Map<String, Object> model) {
    if (model == null) {
      model = new HashMap<>();
    }

    List<LmngIdentityBinding> lmngIdpBindings = new ArrayList<>();
    for (IdentityProvider identityProvider : idpService.getAllIdentityProviders()) {
      LmngIdentityBinding lmngIdentityBinding = new LmngIdentityBinding(identityProvider);
      String lmngId = lmngIdentifierDao.getLmngIdForIdentityProviderId(identityProvider.getInstitutionId());
      lmngIdentityBinding.setLmngIdentifier(lmngId);
      lmngIdpBindings.add(lmngIdentityBinding);
    }

    model.put("accounts", licensingService.getAccounts(true));

    model.put("bindings", lmngIdpBindings);
    return new ModelAndView("shopadmin/idp-overview", model);
  }

  @RequestMapping(value = "/save-idplmng", method = RequestMethod.POST)
  public ModelAndView saveLmngServices(HttpServletRequest req) {
    Map<String, Object> model = new HashMap<>();

    String idpId = req.getParameter("idpIdentifier");
    String lmngId = req.getParameter("lmngIdentifier");
    Integer index = Integer.valueOf(req.getParameter("index"));

    String isClearPressed = req.getParameter("clearbutton");
    if (StringUtils.isBlank(lmngId) || StringUtils.isNotBlank(isClearPressed)) {
      log.debug("Clearing lmng identifier for IdentityProvider with institutionID " + idpId );
      lmngId = null;
    } else {
      // extra validation (also done in frontend/jquery)
      if (!lmngUtil.isValidGuid(lmngId)) {
        model.put("errorMessage", "jsp.lmng_binding_overview.wrong.guid");
        model.put("messageIndex", index);
        return listAllIdps(model);
      }

      String institutionLmngName = licensingService.getInstitutionName(lmngId);
      if (institutionLmngName == null) {
        model.put("errorMessage", "jsp.lmng_binding_overview.unknown.guid");
        model.put("messageIndex", index);
      } else {
        model.put("infoMessage", institutionLmngName);
        model.put("messageIndex", index);
      }

      log.debug("Storing lmng identifier '" + lmngId + "' for IdentityProvider with institutionID " + idpId );
    }
    lmngIdentifierDao.saveOrUpdateLmngIdForIdentityProviderId(idpId, lmngId);
    return listAllIdps(model);
  }

  @RequestMapping(value = "/clean-cache", method = RequestMethod.GET)
  public RedirectView cleanCrmCache() {
    log.info("Cleaning caches");
    serviceProviderService.refreshExampleSingleTenants();
    licensingService.evictCache();
    servicesCache.evict();
    providerCache.evict();
    crmCache.evict();
    return new RedirectView("all-spslmng.shtml", true);
  }

}
