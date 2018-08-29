package selfservice.domain;

import java.util.List;

public class Settings {

    private List<ServiceProviderSettings> serviceProviderSettings;
    private String keywordsNl;
    private String keywordsEn;
    private String logoUrl;
    private String displayNamesEn;
    private String displayNamesNl;
    private String descriptionsEn;
    private String descriptionsNl;
    private boolean publishedInEdugain;
    private String comments;
    private StateType stateType;
    private ConsentType consentType;

    private List<ContactPerson> contactPersons;

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

    public boolean isPublishedInEdugain() {
        return publishedInEdugain;
    }

    public void setPublishedInEdugain(boolean publishedInEdugain) {
        this.publishedInEdugain = publishedInEdugain;
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
}
