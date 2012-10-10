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

package nl.surfnet.coin.selfservice.control.shopadmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.selfservice.command.LmngIdentityBinding;
import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.dao.LmngIdentifierDao;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.service.IdentityProviderService;
import nl.surfnet.coin.selfservice.service.LicensingService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/shopadmin/*")
public class IdpLnmgListController extends BaseController {
  private static final Logger log = LoggerFactory.getLogger(IdpLnmgListController.class);

  @Autowired
  private IdentityProviderService idpService;

  @Resource(name = "licensingService")
  private LicensingService licensingService;

  @Autowired
  private LmngIdentifierDao lmngIdentifierDao;

  @RequestMapping(value = "/all-idpslmng")
  public ModelAndView listAllIdps() {
    Map<String, Object> m = new HashMap<String, Object>();

    List<LmngIdentityBinding> lmngIdpBindings = new ArrayList<LmngIdentityBinding>();
    for (IdentityProvider identityProvider : idpService.getAllIdentityProviders()) {
      LmngIdentityBinding lmngIdentityBinding = new LmngIdentityBinding(identityProvider);
      String lmngId = lmngIdentifierDao.getLmngIdForIdentityProviderId(identityProvider.getInstitutionId());
      lmngIdentityBinding.setLmngIdentifier(lmngId);
      lmngIdpBindings.add(lmngIdentityBinding);
    }

    m.put("menu", buildMenu(MenuType.IDPADMIN, "all-idpslmng"));
    m.put("bindings", lmngIdpBindings);
    return new ModelAndView("shopadmin/idp-overview", m);
  }

  @RequestMapping(value = "/save-idplmng", method = RequestMethod.POST)
  public ModelAndView saveLmngServices(HttpServletRequest req) {
    String idpId = req.getParameter("idpIdentifier");
    String lmngId = req.getParameter("lmngIdentifier");
    if ("clear".equalsIgnoreCase(req.getParameter("submit"))) {
      log.debug("Clearing lmng identifier for IdentityProvider with institutionID " + idpId );
      lmngId = null;
    } else {
      log.debug("Storing lmng identifier " + lmngId + " for IdentityProvider with institutionID " + idpId );
    }
    lmngIdentifierDao.saveOrUpdateLmngIdForIdentityProviderId(idpId, lmngId);

    return listAllIdps();
  }

}
