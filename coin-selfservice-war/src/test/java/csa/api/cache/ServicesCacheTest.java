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
package csa.api.cache;

import csa.service.ServicesService;
import csa.model.Service;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.jayway.awaitility.Duration;

public class ServicesCacheTest {

  private ServicesCache subject;
  private ServicesService servicesService;

  @Before
  public void setUp() throws Exception {

    servicesService = mock(ServicesService.class);
    Map<String, List<Service>> servicesMap = initServices();
    when(servicesService.findAll(anyLong())).thenReturn(servicesMap);

    subject = new ServicesCache(servicesService, 0, 1000L, 0);
    //cache needs to kick in
    Thread.sleep(10);
  }
  @Test
  public void getServices() throws Exception {
    //the setup initializes the cache for the first hit
    List<Service> services = subject.getAllServices("en");
    assertEquals(1, services.size());

    Map<String, List<Service>> servicesMap = initServices();
    servicesMap.get("en").add(new Service());
    when(servicesService.findAll(anyLong())).thenReturn(servicesMap);

    services = subject.getAllServices("en");
    assertEquals(1, services.size());

    //now wait for the cache to expire and be refilled
    await().atMost(Duration.FIVE_SECONDS).until(() -> subject.getAllServices("en").size(), is(2));
  }

  @Test
  public void serviceCacheShouldClone() {
    List<Service> services1 = subject.getAllServices("en");

    Service service1 = services1.get(0);
    List<Service> services2 = subject.getAllServices("en");
    Service service2 = services2.get(0);
    assertEquals("Cloned services should 'be equal'", service1, service2);
    assertFalse("Clones services should not be ==", service1 == service2);
  }

  private Map<String, List<Service>> initServices() {
    Map<String, List<Service>> services = new HashMap<>();
    List<Service> nl = new ArrayList<>();
    Service service = new Service();
    nl.add(service);
    List<Service> en = new ArrayList<>();
    en.add(service);
    services.put("nl", nl);
    services.put("en", en);
    return services;
  }

}
