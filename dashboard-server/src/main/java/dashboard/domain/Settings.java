package dashboard.domain;

import java.util.List;

public class Settings {

    private List<ServiceProviderSettings> serviceProviderSettings;
    private String keywordsNl;
    private String keywordsEn;
    private String keywordsPt;
    private String logoUrl;
    private String displayNamesEn;
    private String displayNamesNl;
    private String displayNamesPt;
    private String descriptionsEn;
    private String descriptionsNl;
    private String descriptionsPt;
    private String organisationUrlEn;
    private String organisationUrlNl;
    private String organisationUrlPt;
    private String organisationNameEn;
    private String organisationNameNl;
    private String organisationNamePt;
    private String organisationDisplayNameEn;
    private String organisationDisplayNameNl;
    private String organisationDisplayNamePt;
    private boolean publishedInEdugain;
    private boolean connectToRSServicesAutomatically;
    private boolean allowMaintainersToManageAuthzRules;
    private String comments;
    private StateType stateType;
    private ConsentType consentType;

    private List<ContactPerson> contactPersons;
    private boolean displayAdminEmailsInDashboard;
    private boolean displayStatsInDashboard;

    public Settings() {
    }

    public List<ServiceProviderSettings> getServiceProviderSettings() {
        return serviceProviderSettings;
    }

    public void setServiceProviderSettings(List<ServiceProviderSettings> serviceProviderSettings) {
        this.serviceProviderSettings = serviceProviderSettings;
    }

    public String getKeywordsNl() {
        return keywordsNl;
    }

    public void setKeywordsNl(String keywordsNl) {
        this.keywordsNl = keywordsNl;
    }

    public String getKeywordsPt() {
        return keywordsPt;
    }

    public void setKeywordsPt(String keywordsPt) {
        this.keywordsPt = keywordsPt;
    }

    public String getKeywordsEn() {
        return keywordsEn;
    }

    public void setKeywordsEn(String keywordsEn) {
        this.keywordsEn = keywordsEn;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getDisplayNamesEn() {
        return displayNamesEn;
    }

    public void setDisplayNamesEn(String displayNamesEn) {
        this.displayNamesEn = displayNamesEn;
    }

    public String getDisplayNamesNl() {
        return displayNamesNl;
    }

    public void setDisplayNamesNl(String displayNamesNl) {
        this.displayNamesNl = displayNamesNl;
    }

    public String getDisplayNamesPt() {
        return displayNamesPt;
    }

    public void setDisplayNamesPt(String displayNamesPt) {
        this.displayNamesPt = displayNamesPt;
    }

    public String getDescriptionsEn() {
        return descriptionsEn;
    }

    public void setDescriptionsEn(String descriptionsEn) {
        this.descriptionsEn = descriptionsEn;
    }

    public String getDescriptionsNl() {
        return descriptionsNl;
    }

    public void setDescriptionsNl(String descriptionsNl) {
        this.descriptionsNl = descriptionsNl;
    }

    public String getDescriptionsPt() {
        return descriptionsPt;
    }

    public void setDescriptionsPt(String descriptionsPt) {
        this.descriptionsPt = descriptionsPt;
    }

    public boolean isPublishedInEdugain() {
        return publishedInEdugain;
    }

    public void setPublishedInEdugain(boolean publishedInEdugain) {
        this.publishedInEdugain = publishedInEdugain;
    }

    public boolean isConnectToRSServicesAutomatically() {
        return connectToRSServicesAutomatically;
    }

    public void setConnectToRSServicesAutomatically(boolean connectToRSServicesAutomatically) {
        this.connectToRSServicesAutomatically = connectToRSServicesAutomatically;
    }

    public boolean isAllowMaintainersToManageAuthzRules() {
        return allowMaintainersToManageAuthzRules;
    }

    public void setAllowMaintainersToManageAuthzRules(boolean allowMaintainersToManageAuthzRules) {
        this.allowMaintainersToManageAuthzRules = allowMaintainersToManageAuthzRules;
    }

    public boolean isDisplayAdminEmailsInDashboard() {
        return displayAdminEmailsInDashboard;
    }

    public void setDisplayAdminEmailsInDashboard(boolean displayAdminEmailsInDashboard) {
        this.displayAdminEmailsInDashboard = displayAdminEmailsInDashboard;
    }

    public boolean isDisplayStatsInDashboard() {
        return displayStatsInDashboard;
    }

    public void setDisplayStatsInDashboard(boolean displayStatsInDashboard) {
        this.displayStatsInDashboard = displayStatsInDashboard;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public ConsentType getConsentType() {
        return consentType;
    }

    public void setConsentType(ConsentType consentType) {
        this.consentType = consentType;
    }

    public StateType getStateType() {
        return stateType;
    }

    public void setStateType(StateType stateType) {
        this.stateType = stateType;
    }

    public List<ContactPerson> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<ContactPerson> contactPersons) {
        this.contactPersons = contactPersons;
    }

    public String getOrganisationUrlEn() {
        return organisationUrlEn;
    }

    public void setOrganisationUrlEn(String organisationUrlEn) {
        this.organisationUrlEn = organisationUrlEn;
    }

    public String getOrganisationUrlNl() {
        return organisationUrlNl;
    }

    public void setOrganisationUrlNl(String organisationUrlNl) {
        this.organisationUrlNl = organisationUrlNl;
    }

    public String getOrganisationUrlPt() {
        return organisationUrlPt;
    }

    public void setOrganisationUrlPt(String organisationUrlPt) {
        this.organisationUrlPt = organisationUrlPt;
    }

    public String getOrganisationNameEn() {
        return organisationNameEn;
    }

    public void setOrganisationNameEn(String organisationNameEn) {
        this.organisationNameEn = organisationNameEn;
    }

    public String getOrganisationNameNl() {
        return organisationNameNl;
    }

    public void setOrganisationNameNl(String organisationNameNl) {
        this.organisationNameNl = organisationNameNl;
    }

    public String getOrganisationNamePt() {
        return organisationNamePt;
    }

    public void setOrganisationNamePt(String organisationNamePt) {
        this.organisationNamePt = organisationNamePt;
    }

    public String getOrganisationDisplayNameEn() {
        return organisationDisplayNameEn;
    }

    public void setOrganisationDisplayNameEn(String organisationDisplayNameEn) {
        this.organisationDisplayNameEn = organisationDisplayNameEn;
    }

    public String getOrganisationDisplayNameNl() {
        return organisationDisplayNameNl;
    }

    public void setOrganisationDisplayNameNl(String organisationDisplayNameNl) {
        this.organisationDisplayNameNl = organisationDisplayNameNl;
    }

    public String getOrganisationDisplayNamePt() {
        return organisationDisplayNamePt;
    }

    public void setOrganisationDisplayNamePt(String organisationDisplayNamePt) {
        this.organisationDisplayNamePt = organisationDisplayNamePt;
    }

    @Override
    public String toString() {
        return "Settings{" +
                "serviceProviderSettings=" + serviceProviderSettings +
                ", keywordsNl='" + keywordsNl + '\'' +
                ", keywordsEn='" + keywordsEn + '\'' +
                ", keywordsPt='" + keywordsPt + '\'' +
                ", logoUrl='" + logoUrl + '\'' +
                ", displayNamesEn='" + displayNamesEn + '\'' +
                ", displayNamesNl='" + displayNamesNl + '\'' +
                ", displayNamesPt='" + displayNamesPt + '\'' +
                ", descriptionsEn='" + descriptionsEn + '\'' +
                ", descriptionsNl='" + descriptionsNl + '\'' +
                ", descriptionsPt='" + descriptionsPt + '\'' +
                ", organisationUrlEn='" + organisationUrlEn + '\'' +
                ", organisationUrlNl='" + organisationUrlNl + '\'' +
                ", organisationUrlPt='" + organisationUrlPt + '\'' +
                ", publishedInEdugain=" + publishedInEdugain +
                ", connectToRSServicesAutomatically=" + connectToRSServicesAutomatically +
                ", allowMaintainersToManageAuthzRules=" + allowMaintainersToManageAuthzRules +
                ", comments='" + comments + '\'' +
                ", stateType=" + stateType +
                ", consentType=" + consentType +
                ", contactPersons=" + contactPersons +
                '}';
    }

}
