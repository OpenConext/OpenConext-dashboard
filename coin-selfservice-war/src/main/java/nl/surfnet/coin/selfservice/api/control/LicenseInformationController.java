package nl.surfnet.coin.selfservice.api.control;

import java.util.Date;

import nl.surfnet.coin.selfservice.api.model.LicenseInformation;
import nl.surfnet.coin.selfservice.api.model.LicenseInformationResponse;
import nl.surfnet.coin.selfservice.api.model.LicenseStatus;
import nl.surfnet.coin.selfservice.domain.License;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/api/license/*")
public class LicenseInformationController {
  
  @RequestMapping(method = RequestMethod.GET,value = "/licenses.json")
  public @ResponseBody
  LicenseInformationResponse getLicenseInformation(@RequestParam final String idpEntityId) {
    LicenseInformationResponse result = new LicenseInformationResponse();
    result.setIdpEntityId(idpEntityId);
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
    result.addLicense(licenseInformation);
    return result;
  }
}
