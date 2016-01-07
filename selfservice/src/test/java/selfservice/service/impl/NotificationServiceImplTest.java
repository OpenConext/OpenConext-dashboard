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

import selfservice.domain.CoinAuthority.Authority;
import selfservice.domain.InstitutionIdentityProvider;
import selfservice.domain.License;
import selfservice.domain.NotificationMessage;
import selfservice.domain.Service;
import selfservice.service.Csa;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class NotificationServiceImplTest {

  @InjectMocks
  private NotificationServiceImpl notificationServiceImpl;

  @Mock
  private Csa csa;

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

  ;

  @Test
  public void testGetNotificationsWithMessagesFcp() {
    authorities = Arrays.asList(new Authority[]{Authority.ROLE_DASHBOARD_ADMIN});

    InstitutionIdentityProvider idp = new InstitutionIdentityProvider("idpId", "institutionid", "name");

    List<Service> services = new ArrayList<>();
    services.add(createService("testSp1", true, false));
    services.add(createService("testSp2", false, true));
    services.add(createService("testSp3", true, true));
    services.add(createService("testSp4", false, false));

    when(csa.getServicesForIdp(idp.getId())).thenReturn(services);

    NotificationMessage message = notificationServiceImpl.getNotifications(idp.getId());

    assertEquals(2, message.getArguments().size());
    assertEquals(services.get(0), message.getArguments().get(0));
    assertEquals(services.get(1), message.getArguments().get(1));

    assertEquals(NotificationServiceImpl.FCP_NOTIFICATIONS, message.getMessageKeys().get(0));
  }

  private Service createService(String spName, boolean hasLicense, boolean isConnected) {
    Service s = new Service(0L, spName, "", "", true, "", "");
    if (hasLicense) {
      s.setLicense(new License(new Date(), new Date(), "", ""));
    }
    s.setConnected(isConnected);
    return s;
  }

}
