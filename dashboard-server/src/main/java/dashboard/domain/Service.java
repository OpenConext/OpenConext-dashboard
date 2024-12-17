/*
 * Copyright 2013 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dashboard.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;
import dashboard.manage.DashboardConnectOption;
import dashboard.manage.EntityType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Service implements Comparable<Service>, Serializable {

    private static final long serialVersionUID = 0L;

    private long id;

    private String state;
    private String name;
    private String organisation;
    private String description;
    private String logoUrl;
    private String websiteUrl;
    private String appUrl;
    private String serviceUrl;
    private String detailLogoUrl;
    private String supportUrl;
    private String eulaUrl;
    private String wikiUrl;
    private String supportMail;
    private String enduserDescription;
    private String institutionDescription;
    private String institutionId;
    private String spEntityId;
    private String spName;
    private String interfedSource;
    private String privacyStatementUrl;
    private String registrationInfoUrl;
    private String registrationPolicyUrl;
    private String entityCategories1;
    private String entityCategories2;
    private String entityCategories3;
    private String publishInEdugainDate;
    private String manipulationNotes;
    private boolean manipulation;
    private String contractualBase;

    private List<String> screenshotUrls = new ArrayList<>();
    private List<Category> categories = new ArrayList<>();
    private Map<String, String> names = new HashMap<>();
    private Map<String, String> organisations = new HashMap<>();
    private Map<String, String> motivations = new HashMap<>();
    private Map<String, String> sources = new HashMap<>();
    private Map<String, String> descriptions = new HashMap<>();
    private Map<String, String> displayNames = new HashMap<>();

    private boolean connected;
    private boolean idpVisibleOnly;
    private Boolean normenkaderPresent;
    private boolean exampleSingleTenant;
    private boolean policyEnforcementDecisionRequired;
    private boolean strongAuthentication;
    private boolean noConsentRequired;
    private boolean aansluitovereenkomstRefused;
    private boolean guestEnabled;

    private PrivacyInfo privacyInfo;

    private LicenseStatus licenseStatus;

    private ARP arp;

    private List<ContactPerson> contactPersons;
    private List<String> nameIds;
    private String minimalLoaLevel;
    private EntityType entityType;
    private List<String> resourceServers;
    private boolean isResourceServer;
    private DashboardConnectOption dashboardConnectOption;

    public Service() {
    }

    public Service(long id, String name, String logoUrl, String websiteUrl, String spEntityId) {
        this.id = id;
        this.name = name;
        this.logoUrl = logoUrl;
        this.websiteUrl = websiteUrl;
        this.spEntityId = spEntityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public DashboardConnectOption getDashboardConnectOption() {
        return dashboardConnectOption;
    }

    public void setDashboardConnectOption(DashboardConnectOption dashboardConnectOption) {
        this.dashboardConnectOption = dashboardConnectOption;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getSpEntityId() {
        return spEntityId;
    }

    public void setSpEntityId(String spEntityId) {
        this.spEntityId = spEntityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getDetailLogoUrl() {
        return detailLogoUrl;
    }

    public void setDetailLogoUrl(String detailLogoUrl) {
        this.detailLogoUrl = detailLogoUrl;
    }

    public String getSupportUrl() {
        return supportUrl;
    }

    public void setSupportUrl(String supportUrl) {
        this.supportUrl = supportUrl;
    }

    public String getEulaUrl() {
        return eulaUrl;
    }

    public void setEulaUrl(String eulaUrl) {
        this.eulaUrl = eulaUrl;
    }

    public List<String> getScreenshotUrls() {
        return screenshotUrls;
    }

    public void setScreenshotUrls(List<String> screenshotUrls) {
        this.screenshotUrls = screenshotUrls;
    }

    public String getSupportMail() {
        return supportMail;
    }

    public void setSupportMail(String supportMail) {
        this.supportMail = supportMail;
    }

    public String getEnduserDescription() {
        return enduserDescription;
    }

    public void setEnduserDescription(String enduserDescription) {
        this.enduserDescription = enduserDescription;
    }

    public String getInstitutionDescription() {
        return institutionDescription;
    }

    public void setInstitutionDescription(String institutionDescription) {
        this.institutionDescription = institutionDescription;
    }

    public ARP getArp() {
        return arp;
    }

    public void setArp(ARP arp) {
        this.arp = arp;
    }

    public Map<String, String> getNames() {
        return names;
    }

    public void setNames(Map<String, String> names) {
        this.names = names;
    }

    public Map<String, String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(Map<String, String> descriptions) {
        this.descriptions = descriptions;
    }

    public Map<String, String> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(Map<String, String> displayNames) {
        this.displayNames = displayNames;
    }

    public boolean isIdpVisibleOnly() {
        return idpVisibleOnly;
    }

    public void setIdpVisibleOnly(boolean idpVisibleOnly) {
        this.idpVisibleOnly = idpVisibleOnly;
    }

    public boolean isPolicyEnforcementDecisionRequired() {
        return policyEnforcementDecisionRequired;
    }

    public void setPolicyEnforcementDecisionRequired(boolean policyEnforcementDecisionRequired) {
        this.policyEnforcementDecisionRequired = policyEnforcementDecisionRequired;
    }

    public String getInstitutionId() {
        return institutionId;
    }

    public void setInstitutionId(String institutionId) {
        this.institutionId = institutionId;
    }

    public String getSpName() {
        return spName;
    }

    public void setSpName(String spName) {
        this.spName = spName;
    }

    public String getWikiUrl() {
        return wikiUrl;
    }

    public void setWikiUrl(String wikiUrl) {
        this.wikiUrl = wikiUrl;
    }


    public void setPublishInEdugainDate(String publishInEdugainDate) {
        this.publishInEdugainDate = publishInEdugainDate;
    }

    public LicenseStatus getLicenseStatus() {
        return licenseStatus;
    }

    public void setLicenseStatus(LicenseStatus licenseStatus) {
        this.licenseStatus = licenseStatus;
    }

    public boolean isNormenkaderPresent() {
        return normenkaderPresent;
    }

    public boolean isExampleSingleTenant() {
        return exampleSingleTenant;
    }

    public void setExampleSingleTenant(boolean exampleSingleTenant) {
        this.exampleSingleTenant = exampleSingleTenant;
    }

    public String getInterfedSource() {
        return interfedSource;
    }

    public void setInterfedSource(String interfedSource) {
        this.interfedSource = interfedSource;
    }

    public String getPrivacyStatementUrl() {
        return privacyStatementUrl;
    }

    public void setPrivacyStatementUrl(String privacyStatementUrl) {
        this.privacyStatementUrl = privacyStatementUrl;
    }

    public String getRegistrationInfoUrl() {
        return registrationInfoUrl;
    }

    public void setRegistrationInfoUrl(String registrationInfo) {
        this.registrationInfoUrl = registrationInfo;
    }

    public String getRegistrationPolicyUrl() {
        return registrationPolicyUrl;
    }

    public void setRegistrationPolicyUrl(String registrationPolicyUrl) {
        this.registrationPolicyUrl = registrationPolicyUrl;
    }

    public String getEntityCategories1() {
        return entityCategories1;
    }

    public void setEntityCategories1(String entityCategories1) {
        this.entityCategories1 = entityCategories1;
    }

    public String getEntityCategories2() {
        return entityCategories2;
    }

    public void setEntityCategories2(String entityCategories2) {
        this.entityCategories2 = entityCategories2;
    }

    public String getEntityCategories3() {
        return entityCategories3;
    }

    public void setEntityCategories3(String entityCategories3) {
        this.entityCategories3 = entityCategories3;
    }

    public boolean isStrongAuthentication() {
        return strongAuthentication;
    }

    public void setStrongAuthentication(boolean strongAuthentication) {
        this.strongAuthentication = strongAuthentication;
    }

    public boolean isNoConsentRequired() {
        return noConsentRequired;
    }

    public void setNoConsentRequired(boolean noConsentRequired) {
        this.noConsentRequired = noConsentRequired;
    }

    public List<ContactPerson> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<ContactPerson> contactPersons) {
        this.contactPersons = contactPersons;
    }

    public PrivacyInfo getPrivacyInfo() {
        return privacyInfo;
    }

    public void setPrivacyInfo(PrivacyInfo privacyInfo) {
        this.privacyInfo = privacyInfo;
    }

    public Boolean getNormenkaderPresent() {
        return normenkaderPresent;
    }

    public void setNormenkaderPresent(Boolean normenkaderPresent) {
        this.normenkaderPresent = normenkaderPresent;
    }

    public Map<String, String> getMotivations() {
        return motivations;
    }

    public void setMotivations(Map<String, String> motivations) {
        this.motivations = motivations;
    }

    public Map<String, String> getSources() {
        return sources;
    }

    public void setSources(Map<String, String> sources) {
        this.sources = sources;
    }

    public boolean isAansluitovereenkomstRefused() {
        return aansluitovereenkomstRefused;
    }

    public void setAansluitovereenkomstRefused(boolean aansluitovereenkomstRefused) {
        this.aansluitovereenkomstRefused = aansluitovereenkomstRefused;
    }

    public boolean isGuestEnabled() {
        return guestEnabled;
    }

    public void setGuestEnabled(boolean guestEnabled) {
        this.guestEnabled = guestEnabled;
    }

    public String getManipulationNotes() {
        return manipulationNotes;
    }

    public void setManipulationNotes(String manipulationNotes) {
        this.manipulationNotes = manipulationNotes;
    }

    public boolean isManipulation() {
        return manipulation;
    }

    public void setManipulation(boolean manipulation) {
        this.manipulation = manipulation;
    }

    public String getContractualBase() {
        return contractualBase;
    }

    public void setContractualBase(String contractualBase) {
        this.contractualBase = contractualBase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Service service = (Service) o;

        if (id != service.id) return false;
        if (name != null ? !name.equals(service.name) : service.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(Service other) {
        if (other == null) {
            return 1;
        }
        String otherName = other.getName();
        if (this.name == null && otherName == null) {
            return -1;
        }
        if (this.name == null) {
            return -1;
        }
        if (otherName == null) {
            return 1;
        }
        return this.name.compareTo(otherName);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("name", name).toString();
    }

    public void setNameIds(List<String> nameIds) {
        this.nameIds = nameIds;
    }

    public List<String> getNameIds() {
        return nameIds;
    }

    public String getMinimalLoaLevel() {
        return minimalLoaLevel;
    }

    public void setMinimalLoaLevel(String minimalLoaLevel) {
        this.minimalLoaLevel = minimalLoaLevel;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setResourceServers(List<String> resourceServers) {
        this.resourceServers = resourceServers;
    }

    public List<String> getResourceServers() {
        return resourceServers;
    }

    public boolean isResourceServer() {
        return isResourceServer;
    }

    public void setResourceServer(boolean resourceServer) {
        isResourceServer = resourceServer;
    }

    public boolean connectsWithoutInteraction() {
        if (dashboardConnectOption == null) {
            return false;
        }
        return dashboardConnectOption.connectsWithoutInteraction();
    }

    public boolean sendsEmailWithoutInteraction() {
        if (dashboardConnectOption == null) {
            return false;
        }
        return dashboardConnectOption.sendsEmail();
    }


    public Map<String, String> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(Map<String, String> organisations) {
        this.organisations = organisations;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }
}
