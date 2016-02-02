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
package selfservice.cache;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import selfservice.domain.Service;
import selfservice.service.ServicesService;

public class ServicesCache extends AbstractCache {

  private static final Logger LOG = LoggerFactory.getLogger(ServicesCache.class);

  private final ServicesService servicesService;

  private final Long callDelay;

  private AtomicReference<Map<String, List<Service>>> allServicesCache = new AtomicReference<>();

  public ServicesCache(ServicesService servicesService, long delay, long duration, long callDelay) {
    super(delay, duration);
    this.callDelay = callDelay;
    this.servicesService = servicesService;
  }

  public List<Service> getAllServices(final String lang) {
    checkArgument("en".equalsIgnoreCase(lang) || "nl".equalsIgnoreCase(lang), "The only languages supported are 'nl' and 'en'");

    List<Service> services = allServicesCache.get().get(lang);
    if (services == null) {
      LOG.warn("Cache miss for lang '{}', will return empty list", lang);
      services = Collections.emptyList();
    }

    return SerializationUtils.clone(new ArrayList<>(services));
  }

  @Override
  protected void doPopulateCache() {
    Map<String, List<Service>> services = servicesService.findAll(callDelay);
    allServicesCache.set(services);
  }

  @Override
  protected String getCacheName() {
    return "Service Registry cache (SPs)";
  }
}
