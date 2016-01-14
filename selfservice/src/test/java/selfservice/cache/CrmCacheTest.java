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
package selfservice.cache;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.common.collect.ImmutableList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import selfservice.dao.LmngIdentifierDao;
import selfservice.domain.IdentityProvider;
import selfservice.domain.License;
import selfservice.domain.Service;
import selfservice.domain.csa.Article;
import selfservice.domain.csa.MappingEntry;
import selfservice.service.CrmService;

@RunWith(MockitoJUnitRunner.class)
public class CrmCacheTest {

  private CrmCache cache;

  @Mock
  private CrmService crmServiceMock;

  @Mock
  private LmngIdentifierDao lmngIdentifierDaoMock;

  @Before
  public void before() throws Exception {
    cache = new CrmCache(crmServiceMock, lmngIdentifierDaoMock, 10000, 5000);
  }

  @Test
  public void testGetLicense() {
    Service service = new Service();
    service.setSpEntityId("spId-2");

    when(lmngIdentifierDaoMock.findAllIdentityProviders()).thenReturn(getIdentityProviders());
    when(lmngIdentifierDaoMock.findAllServiceProviders()).thenReturn(getServiceProviders());
    when(crmServiceMock.getLicensesForIdpAndSp(any(IdentityProvider.class), anyString())).thenReturn(ImmutableList.of(createLicense()));

    cache.doPopulateCache();

    assertThat(cache.getLicense(service, "idpId-2"), notNullValue());
  }

  @Test
  public void testGetArticle() {
    Service service = new Service();
    service.setSpEntityId("spId-2");

    when(lmngIdentifierDaoMock.findAllIdentityProviders()).thenReturn(getIdentityProviders());
    when(lmngIdentifierDaoMock.findAllServiceProviders()).thenReturn(getServiceProviders());
    when(crmServiceMock.getArticlesForServiceProviders(anyListOf(String.class))).thenReturn(ImmutableList.of(createArticle()));

    cache.doPopulateCache();

    assertThat(cache.getArticle(service), notNullValue());
  }

  @Test
  public void noArticleFound() {
    when(crmServiceMock.getArticlesForServiceProviders(anyListOf(String.class))).thenReturn(Collections.emptyList());
    when(lmngIdentifierDaoMock.findAllServiceProviders()).thenReturn(getProviders("spId"));

    cache.doPopulateCache();

    assertNull(cache.getArticle(new Service(1L, "name", "", "", true, "", "spId-0")));
  }

  @Test
  public void getAllLicensesForIdpEvenWhenFirstGivesAnEmptyList() {
    Service service1 = new Service(1L, "sp1", "logo", "website", true, "crmUrl", "sp1");
    Service service2 = new Service(2L, "sp2", "logo", "website", true, "crmUrl", "sp2");
    String idpId = "idp";
    IdentityProvider idp = new IdentityProvider(idpId, idpId, "dummy");
    License license = new License();

    when(lmngIdentifierDaoMock.findAllServiceProviders()).thenReturn(ImmutableList.of(new MappingEntry("sp1", "lmng-sp1"), new MappingEntry("sp2", "lmng-sp2")));
    when(lmngIdentifierDaoMock.findAllIdentityProviders()).thenReturn(ImmutableList.of(new MappingEntry("idp", "lmng")));

    when(crmServiceMock.getLicensesForIdpAndSp(idp, "lmng-sp1")).thenReturn(ImmutableList.of());
    when(crmServiceMock.getLicensesForIdpAndSp(idp, "lmng-sp2")).thenReturn(ImmutableList.of(license));

    cache.doPopulateCache();

    assertThat(cache.getLicense(service1, "idp"), nullValue());
    assertThat(cache.getLicense(service2, "idp"), is(license));
  }

  private License createLicense() {
    Date now = new Date();
    return new License(now, now, "licenseNumber", "institutionName");
  }

  private Article createArticle() {
    return new Article("lmngIdentifier");
  }

  private List<MappingEntry> getIdentityProviders() {
    return getProviders("idpId");
  }

  private List<MappingEntry> getServiceProviders() {
    return getProviders("spId");
  }

  private List<MappingEntry> getProviders(final String providerType) {
    List<MappingEntry> identityProviders = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      identityProviders.add(new MappingEntry(providerType + "-" + i, "lmngId-" + i));
    }
    return identityProviders;
  }

}
