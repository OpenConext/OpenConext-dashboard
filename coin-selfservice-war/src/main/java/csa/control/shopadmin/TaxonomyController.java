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

import java.util.List;
import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import csa.control.BaseController;
import csa.dao.FacetDao;
import csa.dao.LocalizedStringDao;
import csa.dao.MultilingualStringDao;
import csa.domain.CompoundServiceProvider;
import csa.domain.InUseFacetValue;
import csa.model.Facet;
import csa.model.MultilingualString;
import csa.service.impl.CompoundSPService;
import csa.dao.FacetValueDao;
import csa.domain.IsLinkRequest;
import csa.model.FacetValue;
import csa.model.LocalizedString;

@Controller
@RequestMapping(value = "/shopadmin/*")
public class TaxonomyController extends BaseController {

  @Resource
  private FacetDao facetDao;

  @Resource
  private FacetValueDao facetValueDao;

  @Resource
  private MultilingualStringDao multilingualStringDao;

  @Resource
  private LocalizedStringDao localizedStringDao;

  @Resource
  private CompoundSPService compoundSPService;

  @RequestMapping("taxonomy-overview.shtml")
  public String getAllFacets(ModelMap model) {
    model.addAttribute("facets", facetDao.findAll());
    return "shopadmin/taxonomy-overview";
  }

  @RequestMapping("taxonomy-translations.shtml")
  public String getFacetTranslations(ModelMap model) {
    model.addAttribute("facets", facetDao.findAll());
    return "shopadmin/taxonomy-translations";
  }

  @RequestMapping("taxonomy-services-overview.shtml")
  public String getServicesFacetsOverview(ModelMap model) {
    model.addAttribute("facets", facetDao.findAll());
    model.addAttribute("csps", compoundSPService.getAllBareCSPs());
    return "shopadmin/csp-taxonomy-overview";
  }

  @RequestMapping(value = "/facet/{facetId}", method = RequestMethod.PUT)
  @ResponseBody
  public String updateFacet(@RequestBody Facet facet, @PathVariable("facetId") Long facetId) {
    Facet prev = facetDao.findOne(facetId);
    prev.setName(facet.getName());
    facetDao.save(prev);
    return "ok";
  }

  @RequestMapping(value = "/facet", method = RequestMethod.POST)
  public
  @ResponseBody
  Long createFacet(@RequestBody Facet newFacet) {
    return facetDao.save(newFacet).getId();
  }

  @RequestMapping(value = "/facet/{facetId}", method = RequestMethod.DELETE)
  public
  @ResponseBody
  String deleteFacet(@PathVariable("facetId") Long facetId) {
    Facet prev = facetDao.findOne(facetId);
    facetValueDao.unlinkAllCspFromFacet(facetId);
    facetDao.delete(prev);
    return "ok";
  }

  @RequestMapping(value = "/facet-value/{facetValueId}", method = RequestMethod.PUT)
  public
  @ResponseBody
  String updateFacetValue(@PathVariable("facetValueId") Long facetValueId, @RequestBody FacetValue facetValue) {
    FacetValue prev = facetValueDao.findOne(facetValueId);
    prev.setValue(facetValue.getValue());
    facetValueDao.save(prev);
    return "ok";
  }

  @RequestMapping(value = "{facetId}/facet-value", method = RequestMethod.POST)
  public
  @ResponseBody
  Long createFacetValue(@PathVariable("facetId") Long facetId, @RequestBody FacetValue newFacetValue) {
    Facet facet = facetDao.findOne(facetId);
    newFacetValue.setFacet(facet);
    return facetValueDao.save(newFacetValue).getId();
  }

  @RequestMapping(value = "/facet-value/{facetValueId}", method = RequestMethod.DELETE)
  public
  @ResponseBody
  String deleteFacetValue(@PathVariable("facetValueId") Long facetValueId) {
    FacetValue prev = facetValueDao.findOne(facetValueId);
    facetValueDao.unlinkAllCspFromFacetValue(facetValueId);
    facetValueDao.delete(prev);
    return "ok";
  }

  @RequestMapping(value = "/service-taxonomy-configuration", method = RequestMethod.GET)
  public String facetConfiguraton(@RequestParam("spEntityId") String entityId, ModelMap modelMap) {
    modelMap.addAttribute("facets", facetDao.findAll());
    CompoundServiceProvider compoundSp = compoundSPService.getCSPByServiceProviderEntityId(entityId);
    modelMap.addAttribute("compoundSp", compoundSp);
    return "shopadmin/service-taxonomy-configuration";
  }

  @RequestMapping(value = "/facet-value-csp/{facetValueId}/{compoundServiceProviderId}", method = RequestMethod.POST)
  public
  @ResponseBody
  String linkFacetValueCompoundServiceProvider(@PathVariable("facetValueId") Long facetValueId,
                                               @PathVariable("compoundServiceProviderId") Long compoundServiceProviderId,
                                               @ModelAttribute IsLinkRequest isLinkRequest) {
    if (isLinkRequest.getValue()) {
      facetValueDao.linkCspToFacetValue(compoundServiceProviderId, facetValueId);
    } else {
      facetValueDao.unlinkCspFromFacetValue(compoundServiceProviderId, facetValueId);
    }
    return "ok";
  }

  @RequestMapping(value = "/facet-value-used/{facetValueId}", method = RequestMethod.GET)
  public
  @ResponseBody
  List<InUseFacetValue> facetValueUsed(@PathVariable("facetValueId") Long facetValueId) {
    return facetValueDao.findInUseFacetValues(facetValueId);
  }

  @RequestMapping(value = "/facet-used/{facetId}", method = RequestMethod.GET)
  public
  @ResponseBody
  List<InUseFacetValue> facetUsed(@PathVariable("facetId") Long facetId) {
    return facetValueDao.findInUseFacet(facetId);
  }


  @RequestMapping(value = "/taxonomy-translation/{multilingualStringId}", method = RequestMethod.POST)
  public
  @ResponseBody
  Long addFacetValueTranslation(@PathVariable("multilingualStringId") Long multilingualStringId, @RequestBody LocalizedString newLocalizedString) {
    MultilingualString multilingualString = multilingualStringDao.findOne(multilingualStringId);
    LocalizedString localizedString = new LocalizedString(newLocalizedString.getLocale(), newLocalizedString.getValue(), multilingualString);
    return localizedStringDao.save(localizedString).getId();
  }

  @RequestMapping(value = "/taxonomy-translation/{localizedStringId}", method = RequestMethod.PUT)
  public
  @ResponseBody
  String updateFacetValueTranslation(@PathVariable("localizedStringId") Long localizedStringId, @RequestBody LocalizedString update) {
    LocalizedString localizedString = localizedStringDao.findOne(localizedStringId);
    localizedString.setValue(update.getValue());
    localizedStringDao.save(localizedString);
    return "ok";
  }

}
