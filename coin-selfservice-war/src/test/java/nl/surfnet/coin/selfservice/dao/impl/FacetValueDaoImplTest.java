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
package nl.surfnet.coin.selfservice.dao.impl;

import nl.surfnet.coin.selfservice.dao.CompoundServiceProviderDao;
import nl.surfnet.coin.selfservice.dao.FacetDao;
import nl.surfnet.coin.selfservice.dao.FacetValueDao;
import nl.surfnet.coin.selfservice.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:coin-selfservice-context.xml",
        "classpath:coin-selfservice-properties-context.xml",
        "classpath:coin-shared-context.xml"})
@TransactionConfiguration(transactionManager = "selfServiceTransactionManager", defaultRollback = true)
@Transactional
public class FacetValueDaoImplTest {

  @Autowired
  private FacetValueDao facetValueDao;

  @Autowired
  private FacetDao facetDao;

  @Autowired
  private CompoundServiceProviderDao compoundServiceProviderDao;

  @Test
  public void testRetrieveFacetOnCompoundServicerProvider() {
    Facet facet = createFacetWithValue();
    CompoundServiceProvider csp = createCompoundServerProvider();

    csp.addFacetValue(facet.getFacetValues().first());
    compoundServiceProviderDao.saveOrUpdate(csp);

    csp = compoundServiceProviderDao.findById(csp.getId());
    assertEquals(facet.getName() ,csp.getFacetValues().first().getFacet().getName());
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
  public void testCreateFacet() {
    createFacetWithValue();

    List<FacetValue> facetValues = facetValueDao.findAll();
    assertEquals(2, facetValues.size()) ;

  }

  private Facet createFacetWithValue() {
    Facet facet = new Facet();
    facet.setName("category");

    FacetValue cloud = new FacetValue();
    cloud.setValue("cloud");
    facet.addFacetValue(cloud);

    FacetValue hosted = new FacetValue();
    hosted.setValue("hosted");
    facet.addFacetValue(hosted);

    facetDao.saveOrUpdate(facet);

    return facet;
  }

  private CompoundServiceProvider createCompoundServerProvider() {
    CompoundServiceProvider provider = CompoundServiceProvider.builder(new ServiceProvider("sp-id"), new Article());
    compoundServiceProviderDao.saveOrUpdate(provider);
    return provider;
  }


}
