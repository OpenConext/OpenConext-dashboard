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

package nl.surfnet.coin.selfservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import nl.surfnet.coin.janus.Janus;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.impl.ServiceRegistryProviderService;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;

public class ServiceRegistryProviderServiceTest {

  @Mock
  private Janus janus;

  @InjectMocks
  private ServiceRegistryProviderService serviceRegistryProviderService;

  @Before
  public void before() {
    serviceRegistryProviderService = new ServiceRegistryProviderService();
    MockitoAnnotations.initMocks(this);
  }
  @Test
  @Ignore("janus mock doesn't work")
  public void getAllServiceProviders() {
    final Map<String, Map<String, String>> data = new HashMap<String, Map<String, String>>();
    data.put("entityid", new HashMap<String, String>());
    data.get("entityid").put("key", "value");
    when(janus.getSpList((Janus.Metadata[]) anyObject())).thenReturn(data);

    final List<ServiceProvider> allServiceProviders = serviceRegistryProviderService.getAllServiceProviders("anyid");
    System.out.println(allServiceProviders);
    assertThat(allServiceProviders.get(0).getId(), is("entityid"));
  }
}
