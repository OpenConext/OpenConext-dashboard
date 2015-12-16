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
package selfservice.api.csa;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.StreamSupport;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import selfservice.dao.FacetDao;
import selfservice.domain.Category;
import selfservice.domain.CategoryValue;
import selfservice.domain.Facet;
import selfservice.domain.Taxonomy;

@Controller
@RequestMapping
public class TaxonomyApiController extends BaseApiController {

  @Resource
  private FacetDao facetDao;

  @RequestMapping(method = RequestMethod.GET, value = "/api/public/taxonomy.json")
  @Cacheable(value = "csaApi")
  @ResponseBody
  public Taxonomy getTaxonomy(@RequestParam(value = "lang", defaultValue = "en") String language) {
    Iterable<Facet> facets = facetDao.findAll();
    List<Category> categories = StreamSupport.stream(facets.spliterator(), false).map(facet -> {
      Category category = new Category(facet.getName());
      List<CategoryValue> values = facet.getFacetValues().stream().map(fv -> new CategoryValue(fv.getValue())).collect(toList());
      category.setValues(values);
      return category;
    }).collect(toList());

    return new Taxonomy(categories);
  }
}
