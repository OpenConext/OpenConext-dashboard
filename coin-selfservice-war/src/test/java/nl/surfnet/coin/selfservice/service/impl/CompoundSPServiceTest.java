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

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import nl.surfnet.coin.selfservice.dao.CompoundServiceProviderDao;
import nl.surfnet.coin.selfservice.dao.LmngIdentifierDao;
import nl.surfnet.coin.selfservice.domain.Article;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.LmngService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CompoundSPServiceTest {

  @InjectMocks
  private CompoundSPService cspService;

  @Mock
  private ServiceProviderService serviceProviderService;

  @Mock
  private LmngService licensingService;

  @Mock
  private CompoundServiceProviderDao compoundServiceProviderDao;

  @Mock
  private LmngIdentifierDao lmngIdentifierDao;

  private DateTime now = new DateTime();

  @Before
  public void setUp() throws Exception {
    cspService = new CompoundSPService() {
      @Override
      protected DateTime getNow() {
        return getParametrizedNow();
      }
    };
    MockitoAnnotations.initMocks(this);
  }

  private DateTime getParametrizedNow() {
    return now;
  }

  /**
   * 
   * Get a list of all CSPs for a given IDP. This test checks that for SPs in
   * the ServiceProviderService that have no corresponding CSP yet, one will be
   * created.
   */
  @Test
  public void testGetCSPsByIdp() throws Exception {

    // This is the IDP for whom we want all csps.
    IdentityProvider idp = new IdentityProvider("idpId", "institutionid", "name");

    // Two SPs exist
    List<ServiceProvider> sps = new ArrayList<ServiceProvider>();
    ServiceProvider sp1 = new ServiceProvider("spId1");
    ServiceProvider sp2 = new ServiceProvider("spId2");
    sps.add(sp1);
    sps.add(sp2);

    // There is one existing CSP, belonging to sp1.
    List<CompoundServiceProvider> csps = new ArrayList<CompoundServiceProvider>();
    CompoundServiceProvider csp1 = CompoundServiceProvider.builder(sp1, new Article());
    csps.add(csp1);

    when(serviceProviderService.getServiceProvider("spId1")).thenReturn(sp1);
    when(serviceProviderService.getServiceProvider("spId2")).thenReturn(sp2);
    when(compoundServiceProviderDao.findAll()).thenReturn(csps);
    when(serviceProviderService.getAllServiceProviders("idpId")).thenReturn(sps);

    cspService.getCSPsByIdp(idp);

    // Check that the 'missing' CSP is saved
    verify(compoundServiceProviderDao, times(1)).saveOrUpdate(any(CompoundServiceProvider.class));
  }

  @Test
  public void testGetCSPById() throws Exception {
    List<ServiceProvider> sps = new ArrayList<ServiceProvider>();
    ServiceProvider sp1 = new ServiceProvider("spId1");
    ServiceProvider sp2 = new ServiceProvider("spId2");
    sps.add(sp1);
    sps.add(sp2);
    when(serviceProviderService.getAllServiceProviders("idpid")).thenReturn(sps);
    when(serviceProviderService.getServiceProvider("spId1")).thenReturn(sp1);

    CompoundServiceProvider csp = CompoundServiceProvider.builder(sp1, new Article());
    when(compoundServiceProviderDao.findById(1L)).thenReturn(csp);
    IdentityProvider idp = new IdentityProvider("idpid", "instid", "thename");
    CompoundServiceProvider gottenCSP = cspService.getCSPById(idp, 1L, false);
    assertTrue(csp == gottenCSP);
    assertTrue(sp1 == gottenCSP.getServiceProvider());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testLmngCacheExpire() throws Exception {
    //Set the cache to 1 minute
    cspService.setLmngArticleCacheExpireSeconds(60);
    cspService.setLmngLicenseCacheExpireSeconds(20);

    // This is the IDP for whom we want all csps.
    IdentityProvider idp = new IdentityProvider("idpId", "institutionid", "name");

    // Two SPs exist
    List<ServiceProvider> sps = new ArrayList<ServiceProvider>();
    ServiceProvider sp1 = new ServiceProvider("spId1");
    ServiceProvider sp2 = new ServiceProvider("spId2");
    sps.add(sp1);
    sps.add(sp2);

    List<Article> articles = new ArrayList<Article>();
    Article article1 = new Article();
    article1.setServiceProviderEntityId(sp1.getId());
    article1.setLmngIdentifier("article1");
    articles.add(article1);
    Article article2 = new Article();
    article2.setServiceProviderEntityId(sp2.getId());
    article2.setLmngIdentifier("article2");
    articles.add(article2);

    List<CompoundServiceProvider> csps = new ArrayList<CompoundServiceProvider>();
    CompoundServiceProvider csp1 = CompoundServiceProvider.builder(sp1, article1);
    CompoundServiceProvider csp2 = CompoundServiceProvider.builder(sp2, article2);
    csps.add(csp1);
    csps.add(csp2);

    when(licensingService.isActiveMode()).thenReturn(true);
    when(licensingService.getArticlesForServiceProviders(any(List.class))).thenReturn(articles);
    when(serviceProviderService.getServiceProvider("spId2")).thenReturn(sp2);
    when(compoundServiceProviderDao.findAll()).thenReturn(csps);
    when(serviceProviderService.getAllServiceProviders()).thenReturn(sps);
    when(serviceProviderService.getAllServiceProviders("idpId")).thenReturn(sps);

    now = new DateTime();
    cspService.getCSPsByIdp(idp);

    // licensingService called once for all articles together articles, after that everything came from cache
    // licensingService called twice for licenses (once per article),
    verify(licensingService, times(1)).getArticlesForServiceProviders(any(List.class));
    verify(licensingService, times(2)).getLicensesForIdpAndSp(eq(idp),any(String.class),any(Date.class));
    
    // add half 10 seconds, articles and licenses should still come from cache
    now = now.plusSeconds(10);
    cspService.getCSPsByIdp(idp);
    // licensingservice should still be called once for articles and twice for licenses
    verify(licensingService, times(1)).getArticlesForServiceProviders(any(List.class));  
    verify(licensingService, times(2)).getLicensesForIdpAndSp(eq(idp),any(String.class),any(Date.class));

    // add half another 20 seconds, articles should still come from cache, licenses are expired
    now = now.plusSeconds(20);
    cspService.getCSPsByIdp(idp);
    // licensingservice should still be called once for articles and twice for licenses
    verify(licensingService, times(1)).getArticlesForServiceProviders(any(List.class));  
    verify(licensingService, times(4)).getLicensesForIdpAndSp(eq(idp),any(String.class),any(Date.class));

    // add another minute, now cache should be invalidated
    now = now.plusMinutes(1);
    cspService.getCSPsByIdp(idp);
    // licensingservice should be called one extra time for both articles and licenses
    verify(licensingService, times(2)).getArticlesForServiceProviders(any(List.class));
    verify(licensingService, times(6)).getLicensesForIdpAndSp(eq(idp),any(String.class),any(Date.class));

}
}