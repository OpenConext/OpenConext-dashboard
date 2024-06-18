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
import lombok.Getter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;


@Getter
public class ServiceProvider extends Provider implements Serializable, Cloneable {

    private static final List<String> nameIdFormats = Arrays.asList("NameIDFormat", "NameIDFormats:0", "NameIDFormats:1", "NameIDFormats:2");

    private final Map<String, String> arpMotivations;
    private final Map<String, String> arpSources;
    private final String applicationUrl;
    private final String institutionId;
    private final String eulaURL;
    private final String interfedSource;
    private final String privacyStatementUrlEn;
    private final String privacyStatementUrlNl;
    private final String privacyStatementUrlPt;
    private final String registrationInfo;
    private final String registrationPolicyUrlEn;
    private final String registrationPolicyUrlNl;
    private final String registrationPolicyUrlPt;
    private final String entityCategories1;
    private final String entityCategories2;
    private final String entityCategories3;
    private final boolean idpVisibleOnly;
    private final boolean policyEnforcementDecisionRequired;
    private final DashboardConnectOption dashboardConnectOption;
    private final boolean aansluitovereenkomstRefused;
    private final boolean hidden;
    private final LicenseStatus licenseStatus;
    private final List<String> nameIds;
    private final List<String> resourceServers;
    private final boolean isResourceServer;
    private boolean isClientCredentials;
    private final boolean excludeFromPush;

    private final ARP arp;
    private final PrivacyInfo privacyInfo;

    private final Map<String, String> urls = new HashMap<>();
    private final String wikiUrlNl;
    private final String wikiUrlEn;
    private final String wikiUrlPt;
    private final boolean strongAuthenticationEnabled;
    private final String minimalLoaLevel;

    private List<String> typeOfServicesNl = new ArrayList<>();
    private List<String> typeOfServicesEn = new ArrayList<>();
    private List<String> typeOfServicesPt = new ArrayList<>();

    private final String manipulationNotes;
    private final boolean manipulation;
    private final String contractualBase;

    @SuppressWarnings("unchecked")
    public ServiceProvider(Map<String, Object> metaData) {
        super(metaData);
        this.dashboardConnectOption = DashboardConnectOption.fromOption((String) metaData.getOrDefault("coin:dashboard_connect_option", "connect_with_interaction"));
        this.applicationUrl = (String) metaData.get("coin:application_url");
        this.institutionId = (String) metaData.get("coin:institution_guid");
        this.eulaURL = (String) metaData.get("coin:eula");
        this.interfedSource = (String) metaData.getOrDefault("coin:interfed_source", "SURFconext");
        this.privacyStatementUrlEn = (String) metaData.get("mdui:PrivacyStatementURL:en");
        this.privacyStatementUrlNl = (String) metaData.get("mdui:PrivacyStatementURL:nl");
        this.privacyStatementUrlPt = (String) metaData.get("mdui:PrivacyStatementURL:pt");
        this.registrationInfo = (String) metaData.get("mdrpi:RegistrationInfo");
        this.registrationPolicyUrlEn = (String) metaData.get("mdrpi:RegistrationPolicy:en");
        this.registrationPolicyUrlNl = (String) metaData.get("mdrpi:RegistrationPolicy:nl");
        this.registrationPolicyUrlPt = (String) metaData.get("mdrpi:RegistrationPolicy:pt");
        this.entityCategories1 = (String) metaData.get("coin:entity_categories:1");
        this.entityCategories2 = (String) metaData.get("coin:entity_categories:2");
        this.entityCategories3 = (String) metaData.get("coin:entity_categories:3");
        this.licenseStatus = LicenseStatus.fromManage((String) metaData.get("coin:ss:license_status"));
        this.idpVisibleOnly = booleanValue(metaData.get("coin:ss:idp_visible_only"));
        this.policyEnforcementDecisionRequired = booleanValue(metaData.get
                ("coin:policy_enforcement_decision_required"));
        this.minimalLoaLevel = (String) metaData.get("coin:stepup:requireloa");
        this.strongAuthenticationEnabled = StringUtils.hasText(this.minimalLoaLevel);
        this.aansluitovereenkomstRefused = booleanValue(metaData.get("coin:ss:aansluitovereenkomst_refused"));
        this.excludeFromPush = booleanValue(metaData.get("coin:exclude_from_push"));
        this.hidden = booleanValue(metaData.get("coin:ss:hidden"));
        this.wikiUrlEn = (String) metaData.get("coin:ss:wiki_url:en");
        this.wikiUrlNl = (String) metaData.get("coin:ss:wiki_url:nl");
        this.wikiUrlPt = (String) metaData.get("coin:ss:wiki_url:pt");
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
        typeOfService = (String) metaData.get("coin:ss:type_of_service:pt");
        if (StringUtils.hasText(typeOfService)) {
            this.typeOfServicesPt = Arrays.asList(typeOfService.split(","));
        }

        addUrl("en", (String) metaData.get("url:en"));
        addUrl("nl", (String) metaData.get("url:nl"));
        addUrl("pt", (String) metaData.get("url:pt"));

        this.privacyInfo = this.buildPrivacyInfo(metaData);
        this.arpMotivations = (Map<String, String>) metaData.get("motivations");
        this.arpSources = (Map<String, String>) metaData.get("sources");
        this.manipulationNotes = (String) metaData.get("manipulationNotes");
        this.manipulation = StringUtils.hasText((String) metaData.get("manipulation"));
        this.contractualBase = (String) metaData.getOrDefault("coin:contractual_base", "NA");

        this.nameIds = nameIdFormats.stream()
                .filter(metaData::containsKey)
                .map(nameId -> (String) metaData.get(nameId))
                .collect(Collectors.toList());

        this.resourceServers = (List<String>) metaData.get("allowedResourceServers");
        this.isResourceServer = booleanValue(metaData.get("isResourceServer"));
        List<String> grants = (List<String>) metaData.get("grants");
        if (!CollectionUtils.isEmpty(grants) && grants.size() == 1 && grants.get(0).equals("client_credentials")) {
            this.isClientCredentials = true;
        }

    }

    private PrivacyInfo buildPrivacyInfo(Map<String, Object> metaData) {
        return new PrivacyInfo(
                (String) metaData.get("coin:privacy:what_data"),
                (String) metaData.get("coin:privacy:country"),
                (String) metaData.get("coin:privacy:access_data"),
                (String) metaData.get("coin:privacy:security_measures"),
                (String) metaData.get("mdui:PrivacyStatementURL:en"),
                (String) metaData.get("mdui:PrivacyStatementURL:nl"),
                (String) metaData.get("coin:privacy:dpa_type"),
                (String) metaData.get("coin:privacy:other_info")
        );

    }

    public String getUrl(Language language) {
        return CollectionUtils.isEmpty(this.urls) ? null : urls.get(language.name().toLowerCase());
    }

    public String getWikiUrl(Language language) {
        return Language.EN.equals(language) ? this.wikiUrlEn : Language.PT.equals(language) ? this.wikiUrlPt : this.wikiUrlNl;
    }

    private void addUrl(String lang, String url) {
        if (StringUtils.hasText(url)) {
            this.urls.put(lang, url);
        }
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
                ", entityType=" + getEntityType() +
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

}
