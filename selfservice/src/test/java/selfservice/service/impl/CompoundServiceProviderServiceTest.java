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

package selfservice.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import selfservice.dao.CompoundServiceProviderDao;
import selfservice.dao.LmngIdentifierDao;
import selfservice.domain.IdentityProvider;
import selfservice.domain.ServiceProvider;
import selfservice.domain.csa.Article;
import selfservice.domain.csa.CompoundServiceProvider;
import selfservice.service.CrmService;
import selfservice.serviceregistry.ServiceRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class CompoundServiceProviderServiceTest {

  @InjectMocks
  private CompoundServiceProviderService cspService;

  @Mock
  private ServiceRegistry serviceRegistry;

  @Mock
  private CrmService licensingService;

  @Mock
  private CompoundServiceProviderDao compoundServiceProviderDao;

  @Mock
  private LmngIdentifierDao lmngIdentifierDao;

  @Before
  public void setUp() throws Exception {
    cspService = new CompoundServiceProviderService();
    MockitoAnnotations.initMocks(this);
  }

  /**
   * Get a list of all CSPs for a given IDP. This test checks that for SPs in
   * the ServiceProviderService that have no corresponding CSP yet, one will be
   * created.
   */
  @Test
  public void testGetCompoundServiceProvidersByIdp() throws Exception {

    // This is the IDP for whom we want all csps.
    IdentityProvider idp = new IdentityProvider("idpId", "institutionid", "name");

    // Two SPs exist
    List<ServiceProvider> sps = new ArrayList<ServiceProvider>();
    ServiceProvider sp1 = new ServiceProvider(ImmutableMap.of("entityid", "spId1"));
    ServiceProvider sp2 = new ServiceProvider(ImmutableMap.of("entityid", "spId2"));
    sps.add(sp1);
    sps.add(sp2);

    // There is one existing CSP, belonging to sp1.
    List<CompoundServiceProvider> csps = new ArrayList<CompoundServiceProvider>();
    CompoundServiceProvider csp1 = CompoundServiceProvider.builder(sp1, Optional.of(new Article()));
    csps.add(csp1);

    when(serviceRegistry.getServiceProvider("spId1")).thenReturn(Optional.of(sp1));
    when(serviceRegistry.getServiceProvider("spId2")).thenReturn(Optional.of(sp2));
    when(compoundServiceProviderDao.findAll()).thenReturn(csps);
    when(serviceRegistry.getAllServiceProviders("idpId")).thenReturn(sps);

    cspService.getCompoundServiceProvidersByIdp(idp);

    // Check that the 'missing' CSP is saved
    verify(compoundServiceProviderDao, times(1)).save(any(CompoundServiceProvider.class));
  }

  @Test
  public void testGetCSPById() throws Exception {
    List<ServiceProvider> sps = new ArrayList<>();
    ServiceProvider sp1 = new ServiceProvider(ImmutableMap.of("entityid", "spId1"));
    ServiceProvider sp2 = new ServiceProvider(ImmutableMap.of("entityid", "spId2"));
    sps.add(sp1);
    sps.add(sp2);
    when(serviceRegistry.getAllServiceProviders("idpid")).thenReturn(sps);
    when(serviceRegistry.getServiceProvider("spId1")).thenReturn(Optional.of(sp1));

    CompoundServiceProvider csp = CompoundServiceProvider.builder(sp1, Optional.of(new Article()));
    when(compoundServiceProviderDao.findOne(1L)).thenReturn(csp);
    IdentityProvider idp = new IdentityProvider("idpid", "instid", "thename");
    CompoundServiceProvider gottenCSP = cspService.getCSPById(idp, 1L);

    assertTrue(csp == gottenCSP);
    assertTrue(sp1 == gottenCSP.getServiceProvider());
  }

}
