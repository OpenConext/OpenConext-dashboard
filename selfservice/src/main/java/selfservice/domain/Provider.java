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
package selfservice.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.MoreObjects;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.springframework.util.CollectionUtils;

import selfservice.domain.csa.ContactPerson;
import selfservice.domain.csa.ContactPersonType;
import selfservice.domain.csa.ProviderType;

/**
 * Abstract class for either ServiceProvider or IdentityProvider
 */
@SuppressWarnings("serial")
public abstract class Provider implements Comparable<Provider>, Serializable {

  public enum Language {
    EN, NL;
  }

  private ProviderType type;

  /**
   * Name of the Provider. SURFfederatie knows only 1 value, SURFconext supports a value per language.
   * This name field can be used for sorting
   */
  private String name;
  private String homeUrl;
  private String logoUrl;
  private String metadataUrl;

  private List<ContactPerson> contactPersons = new ArrayList<>();

  private Map<String, String> names = new HashMap<>();
  private Map<String, String> homeUrls = new HashMap<>();
  private Map<String, String> descriptions = new HashMap<>();

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

  public Map<String, String> getHomeUrls() {
    return homeUrls;
  }

  public void setHomeUrls(Map<String, String> homeUrls) {
    this.homeUrls = homeUrls;
  }

  public void addHomeUrl(String language, String homeUrl) {
    this.homeUrls.put(language, homeUrl);
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

  /**
   * @deprecated use #getDescriptions with the language code as key
   */
  public String getDescription() {
    return this.descriptions.get(Language.EN.name().toLowerCase());
  }

  /**
   * @deprecated use #addDescriptions with the language code as key
   */
  public void setDescription(String description) {
    addDescription(Language.EN.name().toLowerCase(), description);
  }

  public List<ContactPerson> getContactPersons() {
    return contactPersons;
  }

  public ContactPerson getContactPerson(ContactPersonType type) {
    if (CollectionUtils.isEmpty(contactPersons)) {
      return null;
    }

    return contactPersons.stream()
        .filter(cp -> cp.getContactPersonType().equals(type))
        .findFirst().orElse(null);
  }

  public void setContactPersons(List<ContactPerson> contactPersons) {
    this.contactPersons = contactPersons;
  }

  public void addContactPerson(ContactPerson contactPerson) {
    this.contactPersons.add(contactPerson);
  }

  public Map<String, String> getNames() {
    return names;
  }

  public String getName(Language language) {
    if (names == null) {
      return getName();
    } else {
      if (StringUtils.isBlank(names.get(language.name().toLowerCase()))) {
        return getName();
      } else {
        return names.get(language.name().toLowerCase());
      }
    }
  }

  public void setNames(Map<String, String> names) {
    this.names = names;
  }

  public void addName(String language, String name) {
    this.names.put(language, name);
  }

  public String getDescription(Language language) {
    if (descriptions == null) {
      return null;
    } else {
      return descriptions.get(language.name().toLowerCase());
    }
  }

  public Map<String, String> getDescriptions() {
    return descriptions;
  }

  public void setDescriptions(Map<String, String> descriptions) {
    this.descriptions = descriptions;
  }

  public void addDescription(String language, String description) {
    this.descriptions.put(language, description);
  }

  public static Comparator<Provider> firstStatusThenName() {
    return new Comparator<Provider>() {
      @Override
      public int compare(Provider o1, Provider o2) {
        return new CompareToBuilder()
          .append(!o1.isLinked(), !o2.isLinked())
          .append(o1.getName(), o2.getName())
          .toComparison();
      }
    };
  }

  @Override
  public int compareTo(Provider that) {
    return new CompareToBuilder()
      .append(StringUtils.lowerCase(this.name), StringUtils.lowerCase(that.name))
      .toComparison();
  }

  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("name", name)
      .add("names", names)
      .add("type", type)
      .add("id", getId())
      .add("contactPersons", contactPersons)
      .add("descriptions", descriptions)
      .toString();
  }
}
