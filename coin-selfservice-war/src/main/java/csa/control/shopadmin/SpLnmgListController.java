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

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.StreamSupport;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import csa.command.LmngServiceBinding;
import csa.control.BaseController;
import csa.dao.CompoundServiceProviderDao;
import csa.dao.LmngIdentifierDao;
import csa.domain.CompoundServiceProvider;
import csa.model.License;
import csa.service.CrmService;
import csa.service.ExportService;
import csa.service.ServiceProviderService;
import csa.service.impl.CompoundSPService;
import csa.service.impl.LmngUtil;

@Controller
@RequestMapping(value = "/shopadmin/*")
public class SpLnmgListController extends BaseController {

  private static final Logger log = LoggerFactory.getLogger(SpLnmgListController.class);

  @Autowired
  private ServiceProviderService providerService;

  @Autowired
  private CrmService licensingService;

  @Autowired
  private LmngIdentifierDao lmngIdentifierDao;

  @Resource
  private CompoundSPService compoundSPService;

  @Autowired
  private CompoundServiceProviderDao compoundServiceProviderDao;

  @Autowired
  private ExportService exportService;

  private LmngUtil lmngUtil = new LmngUtil();

  private UrlValidator urlValidator = new UrlValidator(new String[]{"http", "https"});

  @RequestMapping(value = "/all-spslmng")
  public ModelAndView listAllSpsLmng(Map<String, Object> model) {
    List<LmngServiceBinding> lmngServiceBindings = getAllBindings();
    List<LmngServiceBinding> cspOrphans = getOrphans(lmngServiceBindings);

    model.put("bindings", lmngServiceBindings);
    model.put("orphans", cspOrphans);
    model.put("licenseStatuses", License.LicenseStatus.values());
    return new ModelAndView("shopadmin/sp-overview", model);
  }

  private List<LmngServiceBinding> getOrphans(List<LmngServiceBinding> lmngServiceBindings) {
    Set<String> spEntitySet = lmngServiceBindings.stream().
      filter(lmngServiceBinding -> lmngServiceBinding.getCompoundServiceProvider() != null).
      map(lmngServiceBinding -> lmngServiceBinding.getCompoundServiceProvider().getServiceProviderEntityId()).
      collect(toSet());

    Iterable<CompoundServiceProvider> csps = compoundServiceProviderDao.findAll();
    Iterator<CompoundServiceProvider> cspIter = csps.iterator();
    while (cspIter.hasNext()) {
      CompoundServiceProvider current = cspIter.next();
      if (spEntitySet.contains(current.getServiceProviderEntityId())) {
        cspIter.remove();
      }
    }

    return StreamSupport.stream(csps.spliterator(), false).
      map(csp -> new LmngServiceBinding(csp.getLmngId(), csp.getServiceProvider(), csp)).
      collect(toList());
  }

  private List<LmngServiceBinding> getAllBindings() {
    return providerService.getAllServiceProviders(false).stream().map(serviceProvider -> {
      String lmngIdentifier = lmngIdentifierDao.getLmngIdForServiceProviderId(serviceProvider.getId());
      CompoundServiceProvider compoundServiceProvider = compoundSPService.getCSPByServiceProvider(serviceProvider);
      return new LmngServiceBinding(lmngIdentifier, serviceProvider, compoundServiceProvider);
    }).collect(toList());
  }

  @RequestMapping(value = "/export.csv", produces = "text/csv")
  @ResponseBody
  public String exportToCSV(HttpServletRequest request, @RequestParam(value = "type", required = false) String type) {
    return getCsvContent(type, getBaseUrl(request));
  }

  private String getCsvContent(String type, String baseUrl) {
    List<LmngServiceBinding> lmngServiceBindings = getAllBindings();

    if (StringUtils.isEmpty(type)) {
      return exportService.exportServiceBindingsCsv(lmngServiceBindings, baseUrl);
    } else if (type.equalsIgnoreCase("orphans")) {
      List<LmngServiceBinding> cspOrphans = getOrphans(lmngServiceBindings);
      return exportService.exportServiceBindingsCsv(cspOrphans, baseUrl);
    } else {
      throw new IllegalArgumentException("Unknown type given: " + type);
    }
  }

  private String getBaseUrl(HttpServletRequest request) {
    try {
      StringBuilder builder = new StringBuilder();
      URI myUri = new URI(request.getRequestURL().toString());
      builder.append(myUri.getScheme() + "://" + myUri.getHost());
      if (myUri.getPort() > 0) {
        builder.append(":" + myUri.getPort());
      }

      return builder.toString();
    } catch (URISyntaxException e) {
      throw Throwables.propagate(e);
    }
  }

  @RequestMapping(value = "/save-splmng", method = RequestMethod.POST)
  public ModelAndView saveLmngServices(HttpServletRequest req) {
    Map<String, Object> model = new HashMap<>();

    String spId = req.getParameter("spIdentifier");
    String lmngId = req.getParameter("lmngIdentifier");
    Integer index = Integer.valueOf(req.getParameter("index"));

    String isClearPressed = req.getParameter("clearbutton");
    if (StringUtils.isBlank(lmngId) || StringUtils.isNotBlank(isClearPressed)) {
      log.debug("Clearing lmng identifier for ServiceProvider with ID {}", spId);
      lmngId = null;
    } else {
      // extra validation (also done in frontend/jquery)
      if (!lmngUtil.isValidGuid(lmngId)) {
        model.put("errorMessage", "jsp.lmng_binding_overview.wrong.guid");
        model.put("messageIndex", index);
        return listAllSpsLmng(model);
      }

      String serviceLmngName = licensingService.getServiceName(lmngId);
      if (serviceLmngName == null) {
        model.put("errorMessage", "jsp.lmng_binding_overview.unknown.guid");
        model.put("messageIndex", index);
      } else {
        model.put("infoMessage", serviceLmngName);
        model.put("messageIndex", index);
      }
      log.debug("Storing lmng identifier {} for ServiceProvider with ID {}", lmngId, spId);
    }
    lmngIdentifierDao.saveOrUpdateLmngIdForServiceProviderId(spId, lmngId);

    return listAllSpsLmng(model);
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

  @RequestMapping(value = "/update-enduser-visible/{cspId}/{newValue}", method = RequestMethod.PUT)
  @ResponseBody
  public String updateCspPublicApi(@PathVariable("cspId") Long cspId, @PathVariable("newValue") boolean newValue) {
    CompoundServiceProvider csp = compoundServiceProviderDao.findOne(cspId);
    csp.setAvailableForEndUser(newValue);
    compoundServiceProviderDao.save(csp);

    log.info("Updated CompoundServiceProvider({}) to be available for end users: {}", cspId, newValue);

    return "ok";
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

  @RequestMapping(value = "/update-license-status/{cspId}/{newValue}", method = RequestMethod.PUT)
  @ResponseBody
  public String updateCspLicenseStatus(@PathVariable("cspId") Long cspId, @PathVariable("newValue") String newValue) {
    CompoundServiceProvider csp = compoundServiceProviderDao.findOne(cspId);
    csp.setLicenseStatus(License.LicenseStatus.valueOf(newValue));
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
