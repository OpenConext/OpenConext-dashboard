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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * Identity provider
 */
@XStreamAlias("IdP")
public class IdentityProvider extends Provider implements Serializable {

  private static final long serialVersionUID = 1L;

  @XStreamAlias("id")
  @XStreamAsAttribute
  private String id;


  @XStreamAlias("InstitutionID")
  private String institutionId;

  @XStreamAlias("SSOLocation")
  private String ssoLocation;

  @XStreamAlias("SLOLocation")
  private String sloLocation;

  public IdentityProvider() {

  }

  public IdentityProvider(String id, String institutionId, String name) {
    this.id = id;
    this.institutionId = institutionId;
    setName(name);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getInstitutionId() {
    return institutionId;
  }

  public void setInstitutionId(String institutionId) {
    this.institutionId = institutionId;
  }

  public String getSsoLocation() {
    return ssoLocation;
  }

  public void setSsoLocation(String ssoLocation) {
    this.ssoLocation = ssoLocation;
  }

  public String getSloLocation() {
    return sloLocation;
  }

  public void setSloLocation(String sloLocation) {
    this.sloLocation = sloLocation;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    IdentityProvider that = (IdentityProvider) o;

    if (id != null ? !id.equals(that.id) : that.id != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return (id == null) ? 0 : id.hashCode();
  }
}
