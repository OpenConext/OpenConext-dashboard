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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;

import nl.surfnet.coin.janus.Janus;
import nl.surfnet.coin.selfservice.domain.Provider;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.ProviderService;

public class ServiceRegistryProviderService implements ProviderService {

  @Resource(name="janusClient")
  private Janus janusClient;

  @Override
  @Cacheable(value = { "sps-janus" })
  public List<Provider> getProviders(String idpId) {
    final List<String> sps = janusClient.getAllowedSps(idpId);
    List<Provider> spList = new ArrayList<Provider>();
    for (String spname : sps) {
      // TODO: enrich the sp
      ServiceProvider sp = new ServiceProvider(spname, spname);
      spList.add(sp);
    }
    return spList;
  }
}
