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

import com.google.common.base.Throwables;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import selfservice.command.ServiceBinding;
import selfservice.dao.CompoundServiceProviderDao;
import selfservice.domain.LicenseStatus;
import selfservice.domain.csa.CompoundServiceProvider;
import selfservice.service.impl.CompoundServiceProviderService;
import selfservice.manage.Manage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Controller
@RequestMapping(value = "/shopadmin")
public class SpLmngListController extends BaseController {

  private static final Logger log = LoggerFactory.getLogger(SpLmngListController.class);

  @Autowired private Manage manage;
  @Autowired private CompoundServiceProviderService compoundSPService;
  @Autowired private CompoundServiceProviderDao compoundServiceProviderDao;

  private UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});

  @RequestMapping(value = "/all-spslmng")
  public ModelAndView listAllSpsLmng(Map<String, Object> model) {
    List<ServiceBinding> serviceBindings = getAllBindings();
    List<ServiceBinding> cspOrphans = getOrphans(serviceBindings);

    model.put("bindings", serviceBindings);
    model.put("orphans", cspOrphans);
    model.put("licenseStatuses", LicenseStatus.values());

    return new ModelAndView("shopadmin/sp-overview", model);
  }

  private List<ServiceBinding> getOrphans(List<ServiceBinding> serviceBindings) {
    Set<String> spEntitySet = serviceBindings.stream()
      .filter(serviceBinding -> serviceBinding.getCompoundServiceProvider() != null)
      .map(serviceBinding -> serviceBinding.getCompoundServiceProvider().getServiceProviderEntityId())
      .collect(toSet());

    return StreamSupport.stream(compoundServiceProviderDao.findAll().spliterator(), false)
      .filter(current -> !spEntitySet.contains(current.getServiceProviderEntityId()))
      .map(csp -> new ServiceBinding(csp.getServiceProvider(), csp))
      .collect(toList());
  }

  private List<ServiceBinding> getAllBindings() {
    return manage.getAllServiceProviders().stream()
      .map(serviceProvider -> new ServiceBinding(serviceProvider, compoundSPService.getCSPByServiceProvider(serviceProvider)))
      .collect(toList());
  }

  private String getBaseUrl(HttpServletRequest request) {
    try {
      URI myUri = new URI(request.getRequestURL().toString());

      StringBuilder builder = new StringBuilder();
      builder.append(myUri.getScheme() + "://" + myUri.getHost());
      if (myUri.getPort() > 0) {
        builder.append(":" + myUri.getPort());
      }

      return builder.toString();
    } catch (URISyntaxException e) {
      throw Throwables.propagate(e);
    }
  }

  @RequestMapping(value = "/save-normenkader-url", method = RequestMethod.POST)
  public ModelAndView saveNormenKaderUrl(HttpServletRequest req) {
    Map<String, Object> model = new HashMap<>();

    Long cspId = Long.parseLong(req.getParameter("cspId"));
    String normenKaderUrl = req.getParameter("normenkaderUrl");
    Integer index = Integer.valueOf(req.getParameter("index"));

    String isClearPressed = req.getParameter("clearbutton");
    if (StringUtils.isBlank(normenKaderUrl) || StringUtils.isNotBlank(isClearPressed)) {
      log.debug("Clearing normenKaderUrl for CompoundServiceProvider with ID {}", cspId);
      normenKaderUrl = null;
    } else if (!urlValidator.isValid(normenKaderUrl)) {
      model.put("errorNormenKaderMessage", "jsp.lmng_binding_overview.normenkader.url.error");
      model.put("messageNormenKaderIndex", index);
      return listAllSpsLmng(model);
    }
    CompoundServiceProvider csp = compoundServiceProviderDao.findOne(cspId);
    csp.setNormenkaderUrl(normenKaderUrl);
    compoundServiceProviderDao.save(csp);

    log.info("Updated CompoundServiceProvider({}) to have normenkader URL: {}", cspId, normenKaderUrl);

    return listAllSpsLmng(model);
  }

  @RequestMapping(value = "/update-normenkader-present/{cspId}/{newValue}", method = RequestMethod.PUT)
  @ResponseBody
  public String updateCspNormenKaderPresent(@PathVariable("cspId") Long cspId, @PathVariable("newValue") boolean newValue) {
    CompoundServiceProvider csp = compoundServiceProviderDao.findOne(cspId);
    csp.setNormenkaderPresent(newValue);
    compoundServiceProviderDao.save(csp);

    log.info("Updated CompoundServiceProvider({}) to normenkader present: {}", cspId, newValue);

    return "ok";
  }

  @RequestMapping(value = "/update-strong-authentication/{cspId}/{newValue}", method = RequestMethod.PUT)
  @ResponseBody
  public String updateStrongAuthentication(@PathVariable("cspId") Long cspId, @PathVariable("newValue") boolean newValue) {
    CompoundServiceProvider csp = compoundServiceProviderDao.findOne(cspId);
    csp.setStrongAuthentication(newValue);
    compoundServiceProviderDao.save(csp);

    log.info("Updated CompoundServiceProvider({}) to strong authentication: {}", cspId, newValue);

    return "ok";
  }

  @RequestMapping(value = "/update-license-status/{cspId}/{newValue}", method = RequestMethod.PUT)
  @ResponseBody
  public String updateCspLicenseStatus(@PathVariable("cspId") Long cspId, @PathVariable("newValue") String newValue) {
    CompoundServiceProvider csp = compoundServiceProviderDao.findOne(cspId);
    csp.setLicenseStatus(LicenseStatus.valueOf(newValue));
    compoundServiceProviderDao.save(csp);

    log.info("Updated CompoundServiceProvider({}) license status: {}", cspId, newValue);

    return "ok";
  }

  @RequestMapping(value = "/delete-csp.shtml", method = RequestMethod.POST)
  public void deleteCompoundServiceProvider(@RequestParam("cspId") String postedCspId, HttpServletResponse response) throws IOException {
    log.info("deleting compound service provider with ID {}", postedCspId);
    Long cspId = Long.parseLong(postedCspId);
    CompoundServiceProvider csp = compoundServiceProviderDao.findOne(cspId);
    compoundServiceProviderDao.delete(csp);

    // redirect to services page
    response.sendRedirect("all-spslmng.shtml");
  }
}
