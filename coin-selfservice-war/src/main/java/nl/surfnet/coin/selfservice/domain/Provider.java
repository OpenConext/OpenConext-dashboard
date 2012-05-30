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
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * Abstract class for either ServiceProvider or IdentityProvider
 */
public abstract class Provider implements Comparable<Provider> {

  @XStreamAlias("type")
  @XStreamAsAttribute
  private ProviderType type;

  @XStreamAlias("Name")
  private String name;

  @XStreamAlias("HomeURL")
  private String homeUrl;

  @XStreamAlias("LogoURL")
  private String logoUrl;

  @XStreamAlias("MetadataURL")
  private String metadataUrl;

  @XStreamAlias("ContactPersons")
  private List<ContactPerson> contactPersons = new ArrayList<ContactPerson>();

  private String description;

  private boolean linked;

  public boolean isLinked() {
    return linked;
  }

  public void setLinked(boolean linked) {
    this.linked = linked;
  }

  public abstract String getId();

  public abstract void setId(String id);

  public ProviderType getType() {
    return type;
  }

  public void setType(ProviderType type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getHomeUrl() {
    return homeUrl;
  }

  public void setHomeUrl(String homeUrl) {
    this.homeUrl = homeUrl;
  }

  public String getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }

  public String getMetadataUrl() {
    return metadataUrl;
  }

  public void setMetadataUrl(String metadataUrl) {
    this.metadataUrl = metadataUrl;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<ContactPerson> getContactPersons() {
    return contactPersons;
  }

  public void setContactPersons(List<ContactPerson> contactPersons) {
    this.contactPersons = contactPersons;
  }

  public void addContactPerson(ContactPerson contactPerson) {
    this.contactPersons.add(contactPerson);
  }

  @Override
  public int compareTo(Provider that) {
    final int EQUAL = 0;

    if (this == that) {
      return EQUAL;
    }
    final String thisName = this.getName();
    final String thatName = that.getName();
    final String thisId = this.getId();
    final String thatId = that.getId();

    if (thisName != null && thatName != null) {
      return thisName.compareToIgnoreCase(thatName);
    } else if (thisName != null && thatId != null) {
      return thisName.compareToIgnoreCase(thatId);
    } else if (thisId != null && thatName != null) {
      return thisId.compareToIgnoreCase(thatName);
    } else if (thisId != null && thatId != null) {
      return thisId.compareToIgnoreCase(thatId);
    }

    //all comparisons have yielded equality
    //verify that compareTo is consistent with equals (optional)
    assert this.equals(that) : "compareTo inconsistent with equals.";

    return EQUAL;
  }

  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("name", name)
        .append("type", type)
        .append("id", getId())
        .append("contactPersons", getContactPersons())
        .toString();
  }
}
