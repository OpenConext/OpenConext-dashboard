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

package csa.service;

import static org.junit.Assert.assertEquals;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import csa.domain.IdentityProvider;
import csa.domain.ServiceProvider;
import csa.janus.Janus;
import csa.janus.domain.ARP;
import csa.janus.domain.EntityMetadata;
import csa.service.impl.ServiceRegistryProviderService;
import csa.util.JanusRestClientMock;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;

public class ServiceRegistryProviderServiceTest {

  @Mock
  private Janus janus;

  @Mock
  private ResourceLoader resourceLoader;

  @InjectMocks
  private ServiceRegistryProviderService serviceRegistryProviderService;

  @Before
  public void before() {
    serviceRegistryProviderService = new ServiceRegistryProviderService();
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getAllServiceProviders() {
    List<EntityMetadata> ems = new ArrayList<>();
    final EntityMetadata e = new EntityMetadata();
    e.setAppEntityId("entityid");
    e.setWorkflowState("prodaccepted");
    ems.add(e);
    when(janus.getMetadataByEntityId("entityid")).thenReturn(e);
    when(janus.getAllowedSps(anyString())).thenReturn(Arrays.asList("entityid"));
    when(janus.getSpList()).thenReturn(ems);
    final List<ServiceProvider> allServiceProviders = serviceRegistryProviderService.getAllServiceProviders("anyid");
    assertThat(allServiceProviders.get(0).getId(), is("entityid"));
  }

  @Test
  public void filteredList() {
    List<EntityMetadata> ems = new ArrayList<>();

    final EntityMetadata linkedEntity = new EntityMetadata();
    linkedEntity.setWorkflowState("prodaccepted");
    linkedEntity.setAppEntityId("linkedEntity-idpVisibleOnly");
    linkedEntity.setIdpVisibleOnly(true);
    ems.add(linkedEntity);

    final EntityMetadata linkedEntity2 = new EntityMetadata();
    linkedEntity2.setWorkflowState("prodaccepted");
    linkedEntity2.setAppEntityId("linkedEntity-not-idpVisibleOnly");
    linkedEntity2.setIdpVisibleOnly(false);
    ems.add(linkedEntity2);

    final EntityMetadata entity = new EntityMetadata();
    entity.setWorkflowState("prodaccepted");
    entity.setAppEntityId("entity-idpVisibleOnly");
    entity.setIdpVisibleOnly(true);
    ems.add(entity);


    final EntityMetadata entity2 = new EntityMetadata();
    entity2.setWorkflowState("prodaccepted");
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

  /**
   * Let getSpList() return a few sps, one of which is not 'in production'. The list returned by
   * getAllServiceProviders() should not include this sp.
   */
  @Test
  public void testFilterWorkflowstate() {
    when(janus.getSpList()).thenReturn(Arrays.asList(entityMetadata("prodaccepted","e1"),entityMetadata("not","e2")));
    final List<ServiceProvider> sps = serviceRegistryProviderService.getAllServiceProviders("myIdpId");
    assertEquals(1, sps.size());
  }
  
  @Test
  public void testNameAttributeRetrieval() {
    JanusRestClientMock janusMock = new JanusRestClientMock();
    EntityMetadata metadata = janusMock.getMetadataByEntityId("http://mock-sp");
    when(janus.getArp("http://mock-sp")).thenReturn(new ARP());
    ServiceProvider spFound = serviceRegistryProviderService.buildServiceProviderByMetadata(metadata, true);
    assertEquals("Populair SP (name en)",spFound.getName());
    
  }

  @Test
  public void testGetInstituteIdentityProviders(){
    final String institutionId = "foo";
    final EntityMetadata shouldBeFound = new EntityMetadata();
    shouldBeFound.setWorkflowState("prodaccepted");
    shouldBeFound.setAppEntityId("1");
    shouldBeFound.setInstutionId(institutionId);

    final EntityMetadata shouldNotBeFound = new EntityMetadata();
    shouldNotBeFound.setWorkflowState("prodaccepted");
    shouldNotBeFound.setAppEntityId("2");
    shouldNotBeFound.setInstutionId("bar");

    final EntityMetadata shouldNotBeFound2 = new EntityMetadata();
    shouldNotBeFound.setWorkflowState("prodaccepted");
    shouldNotBeFound.setAppEntityId("3");
    shouldNotBeFound.setInstutionId(null);


    when(janus.getIdpList()).thenReturn(Arrays.asList(shouldBeFound, shouldNotBeFound, shouldNotBeFound2));
    final List<IdentityProvider> instituteIdentityProviders = this.serviceRegistryProviderService.getInstituteIdentityProviders(institutionId);
    assertThat(instituteIdentityProviders.size(), is(1));
    assertThat("only the idps with the correct institution id must remain", instituteIdentityProviders.get(0).getInstitutionId(), is(institutionId));
  }

  @Test
  public void testReadingExampleSingleTenants() {
    String path = "dummy-single-tenants-services";
    serviceRegistryProviderService.setSingleTenantsConfigPath(path);
    when(resourceLoader.getResource(path)).thenReturn(new ClassPathResource(path));
    serviceRegistryProviderService.refreshExampleSingleTenants();

    when(janus.getSpList()).thenReturn(new ArrayList<>());
    List<ServiceProvider> serviceProviders = serviceRegistryProviderService.getAllServiceProviders(true);
    assertEquals(3, serviceProviders.size());
    assertTrue(serviceProviders.stream().allMatch(ServiceProvider::isExampleSingleTenant));
  }

  @Test
  public void testGetLinkedIdentityProvidersFromJanus() {
    String spId = "http://mock-sp";

    when(janus.getAllowedIdps(spId)).thenReturn(Arrays.asList("http://mock-idp"));
    when(janus.getIdpList()).thenReturn(Arrays.asList(entityMetadata("prodaccepted","http://mock-idp"), entityMetadata("prodaccepted","http://not-linked-idp")));

    List<IdentityProvider> idps = serviceRegistryProviderService.getLinkedIdentityProviders(spId);
    assertEquals(1, idps.size());
  }

  @Test
  public void testGetLinkedIdentityProvidersForSingleTenantService() {
    String path = "dummy-single-tenants-services";
    serviceRegistryProviderService.setSingleTenantsConfigPath(path);
    when(resourceLoader.getResource(path)).thenReturn(new ClassPathResource(path));
    serviceRegistryProviderService.refreshExampleSingleTenants();

    List<IdentityProvider> idps = serviceRegistryProviderService.getLinkedIdentityProviders("https://bod.dummy.sp");
    assertTrue(idps.isEmpty());
  }


  private EntityMetadata entityMetadata(String status, String appEntityId) {
    EntityMetadata e = new EntityMetadata();
    e.setWorkflowState(status);
    e.setAppEntityId(appEntityId);
    return e;
  }
}
