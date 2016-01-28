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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import selfservice.domain.IdentityProvider;
import selfservice.service.IdentityProviderService;

@Component
public class IdentityProviderCache extends AbstractCache {

  private static final Logger LOG = LoggerFactory.getLogger(IdentityProviderCache.class);
  /**
   * This is a lazy-loading cache. Initially, the populateCache() will not do any call to a backend.
   * Each call to getServiceProvider() first checks the cache. If not found, load lazily and put in cache.
   * Subsequent populateCache()'s will then only update this already loaded items.
   */
  private ConcurrentHashMap<String, List<String>> spIdsCache = new ConcurrentHashMap<>();
  private ConcurrentHashMap<String, IdentityProvider> idpCache = new ConcurrentHashMap<>();

  private final IdentityProviderService idpService;

  private final Long callDelay;

  @Autowired
  public IdentityProviderCache(IdentityProviderService idpService,
                       @Value("${cache.default.initialDelay}") long initialDelay,
                       @Value("${cache.default.delay}") long delay,
                       @Value("${cacheMillisecondsCallDelay}") long callDelay) {
    super(initialDelay, delay);
    this.callDelay = callDelay;
    this.idpService = idpService;
  }

  public List<String> getServiceProviderIdentifiers(String identityProviderId) {
    List<String> spIdentifiers = spIdsCache.get(identityProviderId);
    if (spIdentifiers == null) {
      spIdentifiers = idpService.getLinkedServiceProviderIDs(identityProviderId);
      spIdsCache.put(identityProviderId, spIdentifiers);
    }
    if (spIdentifiers == null) {
      spIdentifiers = Collections.emptyList();
    }
    return spIdentifiers;
  }

  @Override
  protected void doPopulateCache() {
    populateSPIds();
    populateIdps();
  }

  private void populateIdps() {
    List<IdentityProvider> allIdentityProviders = idpService.getAllIdentityProviders();
    ConcurrentHashMap<String, IdentityProvider> newIdpCache = new ConcurrentHashMap<>(allIdentityProviders.size());
    for (IdentityProvider idp : allIdentityProviders) {
      newIdpCache.put(idp.getId(), idp);
    }
    idpCache = newIdpCache;
  }

  public IdentityProvider getIdentityProvider(String idpEntityId) {
    return Optional.ofNullable(idpCache.get(idpEntityId)).orElseGet(() -> {
      // kind of bizar, means we have a new IdP in between cache re-populate (happens in theory only and in integration tests)
      Optional<IdentityProvider> liveIdp = idpService.getIdentityProvider(idpEntityId);
      liveIdp.ifPresent(idp -> idpCache.put(idp.getId(), idp));
      return liveIdp.orElse(null);
    });
  }

  private void populateSPIds() {
    Map<String, List<String>> swap = new HashMap<>();

    for (String idpId : spIdsCache.keySet()) {
      List<String> spIdentifiers = idpService.getLinkedServiceProviderIDs(idpId);
      if (callDelay > 0) {
        try {
          Thread.sleep(callDelay);
        } catch (InterruptedException e) {
          LOG.error("interrupted while in call delay", e);
          break;
        }
      }
      swap.put(idpId, spIdentifiers);
    }
    spIdsCache.putAll(swap);
  }

  @Override
  protected String getCacheName() {
    return "Service Registry cache (idps)";
  }
}
