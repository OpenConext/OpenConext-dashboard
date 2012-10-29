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
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import nl.surfnet.coin.selfservice.domain.Article;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.LicensingService;

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
    "classpath:coin-selfservice-properties-integration-context.xml",
    "classpath:coin-shared-context.xml"})
public class LmngServiceImplIT {

  @Autowired
  private LicensingService licensingService;

  @Before
  public void init() throws FileNotFoundException, IOException {
  }

  // we us this for a local integration test only
  @Test
  
  public void testRetrieveLmngSingleGoogle() throws IOException {
    Date date = new Date();
    IdentityProvider identityProvider = new IdentityProvider("mock-institution-id", "mock-institution-id", "testName");
    ServiceProvider serviceProvider = new ServiceProvider("http://www.google.com");
    Article article = licensingService.getArticleForIdentityProviderAndServiceProvider(identityProvider, serviceProvider, date);

    assertNotNull(article);
 // currently no IDP in result
//    assertEquals("Incorrect name for IDP", "Open Universiteit Nederland", article.getInstitutionName());
    assertEquals("Incorrect name for product", "Google Apps Education Edition", article.getProductName());
  }

  // we us this for a local integration test only
  @Test
  public void testRetrieveLmngGoogleEdugroepGreencloudSurfMarket() throws IOException {
    Date date = new Date();
    IdentityProvider idp = new IdentityProvider("SURFmarket", "SURFmarket", "testName");
    
    List<ServiceProvider> sps = new ArrayList<ServiceProvider>();
    sps.add(new ServiceProvider("http://www.google.com"));
    sps.add(new ServiceProvider("Greencloud"));
    sps.add(new ServiceProvider("EDUgroepen"));
    
    List<Article> articles = licensingService.getArticleForIdentityProviderAndServiceProviders(idp, sps, date);

    assertNotNull(articles);
    assertEquals("Incorrect number of results", 4, articles.size());
    
    assertEquals("Incorrect name for product", "Greencloud", articles.get(0).getProductName());
// currently no IDP in result
//    assertEquals("Incorrect name for IDP", "SURFmarket", articles.get(0).getInstitutionName());
    assertNotNull("expected license",articles.get(0).getLicence());
    assertTrue("Expected group license",articles.get(0).getLicence().isGroupLicense());

    assertEquals("Incorrect name for product", "Google Apps Education Edition", articles.get(1).getProductName());
    assertNull("Expected no license", articles.get(1).getLicence());
    
    assertEquals("Incorrect name for product", "Google Drive", articles.get(2).getProductName());
    assertNull("Expected no license", articles.get(2).getLicence());
    
    assertEquals("Incorrect name for product", "EDUgroepen", articles.get(3).getProductName());
    assertNotNull("expected license",articles.get(3).getLicence());
    assertTrue("Expected group license",articles.get(3).getLicence().isGroupLicense());
    
    assertNotNull("expected licencenumber", articles.get(0).getLicenseNumber());
  }

  // we us this for a local integration test only
  @Test
  
  public void testRetrieveLmngGoogleEdugroepGreencloudSurfNet() throws IOException {
    Date date = new Date();
    IdentityProvider idp = new IdentityProvider("SURFnet", "SURFnet", "testName");
    
    List<ServiceProvider> sps = new ArrayList<ServiceProvider>();
    sps.add(new ServiceProvider("http://www.google.com"));
    sps.add(new ServiceProvider("Greencloud"));
    sps.add(new ServiceProvider("EDUgroepen"));
    
    List<Article> articles = licensingService.getArticleForIdentityProviderAndServiceProviders(idp, sps, date);

    assertNotNull(articles);
    assertEquals("Incorrect number of results", 4, articles.size());
 // currently no IDP in result
//    assertEquals("Incorrect name for IDP", "SURFnet bv", articles.get(0).getInstitutionName());
    assertEquals("Incorrect name for product", "Greencloud", articles.get(0).getProductName());
    assertEquals("Incorrect name for product", "Google Apps Education Edition", articles.get(1).getProductName());
    assertEquals("Incorrect name for product", "Google Drive", articles.get(2).getProductName());
    assertEquals("Incorrect name for product", "EDUgroepen", articles.get(3).getProductName());
  }

  // we us this for a local integration test only
  @Test
  @Ignore
  public void testRetrieveLmngGoogleServiceOnly() throws IOException {
    ServiceProvider sp = new ServiceProvider("http://www.google.com");
    
    Article article = licensingService.getArticleForServiceProvider(sp);

    assertNotNull("Expected Article result", article);
    assertNull("Expected no institution name", article.getInstitutionName());
    assertEquals("Incorrect name for product", "Google Drive", article.getProductName());
    assertEquals("Incorrect article id", "{8833CEAE-960C-E211-B6B9-005056950050}", article.getLmngIdentifier());
  }

  
}
