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
package nl.surfnet.coin.selfservice.service.impl;

import static junit.framework.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.License;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * LmngServiceImplIT.java
 * 
 * @TODO move to the integration tests
 *       (/conext-integration-tests/src/test/java/nl/surfnet/conext/test/)
 * 
 * NOTE! we us this for a local integration test only
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:coin-selfservice-context.xml",
    "classpath:coin-selfservice-properties-context.xml",
    "classpath:coin-shared-context.xml"})
public class LmngServiceImplIT {

  @Autowired
  private LmngServiceImpl lmngServiceImpl;

  @Before
  public void init() throws FileNotFoundException, IOException {
  }

  // we us this for a local integration test only
  @Test
  @Ignore
  public void testRetrieveLmngSingleGoogle() throws IOException {
    Date date = new Date();
    IdentityProvider identityProvider = new IdentityProvider("mock-institution-id", "mock-institution-id", "testName");
    ServiceProvider serviceProvider = new ServiceProvider("http://www.google.com");
    List<License> licenses = lmngServiceImpl.getLicensesForIdentityProviderAndServiceProvider(identityProvider, serviceProvider, date);

    assertEquals("Incorrect number of results", 1, licenses.size());
    assertEquals("Incorrect name for IDP", "Open Universiteit Nederland", licenses.get(0).getIdentityName());
    assertEquals("Incorrect name for product", "Google Apps Education Edition", licenses.get(0).getProductName());
  }

  // we us this for a local integration test only
  @Test
  @Ignore
  public void testRetrieveLmngGoogleEdugroepGreencloudSurfMarket() throws IOException {
    Date date = new Date();
    IdentityProvider surfMarket = new IdentityProvider("SURFmarket", "SURFmarket", "testName");
    
    List<ServiceProvider> sps = new ArrayList<ServiceProvider>();
    sps.add(new ServiceProvider("http://www.google.com"));
    sps.add(new ServiceProvider("Greencloud"));
    sps.add(new ServiceProvider("EDUgroepen"));
    
    List<License> licenses = lmngServiceImpl.getLicensesForIdentityProviderAndServiceProviders(surfMarket, sps, date);

    assertEquals("Incorrect number of results", 2, licenses.size());
    assertEquals("Incorrect name for IDP", "SURFmarket", licenses.get(0).getIdentityName());
    assertEquals("Incorrect name for product", "EDUgroepen", licenses.get(0).getProductName());
    assertEquals("Incorrect name for product", "Greencloud", licenses.get(1).getProductName());
  }

  // we us this for a local integration test only
  @Test
  @Ignore
  public void testRetrieveLmngGoogleEdugroepGreencloudSurfNet() throws IOException {
    Date date = new Date();
    IdentityProvider surfMarket = new IdentityProvider("SURFnet", "SURFnet", "testName");
    
    List<ServiceProvider> sps = new ArrayList<ServiceProvider>();
    sps.add(new ServiceProvider("http://www.google.com"));
    sps.add(new ServiceProvider("Greencloud"));
    sps.add(new ServiceProvider("EDUgroepen"));
    
    List<License> licenses = lmngServiceImpl.getLicensesForIdentityProviderAndServiceProviders(surfMarket, sps, date);

    assertEquals("Incorrect number of results", 2, licenses.size());
    assertEquals("Incorrect name for IDP", "SURFnet bv", licenses.get(0).getIdentityName());
    assertEquals("Incorrect name for product", "Google Apps Education Edition", licenses.get(0).getProductName());
    assertEquals("Incorrect name for product", "EDUgroepen", licenses.get(1).getProductName());
  }

  
}
