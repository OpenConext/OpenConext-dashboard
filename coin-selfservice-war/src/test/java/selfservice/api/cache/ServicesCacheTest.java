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
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.jayway.awaitility.Duration;

import org.junit.Before;
import org.junit.Test;

import selfservice.domain.Service;
import selfservice.service.ServicesService;

public class ServicesCacheTest {

  private ServicesCache subject;
  private ServicesService servicesServiceMock;

  @Before
  public void setUp() throws Exception {
    servicesServiceMock = mock(ServicesService.class);
    when(servicesServiceMock.findAll(anyLong())).thenReturn(ImmutableMap.of("en", ImmutableList.of(new Service())));

    subject = new ServicesCache(servicesServiceMock, 0, 1000L, 0);

    await().atMost(Duration.ONE_SECOND).until(() -> subject.getAllServices("en"), hasSize(1));
  }

  @Test
  public void getServices() throws Exception {
    assertThat(subject.getAllServices("en"), hasSize(1));

    when(servicesServiceMock.findAll(anyLong())).thenReturn(ImmutableMap.of("en", ImmutableList.of(new Service(), new Service())));

    await().atMost(Duration.FIVE_SECONDS).until(() -> subject.getAllServices("en"), hasSize(2));
  }

  @Test
  public void serviceCacheShouldClone() {
    Service service1 = subject.getAllServices("en").get(0);
    Service service2 = subject.getAllServices("en").get(0);

    assertEquals("Cloned services should 'be equal'", service1, service2);
    assertFalse("Clones services should not be ==", service1 == service2);
  }

}
