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
package dashboard.domain;

import com.google.common.base.MoreObjects;
import dashboard.manage.EntityType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Abstract class for either ServiceProvider or IdentityProvider
 */
@SuppressWarnings("serial")
public abstract class Provider implements Comparable<Provider>, Serializable {

    private String id;
    private String internalId;
    private Long eid;
    private String state;
    private EntityType entityType;

    /**
     * Name of the Provider. SURFfederatie knows only 1 value, SURFconext supports a value per language.
     * This name field can be used for sorting
     */
    private String name;
    private String logoUrl;
    private String publishInEdugainDate;

    private Map<String, String> names = new HashMap<>();
    private Map<String, String> organisations = new HashMap<>();
    private Map<String, String> homeUrls = new HashMap<>();
    private Map<String, String> descriptions = new HashMap<>();
    private Map<String, String> displayNames = new HashMap<>();

    private boolean linked;

    private List<ContactPerson> contactPersons = new ArrayList<>();

    private boolean allowedAll;
    private Set<String> allowedEntityIds;
    private boolean noConsentRequired;
    private boolean publishedInEdugain;

    public Provider() {
    }

    public Provider(Map<String, Object> metaData) {
        this.internalId = (String) metaData.get("internalId");
        this.id = (String) metaData.get("entityid");
        this.eid = ((Number) metaData.get("eid")).longValue();
        this.state = (String) metaData.get("state");
        addName("en", (String) metaData.get("name:en"));
        addName("nl", (String) metaData.get("name:nl"));
        addName("pt", (String) metaData.get("name:pt"));
        this.name = names.isEmpty() ? (String) metaData.get("entityid") : names.getOrDefault("en", names.get("nl"));
        this.logoUrl = (String) metaData.get("logo:0:url");
        addOrganisation("en", (String) metaData.get("OrganizationDisplayName:en"), (String) metaData.get("OrganizationName:en"));
        addOrganisation("nl", (String) metaData.get("OrganizationDisplayName:nl"), (String) metaData.get("OrganizationName:nl"));
        addOrganisation("pt", (String) metaData.get("OrganizationDisplayName:pt"), (String) metaData.get("OrganizationName:pt"));
        addHomeUrl("en", (String) metaData.get("OrganizationURL:en"));
        addHomeUrl("nl", (String) metaData.get("OrganizationURL:nl"));
        addHomeUrl("pt", (String) metaData.get("OrganizationURL:pt"));
        addDescription("en", (String) metaData.get("description:en"));
        addDescription("nl", (String) metaData.get("description:nl"));
        addDescription("pt", (String) metaData.get("description:pt"));
        addDisplayName("en", (String) metaData.get("displayName:en"));
        addDisplayName("nl", (String) metaData.get("displayName:nl"));
        addDisplayName("pt", (String) metaData.get("displayName:pt"));
        IntStream.rangeClosed(0, 3).forEach(i -> {
            String contactType = (String) metaData.get("contacts:" + i + ":contactType");
            if (contactType != null) {
                addContactPerson(new ContactPerson(
                        safeString(metaData.get("contacts:" + i + ":givenName") + " " + safeString(metaData.get("contacts" +
                                ":" + i +
                                ":surName"))).trim(),
                        (String) metaData.get("contacts:" + i + ":emailAddress"),
                        (String) metaData.get("contacts:" + i + ":telephoneNumber"),
                        contactPersonType(contactType),
                        booleanValue(metaData.get("contacts:" + i + ":isSirtfiSecurityContact")),
                        false)
                );
            }
        });
        this.allowedAll = getAllowedAll(metaData);
        this.allowedEntityIds = getAllowedEntries(metaData);
        this.noConsentRequired = booleanValue(metaData.get("coin:no_consent_required"));
        this.publishedInEdugain = booleanValue(metaData.get("coin:publish_in_edugain"));
        this.publishInEdugainDate = (String) metaData.get("coin:publish_in_edugain_date");
    }

    private void addOrganisation(String language, String organisationName, String organisationNameFallback) {
        if (organisationName != null) {
            this.organisations.put(language, organisationName);
        } else if (organisationNameFallback != null) {
            this.organisations.put(language, organisationNameFallback);
        }
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

    protected void setName(String name) {
        this.name = name;
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

    public Map<String, String> getOrganisations() {
        return organisations;
    }

    public String getOrganisation(Language language) {
        if (organisations == null) {
            return getName();
        }
        String organisation = organisations.get(language.name().toLowerCase());
        if (organisation != null) {
            return organisation;
        }
        return organisations.isEmpty() ? "" : organisations.values().iterator().next();
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

    public String getDisplayName(Language language) {
        if (displayNames == null) {
            return null;
        } else {
            return displayNames.get(language.name().toLowerCase());
        }
    }

    public Map<String, String> getDescriptions() {
        return descriptions;
    }

    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    public Long getEid() {
        return eid;
    }

    public void setEid(Long eid) {
        this.eid = eid;
    }

    private void addDescription(String language, String description) {
        this.descriptions.put(language, description);
    }

    private void addDisplayName(String language, String displayName) {
        this.displayNames.put(language, displayName);
    }

    public boolean isNoConsentRequired() {
        return noConsentRequired;
    }

    public void setNoConsentRequired(boolean noConsentRequired) {
        this.noConsentRequired = noConsentRequired;
    }

    public boolean isAllowedAll() {
        return allowedAll;
    }

    public Set<String> getAllowedEntityIds() {
        return allowedEntityIds;
    }

    public void addAllowedEntityId(String spId) {
        allowedEntityIds.add(spId);
    }

    public String getPublishInEdugainDate() {
        return publishInEdugainDate;
    }

    public boolean isPublishedInEdugain() {
        return publishedInEdugain;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public String getInternalId() {
        return internalId;
    }

    public EntityType getEntityType() {
        return entityType;
    }


    protected boolean booleanValue(Object metadataValue) {
        return metadataValue != null && (metadataValue.equals("1") || (metadataValue instanceof Boolean && (boolean) metadataValue));
    }

    protected Boolean booleanOptionalValue(Object metadataValue) {
        return metadataValue == null ? null : (metadataValue.equals("1") || (metadataValue instanceof Boolean && (boolean) metadataValue));
    }

    protected String safeString(Object o) {
        return o != null ? o.toString() : "";
    }

    private ContactPersonType contactPersonType(String contactType) {
        try {
            return ContactPersonType.valueOf(ContactPersonType.class, contactType);
        } catch (IllegalArgumentException e) {
            return ContactPersonType.other;
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

    public enum Language {
        EN, NL, PT;
    }
}
