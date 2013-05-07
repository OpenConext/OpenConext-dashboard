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
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.Facet;
import nl.surfnet.coin.selfservice.domain.FacetValue;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.service.IdentityProviderService;
import nl.surfnet.coin.selfservice.service.impl.CompoundSPService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/shopadmin/*")
public class TaxonomyController extends BaseController {

  @Resource
  private FacetDao facetDao;

  @Resource
  private FacetValueDao facetValueDao;

  @RequestMapping("taxonomy-overview.shtml")
  public String getAllFacets(ModelMap model) {
    model.addAttribute("facets", facetDao.findAll());
    return "shopadmin/taxonomy-overview";
  }

  @RequestMapping(value = "/facet/{facetId}", method = RequestMethod.PUT)
  public
  @ResponseBody
  String updateFacet(@PathVariable("facetId") Long facetId, @ModelAttribute Facet facet) {
    Facet prev = facetDao.findById(facetId);
    prev.setName(facet.getName());
    facetDao.saveOrUpdate(prev);
    return "ok";
  }

  @RequestMapping(value = "/facet", method = RequestMethod.POST)
  public
  @ResponseBody
  Long createFacet(@ModelAttribute Facet facet) {
    facetDao.saveOrUpdate(facet);
    return facet.getId();
  }

  @RequestMapping(value = "/facet/{facetId}", method = RequestMethod.DELETE)
  public
  @ResponseBody
  String deleteFacet(@PathVariable("facetId") Long facetId) {
    Facet prev = facetDao.findById(facetId);
    facetDao.delete(prev);
    return "ok";
  }

  @RequestMapping(value = "/facet-value/{facetValueId}", method = RequestMethod.PUT)
  public
  @ResponseBody
  String updateFacetValue(@PathVariable("facetValueId") Long facetValueId, @ModelAttribute FacetValue facetValue) {
    FacetValue prev = facetValueDao.findById(facetValueId);
    prev.setValue(facetValue.getValue());
    facetValueDao.saveOrUpdate(prev);
    return "ok";
  }

  @RequestMapping(value = "{facetId}/facet-value", method = RequestMethod.POST)
  public
  @ResponseBody
  Long createFacetValue(@PathVariable("facetId") Long facetId, @ModelAttribute FacetValue facetValue) {
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
    facetValueDao.saveOrUpdate(prev);
    return "ok";
  }

}
