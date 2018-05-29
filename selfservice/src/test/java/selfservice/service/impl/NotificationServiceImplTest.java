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
import selfservice.domain.CoinAuthority.Authority;
import selfservice.domain.InstitutionIdentityProvider;
import selfservice.domain.NotificationMessage;
import selfservice.domain.Service;
import selfservice.service.Services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class NotificationServiceImplTest {

  @InjectMocks
  private NotificationServiceImpl notificationServiceImpl;

  @Mock
  private Services services;

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
  }

  @Test
  public void testGetNotificationsWithMessagesFcp() {
    authorities = Arrays.asList(new Authority[]{Authority.ROLE_DASHBOARD_ADMIN});

    InstitutionIdentityProvider idp = new InstitutionIdentityProvider("idpId", "name", "nameNl", "institutionid");

    List<Service> services = new ArrayList<>();
    services.add(createService("testSp1", true, false));
    services.add(createService("testSp2", false, true));
    services.add(createService("testSp3", true, true));
    services.add(createService("testSp4", false, false));

    when(this.services.getServicesForIdp(idp.getId())).thenReturn(services);

    NotificationMessage message = notificationServiceImpl.getNotifications(idp.getId());

    assertEquals(4, message.getArguments().size());

    assertEquals(NotificationServiceImpl.FCP_NOTIFICATIONS, message.getMessageKeys().get(0));
  }

  private Service createService(String spName, boolean hasLicense, boolean isConnected) {
    Service s = new Service(0L, spName, "", "", "");
    s.setConnected(isConnected);
    return s;
  }

}
