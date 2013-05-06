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
  public void testCreateFacet() {
    createFacetWithValue();

    List<FacetValue> facetValues = facetValueDao.findAll();
    assertEquals(1, facetValues.size()) ;

  }

  private Facet createFacetWithValue() {
    Facet facet = new Facet();
    facet.setName("category");

    facetDao.saveOrUpdate(facet);

    FacetValue facetValue = new FacetValue();
    facetValue.setValue("cloud");

    facet.addFacetValue(facetValue);
    facetDao.saveOrUpdate(facet);
    return facet;
  }

  private CompoundServiceProvider createCompoundServerProvider() {
    CompoundServiceProvider provider = CompoundServiceProvider.builder(new ServiceProvider("sp-id"), new Article());
    compoundServiceProviderDao.saveOrUpdate(provider);
    return provider;
  }


}
