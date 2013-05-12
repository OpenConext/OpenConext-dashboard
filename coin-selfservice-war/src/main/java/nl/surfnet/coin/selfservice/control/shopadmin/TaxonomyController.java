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

import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.dao.FacetDao;
import nl.surfnet.coin.selfservice.dao.FacetValueDao;
import nl.surfnet.coin.selfservice.domain.*;
import nl.surfnet.coin.selfservice.service.impl.CompoundSPService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/shopadmin/*")
public class TaxonomyController extends BaseController {

  @Resource
  private FacetDao facetDao;

  @Resource
  private FacetValueDao facetValueDao;

  @Resource
  private CompoundSPService compoundSPService;

  @RequestMapping("taxonomy-overview.shtml")
  public String getAllFacets(ModelMap model) {
    model.addAttribute("facets", facetDao.findAll());
    return "shopadmin/taxonomy-overview";
  }

  @RequestMapping(value = "/facet/{facetId}", method = RequestMethod.PUT)
  public
  @ResponseBody
  String updateFacet(HttpServletRequest request, @RequestBody Facet facet, @PathVariable("facetId") Long facetId) {
    Facet prev = facetDao.findById(facetId);
    prev.setName(facet.getName());
    facetDao.saveOrUpdate(prev);
    return "ok";
  }

  @RequestMapping(value = "/facet", method = RequestMethod.POST)
  public
  @ResponseBody
  Long createFacet(@RequestBody Facet facet) {
    facetDao.saveOrUpdate(facet);
    return facet.getId();
  }

  @RequestMapping(value = "/facet/{facetId}", method = RequestMethod.DELETE)
  public
  @ResponseBody
  String deleteFacet(@PathVariable("facetId") Long facetId) {
    Facet prev = facetDao.findById(facetId);
    facetValueDao.unlinkAllCspFromFacet(facetId);
    facetDao.delete(prev);
    return "ok";
  }

  @RequestMapping(value = "/facet-value/{facetValueId}", method = RequestMethod.PUT)
  public
  @ResponseBody
  String updateFacetValue(@PathVariable("facetValueId") Long facetValueId, @RequestBody FacetValue facetValue) {
    FacetValue prev = facetValueDao.findById(facetValueId);
    prev.setValue(facetValue.getValue());
    facetValueDao.saveOrUpdate(prev);
    return "ok";
  }

  @RequestMapping(value = "{facetId}/facet-value", method = RequestMethod.POST)
  public
  @ResponseBody
  Long createFacetValue(@PathVariable("facetId") Long facetId, @RequestBody FacetValue facetValue) {
    Facet facet = facetDao.findById(facetId);
    facetValue.setFacet(facet);
    facetValueDao.saveOrUpdate(facetValue);
    return facetValue.getId();
  }

  @RequestMapping(value = "/facet-value/{facetValueId}", method = RequestMethod.DELETE)
  public
  @ResponseBody
  String deleteFacetValue(@PathVariable("facetValueId") Long facetValueId) {
    FacetValue prev = facetValueDao.findById(facetValueId);
    facetValueDao.unlinkAllCspFromFacetValue(facetValueId);
    facetValueDao.delete(prev);
    return "ok";
  }

  @RequestMapping(value = "/service-taxonomy-configuration", method = RequestMethod.GET)
  public String facetConfiguraton(@RequestParam("spEntityId") String entityId, ModelMap modelMap) {
    modelMap.addAttribute("facets", facetDao.findAll());
    CompoundServiceProvider compoundSp = compoundSPService.getCSPById(entityId);
    modelMap.addAttribute("compoundSp", compoundSp);
    return "shopadmin/service-taxonomy-configuration";
  }

  @RequestMapping(value = "/facet-value-csp/{facetValueId}/{compoundServiceProviderId}", method = RequestMethod.POST)
  public
  @ResponseBody
  String linkFacetValueCompoundServiceProvider(@PathVariable("facetValueId") Long facetValueId, @PathVariable("compoundServiceProviderId") Long compoundServiceProviderId, @ModelAttribute IsLinkRequest isLinkRequest) {
    if (isLinkRequest.getValue()) {
      facetValueDao.linkCspToFacetValue(compoundServiceProviderId, facetValueId);
    } else {
      facetValueDao.unlinkCspFromFacetValue(compoundServiceProviderId, facetValueId);
    }
    return "ok";
  }

  @RequestMapping(value = "/facet-value-used/{facetValueId}", method = RequestMethod.GET)
  public @ResponseBody
  List<InUseFacetValue> facetValueUsed(@PathVariable("facetValueId") Long facetValueId) {
    return facetValueDao.findInUseFacetValues(facetValueId);
  }

  @RequestMapping(value = "/facet-used/{facetId}", method = RequestMethod.GET)
  public @ResponseBody
  List<InUseFacetValue> facetUsed(@PathVariable("facetId") Long facetId) {
    return facetValueDao.findInUseFacet(facetId);
  }

}
