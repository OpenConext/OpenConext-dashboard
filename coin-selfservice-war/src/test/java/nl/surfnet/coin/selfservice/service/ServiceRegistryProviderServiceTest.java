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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import nl.surfnet.coin.janus.Janus;
import nl.surfnet.coin.janus.domain.EntityMetadata;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.impl.ServiceRegistryProviderService;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
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
  public void getAllServiceProviders() {
    List<EntityMetadata> ems = new ArrayList<EntityMetadata>();
    final EntityMetadata e = new EntityMetadata();
    e.setAppEntityId("entityid");
    ems.add(e);
    when(janus.getMetadataByEntityId("entityid")).thenReturn(e);
    when(janus.getAllowedSps(anyString())).thenReturn(Arrays.asList("entityid"));
    when(janus.getSpList()).thenReturn(ems);
    final List<ServiceProvider> allServiceProviders = serviceRegistryProviderService.getAllServiceProviders("anyid");
    assertThat(allServiceProviders.get(0).getId(), is("entityid"));
  }

  @Test
  public void filteredList() {
    List<EntityMetadata> ems = new ArrayList<EntityMetadata>();

    final EntityMetadata linkedEntity = new EntityMetadata();
    linkedEntity.setAppEntityId("linkedEntity-idpVisibleOnly");
    linkedEntity.setIdpVisibleOnly(true);
    ems.add(linkedEntity);

    final EntityMetadata linkedEntity2 = new EntityMetadata();
    linkedEntity2.setAppEntityId("linkedEntity-not-idpVisibleOnly");
    linkedEntity2.setIdpVisibleOnly(false);
    ems.add(linkedEntity2);

    final EntityMetadata entity = new EntityMetadata();
    entity.setAppEntityId("entity-idpVisibleOnly");
    entity.setIdpVisibleOnly(true);
    ems.add(entity);


    final EntityMetadata entity2 = new EntityMetadata();
    entity2.setAppEntityId("entity-not-idpVisibleOnly");
    entity2.setIdpVisibleOnly(false);
    ems.add(entity2);

    when(janus.getMetadataByEntityId("linkedEntity-idpVisibleOnly")).thenReturn(linkedEntity);
    when(janus.getMetadataByEntityId("linkedEntity-not-idpVisibleOnly")).thenReturn(linkedEntity2);
    when(janus.getMetadataByEntityId("entity-idpVisibleOnly")).thenReturn(entity);
    when(janus.getMetadataByEntityId("entity-not-idpVisibleOnly")).thenReturn(entity2);

    when(janus.getAllowedSps(anyString())).thenReturn(
        Arrays.asList("linkedEntity-idpVisibleOnly", "linkedEntity-not-idpVisibleOnly"));

    when(janus.getSpList()).thenReturn(ems);

    final List<ServiceProvider> filteredList = serviceRegistryProviderService.getAllServiceProviders("myIdpId");
    assertThat(filteredList.size(), is(3));
  }
}
