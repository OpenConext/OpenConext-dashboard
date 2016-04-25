/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package selfservice.dao.impl;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableMap;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;

import selfservice.Application;
import selfservice.dao.CompoundServiceProviderDao;
import selfservice.dao.FacetDao;
import selfservice.dao.FacetValueDao;
import selfservice.dao.LocalizedStringDao;
import selfservice.domain.Facet;
import selfservice.domain.FacetValue;
import selfservice.domain.ServiceProvider;
import selfservice.domain.csa.Article;
import selfservice.domain.csa.CompoundServiceProvider;
import selfservice.domain.csa.InUseFacetValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Rollback
@Transactional
@ActiveProfiles("dev")
public class FacetValueDaoImplTestIntegration implements LocaleResolver {

  @Autowired
  private FacetValueDao facetValueDao;

  @Autowired
  private FacetDao facetDao;

  @Autowired
  private CompoundServiceProviderDao compoundServiceProviderDao;

  @Autowired
  private LocalizedStringDao localizedStringDao;

  private Locale currentLocale;

  @Test
  public void testRetrieveFacetOnCompoundServicerProvider() {
    Facet facet = createFacetWithValue();
    CompoundServiceProvider csp = createCompoundServerProvider();

    csp.addFacetValue(facet.getFacetValues().first());
    compoundServiceProviderDao.save(csp);

    csp = compoundServiceProviderDao.findOne(csp.getId());

    assertEquals(facet.getName(), csp.getFacetValues().first().getFacet().getName());
  }

  @Test
  public void testLinkingFacetValueAndCsp() {
    Facet facet = createFacetWithValue();
    CompoundServiceProvider csp = createCompoundServerProvider();

    FacetValue cloud = facet.getFacetValues().first();
    FacetValue hosted = facet.getFacetValues().last();
    Long cspId = csp.getId();
    Long cloudId = cloud.getId();

    /*
     * Link the two FacetValue's belonging to one Facet to the Csp
     */
    facetValueDao.linkCspToFacetValue(cspId, cloudId);
    facetValueDao.linkCspToFacetValue(cspId, hosted.getId());

    /*
     * Test the finding of the InUseFacetValue for one FacetValue
     */
    List<InUseFacetValue> inUseFacetValues = facetValueDao.findInUseFacetValues(cloudId);

    assertEquals(1, inUseFacetValues.size());

    InUseFacetValue inUseFacetValue = inUseFacetValues.get(0);
    assertEquals(cloud.getValue(), inUseFacetValue.getFacetValueValue());
    assertEquals(csp.getServiceProviderEntityId(), inUseFacetValue.getCompoundServiceProviderName());

    /*
     * Test the finding of the two InUseFacetValue for one Facet (containing two FacetValue's)
     */
    inUseFacetValues = facetValueDao.findInUseFacet(facet.getId());
    assertEquals(2, inUseFacetValues.size());

    inUseFacetValue = inUseFacetValues.get(0);
    assertEquals(cloud.getValue(), inUseFacetValue.getFacetValueValue());
    assertEquals(csp.getServiceProviderEntityId(), inUseFacetValue.getCompoundServiceProviderName());

    inUseFacetValue = inUseFacetValues.get(1);
    assertEquals(hosted.getValue(), inUseFacetValue.getFacetValueValue());
    assertEquals(csp.getServiceProviderEntityId(), inUseFacetValue.getCompoundServiceProviderName());

    /*
     * Test the unlinking of the Csp from the FacetValue
     */
    facetValueDao.unlinkCspFromFacetValue(cspId, cloudId);

    inUseFacetValues = facetValueDao.findInUseFacetValues(cloudId);
    assertEquals(0, inUseFacetValues.size());

    /*
     * But the Csp is still linked to the other FacetValue of the Facet
     */
    inUseFacetValues = facetValueDao.findInUseFacet(facet.getId());
    assertEquals(1, inUseFacetValues.size());

    /*
     * Now link the Csp again, so there are two links from the Csp to all
     * the FacetValue's, and test the unlinking (e.g. deletion) of
     * all Csp's (actually only one) from the FacetValue
     */
    facetValueDao.linkCspToFacetValue(cspId, cloudId);
    facetValueDao.unlinkAllCspFromFacetValue(cloudId);
    inUseFacetValues = facetValueDao.findInUseFacetValues(cloudId);
    assertEquals(0, inUseFacetValues.size());

    /*
     * Finally we unlink all FacetValue's belonging to a Facet from all Csp's. Effectively nothing
     * is linked anymore
     */
    facetValueDao.unlinkAllCspFromFacet(facet.getId());
    inUseFacetValues = facetValueDao.findInUseFacet(facet.getId());

    assertEquals(0, inUseFacetValues.size());
  }

  @Test
  public void testLocale() {
    Facet facet = createFacetWithValue();
    facet.addName(new Locale("nl"), "nederlandse_naam");

    facetDao.save(facet);
    // Set up the Locale in the Request (as Spring does)
    HttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE, this);
    ServletRequestAttributes sra = new ServletRequestAttributes(request);
    RequestContextHolder.setRequestAttributes(sra);
    this.setLocale(null, null, new Locale("nl"));

    assertEquals(facet.getName(), "nederlandse_naam");
  }

  @Test
  public void deleteOrphanLocalizedStrings() {
    Facet facet = Facet.builder().name("category").build();
    facetDao.save(facet);

    long localizedStringsCountBefore = localizedStringDao.count();

    facet.setName("the new name");
    facetDao.save(facet);

    long localizedStringsCountAfter = localizedStringDao.count();

    assertEquals("No more than existing nr of localized strings should be stored when updating existing, orphans should be deleted", localizedStringsCountBefore, localizedStringsCountAfter);
  }

  private Facet createFacetWithValue() {
    FacetValue cloud = FacetValue.builder().value("cloud").build();
    FacetValue hosted = FacetValue.builder().value("hosted").build();
    Facet facet = Facet.builder().name("category").addFacetValue(cloud).addFacetValue(hosted).build();

    facetDao.save(facet);

    return facet;
  }

  private CompoundServiceProvider createCompoundServerProvider() {
    CompoundServiceProvider provider = CompoundServiceProvider.builder(new ServiceProvider(ImmutableMap.of("entityid", "sp-id")), Optional.of(new Article()));
    provider = compoundServiceProviderDao.save(provider);

    return provider;
  }

  @Override
  public Locale resolveLocale(HttpServletRequest request) {
    return currentLocale;
  }

  @Override
  public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
    this.currentLocale = locale;
  }
}
