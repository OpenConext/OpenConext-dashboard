package selfservice.control.shopadmin;

import selfservice.domain.LicenseContactPerson;
import selfservice.util.LicenseContactPersonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/shopadmin/*")
public class LicenseContactPersonsController {

  @Autowired
  private LicenseContactPersonService licenseContactPersonService;

  @RequestMapping(value = "/license-contact-persons")
  public ModelAndView listAllSpsLmng(Map<String, Object> model) {
    List<LicenseContactPerson> persons = licenseContactPersonService.getPersons();
    model.put("licenseContactPersons", persons);
    return new ModelAndView("shopadmin/license-contact-persons-overview", model);
  }
}
