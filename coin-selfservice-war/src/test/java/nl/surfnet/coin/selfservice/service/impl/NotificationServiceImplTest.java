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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.surfnet.coin.selfservice.domain.Article;
import nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.License;
import nl.surfnet.coin.selfservice.domain.NotificationMessage;
import nl.surfnet.coin.selfservice.domain.Provider.Language;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class NotificationServiceImplTest {

  @InjectMocks
  private NotificationServiceImpl notificationServiceImpl;

  @Mock
  private CompoundSPService compoundSPService;

  private List<Authority> authorities;

  @Before
  public void setUp() throws Exception {
    notificationServiceImpl = new NotificationServiceImpl() {
      @Override
      protected List<Authority> getAuthorities() {
        return getParametrizedAuthorities();
      }

    };
    MockitoAnnotations.initMocks(this);
  }

  private List<Authority> getParametrizedAuthorities() {
    return authorities;
  };

  @Test
  public void testGetNotificationsWithMessagesLcp() {
    authorities = Arrays.asList(new Authority[] { Authority.ROLE_IDP_LICENSE_ADMIN });

    IdentityProvider idp = new IdentityProvider("idpId", "institutionid", "name");
    
    List<CompoundServiceProvider> services = new ArrayList<CompoundServiceProvider>();
    services.add(createCompoundServiceProvider("testSp1", true, true));
    services.add(createCompoundServiceProvider("testSp2", false, true));
    services.add(createCompoundServiceProvider("testSp3", true, false));
    services.add(createCompoundServiceProvider("testSp4", false, false));

    when(compoundSPService.getCSPsByIdp(idp)).thenReturn(services);

    List<NotificationMessage> result = notificationServiceImpl.getNotifications(idp);

    assertEquals(2, result.size());
    assertEquals("testSp2", result.get(0).getArguments());
    assertEquals("jsp.notifications.lcp.license.not.available", result.get(0).getMessageKey());
    assertEquals("testSp3", result.get(1).getArguments());
    assertEquals("jsp.notifications.lcp.service.not.linked", result.get(1).getMessageKey());

  }

  @Test
  public void testGetNotificationsWithMessagesFcp() {
    authorities = Arrays.asList(new Authority[] { Authority.ROLE_IDP_SURFCONEXT_ADMIN });

    IdentityProvider idp = new IdentityProvider("idpId", "institutionid", "name");
    
    List<CompoundServiceProvider> services = new ArrayList<CompoundServiceProvider>();
    services.add(createCompoundServiceProvider("testSp1", true, false));
    services.add(createCompoundServiceProvider("testSp2", false, true));
    services.add(createCompoundServiceProvider("testSp3", true, true));
    services.add(createCompoundServiceProvider("testSp4", false, false));

    when(compoundSPService.getCSPsByIdp(idp)).thenReturn(services);

    List<NotificationMessage> result = notificationServiceImpl.getNotifications(idp);

    assertEquals(2, result.size());
    assertEquals("testSp1", result.get(0).getArguments());
    assertEquals("jsp.notifications.fcp.service.not.linked", result.get(0).getMessageKey());
    assertEquals("testSp2", result.get(1).getArguments());
    assertEquals("jsp.notifications.fcp.license.not.available", result.get(1).getMessageKey());

  }

  @Test
  public void testGetNotificationsWithMessagesShopmanager() {
    authorities = Arrays.asList(new Authority[] { Authority.ROLE_DISTRIBUTION_CHANNEL_ADMIN });

    IdentityProvider idp = new IdentityProvider("idpId", "institutionid", "name");

    List<CompoundServiceProvider> services = new ArrayList<CompoundServiceProvider>();
    services.add(createCompoundServiceProvider("testSp1", false, true));
    services.add(createCompoundServiceProvider("testSp2", true, false));
    services.add(createCompoundServiceProvider("testSp3", true, true));
    services.add(createCompoundServiceProvider("testSp4", false, false));

    when(compoundSPService.getCSPsByIdp(idp)).thenReturn(services);

    List<NotificationMessage> result = notificationServiceImpl.getNotifications(idp);

    assertEquals(0, result.size());
  }

  @Test
  public void testGetNotificationsWithoutMessages() {
    authorities = Arrays.asList(new Authority[] { Authority.ROLE_IDP_LICENSE_ADMIN });

    IdentityProvider idp = new IdentityProvider("idpId", "institutionid", "name");

    List<CompoundServiceProvider> services = new ArrayList<CompoundServiceProvider>();
    services.add(createCompoundServiceProvider("testSp1", true, true));
    services.add(createCompoundServiceProvider("testSp2", true, true));
    services.add(createCompoundServiceProvider("testSp3", false, false));

    when(compoundSPService.getCSPsByIdp(idp)).thenReturn(services);

    List<NotificationMessage> result = notificationServiceImpl.getNotifications(idp);

    assertEquals(0, result.size());
  }

  private CompoundServiceProvider createCompoundServiceProvider(String spName, boolean hasLicense, boolean isLinked) {
    ServiceProvider sp = new ServiceProvider(spName);
    sp.addName(Language.NL.name().toLowerCase(), spName);

    Article article = new Article();
    article.setServiceProviderEntityId(spName);
    CompoundServiceProvider csp = CompoundServiceProvider.builder(sp, article);
    if (isLinked) {
      sp.setLinked(true);
    } else {
      sp.setLinked(false);
    }

    if (hasLicense) {
      csp.setLicenses(Arrays.asList(new License[] { new License() }));
    }

    return csp;
  }

}
