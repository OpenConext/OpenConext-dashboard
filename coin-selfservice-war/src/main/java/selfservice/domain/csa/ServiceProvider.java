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

package selfservice.domain.csa;

import java.io.Serializable;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import selfservice.domain.ARP;

public class ServiceProvider extends Provider implements Serializable {

  private String id;

  private ARP arp;

  private String eulaURL;

  private Map<String, String> urls;

  private String gadgetBaseUrl;

  private boolean display;

  private String applicationUrl;

  private String institutionId;

  private boolean publishedInEdugain;

  private boolean exampleSingleTenant;

  public ServiceProvider(String id) {
    this.id = id;
  }

  /**
   * @deprecated name is not a single value String anymore
   */
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

  public boolean isIdpVisibleOnly() {
    return !display;
  }

  public void setIdpVisibleOnly(boolean idpVisibleOnly) {
    this.display = !idpVisibleOnly;
  }

  public String getEulaURL() {
    return eulaURL;
  }

  public void setEulaURL(String eulaURL) {
    this.eulaURL = eulaURL;
  }

  public Map<String, String> getUrls() {
    return urls;
  }

  public String getUrl() {
    return CollectionUtils.isEmpty(this.urls) ? null : urls.values().iterator().next();
  }

  public void setUrls(Map<String, String> urls) {
    this.urls = urls;
  }

  public String getGadgetBaseUrl() {
    return gadgetBaseUrl;
  }

  public void setGadgetBaseUrl(String gadgetBaseUrl) {
    this.gadgetBaseUrl = gadgetBaseUrl;
  }

  public ARP getArp() {
    return arp;
  }

  public void setArp(ARP arp) {
    this.arp = arp;
  }

  public boolean isPublishedInEdugain() {
    return publishedInEdugain;
  }

  public void setPublishedInEdugain(boolean publishedInEdugain) {
    this.publishedInEdugain = publishedInEdugain;
  }

  public boolean isExampleSingleTenant() {
    return exampleSingleTenant;
  }

  public void setExampleSingleTenant(boolean exampleSingleTenant) {
    this.exampleSingleTenant = exampleSingleTenant;
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

    if (id != null ? !id.equals(that.id) : that.id != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return (id == null) ? 0 : id.hashCode();
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append("ServiceProvider");
    sb.append("{id='").append(id).append('\'');
    if (arp != null) {
      sb.append(", arp=").append(arp);
    }
    sb.append(", eulaUrl='").append(eulaURL).append('\'');
    sb.append(", gadgetBaseUrl='").append(gadgetBaseUrl).append('\'');
    sb.append(", idpVisibleOnly=").append(isIdpVisibleOnly());
    sb.append(' ').append(super.toString());
    sb.append('}');
    return sb.toString();
  }

  public String getApplicationUrl() {
    return applicationUrl;
  }

  public void setApplicationUrl(String applicationUrl) {
    this.applicationUrl = applicationUrl;
  }

  public String getInstitutionId() {
    return institutionId;
  }

  public void setInstitutionId(String institutionId) {
    this.institutionId = institutionId;
  }

}
