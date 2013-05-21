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

package nl.surfnet.coin.selfservice.api.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import nl.surfnet.coin.selfservice.api.model.LicenseInformation;
import nl.surfnet.coin.selfservice.api.model.LicenseStatus;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.License;
import nl.surfnet.coin.selfservice.service.IdentityProviderService;
import nl.surfnet.coin.selfservice.service.LmngService;
import nl.surfnet.coin.selfservice.service.impl.CompoundSPService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/api/license/*")
public class LicenseInformationController {
  
  @Resource
  private LmngService lmngService;

  @Resource
  private IdentityProviderService idpService;
  
  @Resource
  private CompoundSPService compoundSPService;
  
  @RequestMapping(method = RequestMethod.GET,value = "/licenses.json")
  public @ResponseBody
  List<LicenseInformation> getLicenseInformation(@RequestParam final String idpEntityId) {
    IdentityProvider identityProvider = idpService.getIdentityProvider(idpEntityId);
    List<CompoundServiceProvider> csPs = compoundSPService.getCSPsByIdp(identityProvider);
    
    
    List<LicenseInformation> result = new ArrayList<LicenseInformation>();
    LicenseInformation licenseInformation = new LicenseInformation();
    License license = new License();
    license.setEndDate(new Date());
    license.setGroupLicense(true);
    license.setInstitutionName("Institution Name");
    license.setLicenseNumber("DWS-XX-GLK76");
    license.setStartDate(new Date());
    licenseInformation.setLicense(license);
    licenseInformation.setSpEntityId("spEntityId");
    licenseInformation.setStatus(LicenseStatus.AVAILABLE);
    result.add(licenseInformation);
    return result;
  }
}
