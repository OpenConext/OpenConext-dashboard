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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * Service Provider
 */

@XStreamAlias("SP")
public class ServiceProvider extends Provider implements Serializable {

  private static final long serialVersionUID = 1L;

  @XStreamAlias("id")
  @XStreamAsAttribute
  private String id;


  @XStreamAlias("ARP")
  @XStreamImplicit
  private List<ARP> arps = new ArrayList<ARP>();

  @XStreamAlias("ACL")
  private ACL acl;

  public ServiceProvider(String id, String name) {
    this.id = id;
    setName(name);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public ACL getAcl() {
    return acl;
  }

  public void setAcl(ACL acl) {
    this.acl = acl;
  }

  public List<ARP> getArps() {
    return arps;
  }

  public void setArps(List<ARP> arps) {
    this.arps = arps;
  }

  public void addArp(ARP arp) {
    this.arps.add(arp);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ServiceProvider that = (ServiceProvider) o;

    if (!id.equals(that.id)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append("ServiceProvider");
    sb.append("{id='").append(id).append('\'');
    sb.append(", arps=").append(arps);
    sb.append(", acl=").append(acl);
    sb.append(' ').append(super.toString());
    sb.append('}');
    return sb.toString();
  }
}
