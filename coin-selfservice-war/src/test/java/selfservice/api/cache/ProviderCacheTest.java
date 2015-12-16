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
import static com.jayway.awaitility.Duration.FIVE_SECONDS;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.jayway.awaitility.Duration;

import org.junit.Before;
import org.junit.Test;

import selfservice.api.cache.ProviderCache;
import selfservice.domain.csa.IdentityProvider;
import selfservice.service.IdentityProviderService;

public class ProviderCacheTest {

  public static final String IDP_ID = "http://mock-idp";
  private ProviderCache subject;
  private IdentityProviderService identityProviderService;

  @Before
  public void setUp() throws Exception {
    identityProviderService = mock(IdentityProviderService.class);
    subject = new ProviderCache(identityProviderService, 0, 1000, 0);
  }

  @Test
  public void testGetServiceProviderIdentifiers() throws Exception {
    List<String> sps = getSPs();

    when(identityProviderService.getLinkedServiceProviderIDs(IDP_ID)).thenReturn(sps);

    await().atMost(FIVE_SECONDS).until(() -> subject.getServiceProviderIdentifiers(IDP_ID).size(), is(1));
    sps.add("sp2");

    //now wait for the cache to be updated
    await().atMost(FIVE_SECONDS).until(() -> subject.getServiceProviderIdentifiers(IDP_ID).size(), is(2));
  }

  @Test
  public void tesGetIdentityProvider() {
    String idpEntityId = "unknown-idp";
    IdentityProvider identityProvider = subject.getIdentityProvider(idpEntityId);
    assertNull(identityProvider);

    when(identityProviderService.getIdentityProvider(idpEntityId)).thenReturn(new IdentityProvider(idpEntityId, "institution", "idp1"));
    identityProvider = subject.getIdentityProvider(idpEntityId);

    assertThat(identityProvider, notNullValue(IdentityProvider.class));
  }

  @Test
  public void testGetIdentityProvider() throws InterruptedException {
    IdentityProvider idp1 = new IdentityProvider("idp1", "institution", "idp1");
    IdentityProvider idp2 = new IdentityProvider("idp2", "institution", "idp2");
    IdentityProvider idp3 = new IdentityProvider("idp3", "institution", "idp3");

    List<IdentityProvider> listWithTwoIdps = Arrays.asList(idp1, idp2);
    List<IdentityProvider> listWithThreeIdps = Arrays.asList(idp1, idp2, idp3);

    when(identityProviderService.getAllIdentityProviders()).thenReturn(listWithTwoIdps);
    await().atMost(FIVE_SECONDS).until(() ->
        subject.getIdentityProvider("idp1") != null &&
          subject.getIdentityProvider("idp2") != null &&
          subject.getIdentityProvider("idp3") == null
    );

    when(identityProviderService.getAllIdentityProviders()).thenReturn(listWithThreeIdps);
    await().atMost(Duration.FIVE_SECONDS).until(() ->
        subject.getIdentityProvider("idp1") != null &&
          subject.getIdentityProvider("idp2") != null &&
          subject.getIdentityProvider("idp3") != null
    );
  }

  private List<String> getSPs() {
    List<String> sps = new ArrayList<>();
    sps.add("sp1");
    return sps;
  }
}
