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
package selfservice.api.cache;

import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.jayway.awaitility.Duration;

import selfservice.api.cache.CrmCache;
import selfservice.dao.LmngIdentifierDao;
import selfservice.domain.csa.Article;
import selfservice.domain.csa.IdentityProvider;
import selfservice.domain.csa.MappingEntry;
import selfservice.service.CrmService;
import selfservice.domain.License;
import selfservice.domain.Service;

public class CrmCacheTest {

  private CrmCache cache;
  private CrmService service;
  private LmngIdentifierDao dao;

  @Before
  public void before() throws Exception {
    service = mock(CrmService.class);
    dao = mock(LmngIdentifierDao.class);

    when(dao.findAllIdentityProviders()).thenReturn(getIdentityProviders());
    when(dao.findAllServiceProviders()).thenReturn(getServiceProviders());

    when(service.getLicensesForIdpAndSp(any(IdentityProvider.class), anyString())).thenReturn(ImmutableList.of(createLicense()));
    when(service.getArticlesForServiceProviders(anyListOf(String.class))).thenReturn(ImmutableList.of(createArticle()));

    cache = new CrmCache(service, dao, 0, 1000);
  }

  @Test
  public void testGetLicense() {
    Service service = new Service();
    service.setSpEntityId("spId-2");

    await().atMost(Duration.FIVE_SECONDS).until(() -> cache.getLicense(service, "idpId-2"), notNullValue());
  }

  @Test
  public void testGetArticle() {
    Service service = new Service();
    service.setSpEntityId("spId-2");
    await().atMost(Duration.FIVE_SECONDS).until(() -> cache.getArticle(service), notNullValue());
  }

  @Test
  public void noArticleFound() {
    when(service.getArticlesForServiceProviders(anyListOf(String.class))).thenReturn(Collections.<Article>emptyList());
    when(dao.findAllServiceProviders()).thenReturn(getProviders("spId"));
    cache.doPopulateCache();
    assertNull(cache.getArticle(new Service(1L, "name", "", "", true, "", "spId-0")));
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
