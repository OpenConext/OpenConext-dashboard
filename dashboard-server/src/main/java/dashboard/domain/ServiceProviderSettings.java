package dashboard.domain;

import java.util.List;

public class ServiceProviderSettings {

    private String spEntityId;
    private boolean hasGuestEnabled;
    private boolean noConsentRequired;
    private String displayNameEn;
    private String displayNameNl;
    private String displayNamePt;
    private String descriptionEn;
    private String descriptionNl;
    private String descriptionPt;
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

    public String getDisplayNamePt() {
        return displayNamePt;
    }

    public void setDisplayNamePt(String displayNamePt) {
        this.displayNamePt = displayNamePt;
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

    public String getDescriptionPt() {
        return descriptionPt;
    }

    public void setDescriptionPt(String descriptionPt) {
        this.descriptionPt = descriptionPt;
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

    @Override
    public String toString() {
        return "ServiceProviderSettings{" +
                "spEntityId='" + spEntityId + '\'' +
                ", hasGuestEnabled=" + hasGuestEnabled +
                ", noConsentRequired=" + noConsentRequired +
                ", displayNameEn='" + displayNameEn + '\'' +
                ", displayNameNl='" + displayNameNl + '\'' +
                ", displayNamePt='" + displayNamePt + '\'' +
                ", descriptionEn='" + descriptionEn + '\'' +
                ", descriptionNl='" + descriptionNl + '\'' +
                ", descriptionPt='" + descriptionPt + '\'' +
                ", contactPersons=" + contactPersons +
                ", stateType=" + stateType +
                '}';
    }
}
