package dashboard.domain;

import java.util.List;

public class ServiceProviderSettings {

    private String spEntityId;
    private boolean hasGuestEnabled;
    private boolean noConsentRequired;
    private boolean publishedInEdugain;
    private String displayNameEn;
    private String displayNameNl;
    private String descriptionEn;
    private String descriptionNl;
    private List<ContactPerson> contactPersons;
    private StateType stateType;

    public ServiceProviderSettings() {
    }

    public String getSpEntityId() {
        return spEntityId;
    }

    public void setSpEntityId(String spEntityId) {
        this.spEntityId = spEntityId;
    }

    public boolean isHasGuestEnabled() {
        return hasGuestEnabled;
    }

    public void setHasGuestEnabled(boolean hasGuestEnabled) {
        this.hasGuestEnabled = hasGuestEnabled;
    }

    public boolean isNoConsentRequired() {
        return noConsentRequired;
    }

    public void setNoConsentRequired(boolean noConsentRequired) {
        this.noConsentRequired = noConsentRequired;
    }

    public boolean isPublishedInEdugain() {
        return publishedInEdugain;
    }

    public void setPublishedInEdugain(boolean publishedInEdugain) {
        this.publishedInEdugain = publishedInEdugain;
    }

    public String getDisplayNameEn() {
        return displayNameEn;
    }

    public void setDisplayNameEn(String displayNameEn) {
        this.displayNameEn = displayNameEn;
    }

    public String getDisplayNameNl() {
        return displayNameNl;
    }

    public void setDisplayNameNl(String displayNameNl) {
        this.displayNameNl = displayNameNl;
    }

    public String getDescriptionEn() {
        return descriptionEn;
    }

    public void setDescriptionEn(String descriptionEn) {
        this.descriptionEn = descriptionEn;
    }

    public String getDescriptionNl() {
        return descriptionNl;
    }

    public void setDescriptionNl(String descriptionNl) {
        this.descriptionNl = descriptionNl;
    }

    public List<ContactPerson> getContactPersons() {
        return contactPersons;
    }

    public void setContactPersons(List<ContactPerson> contactPersons) {
        this.contactPersons = contactPersons;
    }

    public StateType getStateType() {
        return stateType;
    }

    public void setStateType(StateType stateType) {
        this.stateType = stateType;
    }
}
