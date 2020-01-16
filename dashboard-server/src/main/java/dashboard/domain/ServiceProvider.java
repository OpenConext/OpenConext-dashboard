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

import dashboard.manage.DashboardConnectOption;
import dashboard.manage.EntityType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;


public class ServiceProvider extends Provider implements Serializable, Cloneable {

    private static final List<String> nameIdFormats = Arrays.asList("NameIDFormat", "NameIDFormats:0", "NameIDFormats:1", "NameIDFormats:2");

    private Map<String, String> arpMotivations;
    private String applicationUrl;
    private String institutionId;
    private String eulaURL;
    private String interfedSource;
    private String privacyStatementUrlEn;
    private String privacyStatementUrlNl;
    private String registrationInfo;
    private String registrationPolicyUrlEn;
    private String registrationPolicyUrlNl;
    private String entityCategories1;
    private String entityCategories2;
    private boolean idpVisibleOnly;
    private boolean policyEnforcementDecisionRequired;
    private EntityType entityType;
    private DashboardConnectOption dashboardConnectOption;
    private boolean aansluitovereenkomstRefused;
    private boolean hidden;
    private LicenseStatus licenseStatus;
    private List<String> nameIds;
    private List<String> resourceServers;

    private ARP arp;
    private PrivacyInfo privacyInfo;

    private Map<String, String> urls = new HashMap<>();
    private String wikiUrlNl;
    private String wikiUrlEn;
    private boolean strongAuthenticationEnabled;
    private String minimalLoaLevel;

    private List<String> typeOfServicesNl = new ArrayList<>();
    private List<String> typeOfServicesEn = new ArrayList<>();

    private String manipulationNotes;
    private boolean manipulation;
    private String contractualBase;

    @SuppressWarnings("unchecked")
    public ServiceProvider(Map<String, Object> metaData) {
        super(metaData);
        this.dashboardConnectOption = DashboardConnectOption.fromOption((String) metaData.getOrDefault("coin:dashboard_connect_option", "connect_with_interaction"));
        this.applicationUrl = (String) metaData.get("coin:application_url");
        this.institutionId = (String) metaData.get("coin:institution_id");
        this.eulaURL = (String) metaData.get("coin:eula");
        this.interfedSource = (String) metaData.getOrDefault("coin:interfed_source", "SURFconext");
        this.privacyStatementUrlEn = (String) metaData.get("mdui:PrivacyStatementURL:en");
        this.privacyStatementUrlNl = (String) metaData.get("mdui:PrivacyStatementURL:nl");
        this.registrationInfo = (String) metaData.get("mdrpi:RegistrationInfo");
        this.registrationPolicyUrlEn = (String) metaData.get("mdrpi:RegistrationPolicy:en");
        this.registrationPolicyUrlNl = (String) metaData.get("mdrpi:RegistrationPolicy:nl");
        this.entityCategories1 = (String) metaData.get("coin:entity_categories:1");
        this.entityCategories2 = (String) metaData.get("coin:entity_categories:2");
        this.licenseStatus = LicenseStatus.fromManage((String) metaData.get("coin:ss:license_status"));
        this.idpVisibleOnly = booleanValue(metaData.get("coin:ss:idp_visible_only"));
        this.policyEnforcementDecisionRequired = booleanValue(metaData.get
            ("coin:policy_enforcement_decision_required"));
        this.minimalLoaLevel = (String) metaData.get("coin:stepup:requireloa");
        this.strongAuthenticationEnabled = StringUtils.hasText(this.minimalLoaLevel);
        this.aansluitovereenkomstRefused = booleanValue(metaData.get("coin:ss:aansluitovereenkomst_refused"));
        this.hidden = booleanValue(metaData.get("coin:ss:hidden"));
        this.wikiUrlEn = (String) metaData.get("coin:ss:wiki_url:en");
        this.wikiUrlNl = (String) metaData.get("coin:ss:wiki_url:nl");
        Object attributes = metaData.get("attributes");
        if (attributes != null) {
            if (attributes instanceof List) {
                Map<String, List<String>> collect = ((List<String>) attributes).stream().collect(toMap(attr ->
                    attr, attr -> Collections.singletonList("*")));
                this.arp = ARP.fromAttributes(collect);
            } else {
                this.arp = ARP.fromAttributes((Map<String, List<String>>) attributes);
            }
        } else {
            this.arp = ARP.noArp();
        }
        String typeOfService = (String) metaData.get("coin:ss:type_of_service:en");
        if (StringUtils.hasText(typeOfService)) {
            this.typeOfServicesEn = Arrays.asList(typeOfService.split(","));
        }
        typeOfService = (String) metaData.get("coin:ss:type_of_service:nl");
        if (StringUtils.hasText(typeOfService)) {
            this.typeOfServicesNl = Arrays.asList(typeOfService.split(","));
        }

        addUrl("en", (String) metaData.get("url:en"));
        addUrl("nl", (String) metaData.get("url:nl"));

        this.privacyInfo = this.buildPrivacyInfo(metaData);
        this.arpMotivations = (Map<String, String>) metaData.get("motivations");
        this.manipulationNotes = (String) metaData.get("manipulationNotes");
        this.manipulation = StringUtils.hasText( (String) metaData.get("manipulation"));
        this.contractualBase = (String) metaData.getOrDefault("coin:contractual_base", "NA");

        this.nameIds = nameIdFormats.stream()
                .filter(nameId -> metaData.containsKey(nameId))
                .map(nameId -> (String) metaData.get(nameId))
                .collect(Collectors.toList());

        this.resourceServers = (List<String>) metaData.get("allowedResourceServers");
    }

    private PrivacyInfo buildPrivacyInfo(Map<String, Object> metaData) {
        return new PrivacyInfo(
            (String) metaData.get("coin:privacy:access_data"),
            booleanOptionalValue(metaData.get("coin:privacy:certification")),
            (String) metaData.get("coin:privacy:certification_location"),
            (String) metaData.get("coin:privacy:country"),
            (String) metaData.get("coin:privacy:other_info"),
            booleanOptionalValue(metaData.get("coin:privacy:privacy_policy")),
            (String) metaData.get("coin:privacy:privacy_policy_url"),
            (String) metaData.get("coin:privacy:security_measures"),
            (String) metaData.get("coin:privacy:sn_dpa_why_not"),
            booleanOptionalValue(metaData.get("coin:privacy:surfmarket_dpa_agreement")),
            booleanOptionalValue(metaData.get("coin:privacy:surfnet_dpa_agreement")),
            (String) metaData.get("coin:privacy:what_data"),
            booleanOptionalValue(metaData.get("coin:ss:aansluitovereenkomst_refused")),
            (String) metaData.get("coin:privacy:certification_valid_from"),
            (String) metaData.get("coin:privacy:certification_valid_to"),
            booleanOptionalValue(metaData.get("coin:privacy:gdpr_is_in_wiki"))
        );

    }

    public boolean isIdpVisibleOnly() {
        return idpVisibleOnly;
    }

    public boolean isPolicyEnforcementDecisionRequired() {
        return policyEnforcementDecisionRequired;
    }

    public boolean isHidden() {
        return hidden;
    }

    public String getEulaURL() {
        return eulaURL;
    }

    public String getInterfedSource() {
        return interfedSource;
    }

    public String getPrivacyStatementUrlEn() {
        return privacyStatementUrlEn;
    }

    public String getPrivacyStatementUrlNl() {
        return privacyStatementUrlNl;
    }

    public String getRegistrationInfo() {
        return registrationInfo;
    }

    public String getRegistrationPolicyUrlEn() {
        return registrationPolicyUrlEn;
    }

    public String getRegistrationPolicyUrlNl() {
        return registrationPolicyUrlNl;
    }

    public Map<String, String> getUrls() {
        return urls;
    }

    public String getUrl(Language language) {
        return CollectionUtils.isEmpty(this.urls) ? null : urls.get(language.name().toLowerCase());
    }

    public String getWikiUrl(Language language) {
        return Language.EN.equals(language) ? this.wikiUrlEn : this.wikiUrlNl;
    }

    public PrivacyInfo getPrivacyInfo() {
        return privacyInfo;
    }

    public Map<String, String> getArpMotivations() {
        return arpMotivations;
    }

    private void addUrl(String lang, String url) {
        if (StringUtils.hasText(url)) {
            this.urls.put(lang, url);
        }
    }

    public ARP getArp() {
        return arp;
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public String getEntityCategories1() {
        return entityCategories1;
    }

    public String getEntityCategories2() {
        return entityCategories2;
    }

    @Override
    public String toString() {
        return "ServiceProvider{" +
            "id='" + getId() + '\'' +
            ", applicationUrl='" + applicationUrl + '\'' +
            ", institutionId='" + institutionId + '\'' +
            ", eulaURL='" + eulaURL + '\'' +
            ", idpVisibleOnly=" + idpVisibleOnly +
            ", policyEnforcementDecisionRequired=" + policyEnforcementDecisionRequired +
            ", entityType=" + entityType +
            ", arp=" + arp +
            ", urls=" + urls +
            '}';
    }

    public ServiceProvider clone() {
        try {
            return (ServiceProvider) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public LicenseStatus getLicenseStatus() {
        return licenseStatus;
    }

    public boolean isStrongAuthenticationEnabled() {
        return strongAuthenticationEnabled;
    }

    public String getMinimalLoaLevel() {
        return minimalLoaLevel;
    }

    public String getWikiUrlNl() {
        return wikiUrlNl;
    }

    public String getWikiUrlEn() {
        return wikiUrlEn;
    }

    public List<String> getTypeOfServicesNl() {
        return typeOfServicesNl;
    }

    public List<String> getTypeOfServicesEn() {
        return typeOfServicesEn;
    }

    public boolean isAansluitovereenkomstRefused() {
        return aansluitovereenkomstRefused;
    }

    public String getManipulationNotes() {
        return manipulationNotes;
    }

    public String getContractualBase() {
        return contractualBase;
    }

    public boolean isManipulation() {
        return manipulation;
    }


    public List<String> getNameIds() {
        return nameIds;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setDashboardConnectOption(DashboardConnectOption dashboardConnectOption) {
        this.dashboardConnectOption = dashboardConnectOption;
    }

    public DashboardConnectOption getDashboardConnectOption() {
        return dashboardConnectOption;
    }

    public List<String> getResourceServers() {
        return resourceServers;
    }
}
