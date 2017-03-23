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

import com.google.common.base.MoreObjects;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.springframework.util.CollectionUtils;
import selfservice.domain.csa.ContactPerson;
import selfservice.domain.csa.ContactPersonType;

import java.io.Serializable;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Abstract class for either ServiceProvider or IdentityProvider
 */
@SuppressWarnings("serial")
public abstract class Provider implements Comparable<Provider>, Serializable {

  private String id;
  /**
   * Name of the Provider. SURFfederatie knows only 1 value, SURFconext supports a value per language.
   * This name field can be used for sorting
   */
  private String name;
  private String logoUrl;
  private String publishInEdugainDate;

  private Map<String, String> names = new HashMap<>();
  private Map<String, String> homeUrls = new HashMap<>();
  private Map<String, String> descriptions = new HashMap<>();

  private boolean linked;

  private List<ContactPerson> contactPersons = new ArrayList<>();

  private boolean allowedAll;
  private Set<String> allowedEntityIds;
  private boolean noConsentRequired;
  private boolean publishedInEdugain;

  public Provider() {
  }

  public Provider(Map<String, Object> metaData) {
    this.id = (String) metaData.get("entityid");
    addName("en", (String) metaData.get("name:en"));
    addName("nl", (String) metaData.get("name:nl"));
    this.name = names.isEmpty() ? (String) metaData.get("entityid") : names.getOrDefault("en", names.get("nl"));
    this.logoUrl = (String) metaData.get("logo:0:url");
    addHomeUrl("en", (String) metaData.get("OrganizationURL:en"));
    addHomeUrl("nl", (String) metaData.get("OrganizationURL:nl"));
    addDescription("en", (String) metaData.get("description:en"));
    addDescription("nl", (String) metaData.get("description:nl"));
    IntStream.rangeClosed(0, 2).forEach(i -> {
      String contactType = (String) metaData.get("contacts:" + i + ":contactType");
      if (contactType != null) {
        addContactPerson(new ContactPerson(
          safeString(metaData.get("contacts:" + i + ":givenName") + " " + safeString(metaData.get("contacts:" + i + ":surName"))).trim(),
          (String) metaData.get("contacts:" + i + ":emailAddress"),
          (String) metaData.get("contacts:" + i + ":telephoneNumber"),
          contactPersonType(contactType)
        ));
      }
    });
    this.allowedAll = getAllowedAll(metaData);
    this.allowedEntityIds = getAllowedEntries(metaData);
    this.noConsentRequired = booleanValue(metaData.get("coin:no_consent_required"));
    this.publishedInEdugain = booleanValue(metaData.get("coin:publish_in_edugain"));
    this.publishInEdugainDate = (String) metaData.get("coin:publish_in_edugain_date");
  }

  public enum Language {
    EN, NL;
  }

  public String getId() {
    return id;
  }

  protected void setId(String id) {
    this.id = id;
  }

  public boolean isLinked() {
    return linked;
  }

  public void setLinked(boolean linked) {
    this.linked = linked;
  }

  public String getName() {
    return name;
  }

  public Map<String, String> getHomeUrls() {
    return homeUrls;
  }

  private void addHomeUrl(String language, String homeUrl) {
    if (homeUrl != null) {
      this.homeUrls.put(language, homeUrl);
    }
  }

  public String getLogoUrl() {
    return logoUrl;
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

  protected void setName(String name) {
    this.name = name;
  }

  protected void addName(String language, String name) {
    if (name != null) {
      this.names.put(language, name);
    }
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

  private void addDescription(String language, String description) {
    this.descriptions.put(language, description);
  }

  public void setNoConsentRequired(boolean noConsentRequired) {
    this.noConsentRequired = noConsentRequired;
  }

  public boolean isNoConsentRequired() {
    return noConsentRequired;
  }

  public boolean isAllowedAll() {
    return allowedAll;
  }

  public Set<String> getAllowedEntityIds() {
    return allowedEntityIds;
  }

  public String getPublishInEdugainDate() {
    return publishInEdugainDate;
  }

  public boolean isPublishedInEdugain() {
    return publishedInEdugain;
  }

  protected boolean booleanValue(Object metadataValue) {
    return metadataValue != null && metadataValue.equals("1");
  }

  protected String safeString(Object o) {
    return o != null ? o.toString() : "";
  }

  private ContactPersonType contactPersonType(String contactType) {
    switch (contactType) {
      case "technical":
        return ContactPersonType.technical;
      case "support":
        return ContactPersonType.help;
      default:
        return ContactPersonType.administrative;
    }
  }

  private Set<String> getAllowedEntries(Map<String, Object> entry) {
    @SuppressWarnings("unchecked")
    List<String> allowedEntities = (List<String>) entry.getOrDefault("allowedEntities", Collections.emptyList());
    return new HashSet<>(allowedEntities);
  }

  private boolean getAllowedAll(Map<String, Object> entry) {
    String allowedall = (String) entry.getOrDefault("allowedall", "yes");
    return allowedall.equals("yes");
  }

  @Override
  public int compareTo(Provider that) {
    return new CompareToBuilder()
      .append(StringUtils.lowerCase(this.name), StringUtils.lowerCase(that.name))
      .toComparison();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Provider provider = (Provider) o;
    return Objects.equals(id, provider.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("name", name)
      .add("names", names)
      .add("id", getId())
      .add("contactPersons", contactPersons)
      .add("descriptions", descriptions)
      .toString();
  }
}
