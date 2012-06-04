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

package nl.surfnet.coin.selfservice.domain;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import org.apache.commons.lang.SerializationUtils;

/**
 * Root element of the SURFfederatie config file
 */
public class FederatieConfig {

  @XStreamAlias(value = "SPs")
  private List<ServiceProvider> sps = new ArrayList<ServiceProvider>();

  @XStreamAlias(value = "IdPs")
  private List<IdentityProvider> idPs = new ArrayList<IdentityProvider>();

  /**
   * Returns a List of cloned {@link ServiceProvider} objects. The clone is needed to manipulate the individual Service
   * Providers while the original list stays cached for all users.
   *
   * @return List of cloned {@link ServiceProvider} objects
   */
  public List<ServiceProvider> getSps() {
    List<ServiceProvider> spList = new ArrayList<ServiceProvider>(sps.size());
    for (ServiceProvider sp : sps) {
      // not the fastest way, but this list should be relatively small
      // if we run into performance issues: write own clone() method in ServiceProvider class
      final Object clone = SerializationUtils.clone(sp);
      spList.add((ServiceProvider) clone);
    }
    return spList;
  }

  public void setSps(List<ServiceProvider> sps) {
    this.sps = sps;
  }

  public void addSp(ServiceProvider sp) {
    this.sps.add(sp);
  }

  public List<IdentityProvider> getIdPs() {
    return idPs;
  }

  public void setIdPs(List<IdentityProvider> idPs) {
    this.idPs = idPs;
  }

  public void addIdP(IdentityProvider idP) {
    this.idPs.add(idP);
  }
}
