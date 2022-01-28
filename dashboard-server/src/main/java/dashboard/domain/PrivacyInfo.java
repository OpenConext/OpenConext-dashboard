package dashboard.domain;

import java.io.Serializable;

public class PrivacyInfo implements Serializable {

    private String accessData;
    private Boolean certification;
    private String certificationLocation;
    private String country;
    private String otherInfo;
    private Boolean privacyPolicy;
    private String privacyPolicyUrl;
    private String securityMeasures;
    private String snDpaWhyNot;
    private Boolean surfmarketDpaAgreement;
    private Boolean surfnetDpaAgreement;
    private String whatData;
    private Boolean aoRefused;
    private String certificationValidFrom;
    private String certificationValidTo;
    private Boolean gdprIsInWiki;

    public PrivacyInfo(String accessData, Boolean certification, String certificationLocation, String country, String
            otherInfo, Boolean privacyPolicy, String privacyPolicyUrl, String securityMeasures, String snDpaWhyNot, Boolean
                               surfmarketDpaAgreement, Boolean surfnetDpaAgreement, String whatData, Boolean aoRefused, String
                               certificationValidFrom, String certificationValidTo, Boolean gdprIsInWiki) {
        this.accessData = accessData;
        this.certification = certification;
        this.certificationLocation = certificationLocation;
        this.country = country;
        this.otherInfo = otherInfo;
        this.privacyPolicy = privacyPolicy;
        this.privacyPolicyUrl = privacyPolicyUrl;
        this.securityMeasures = securityMeasures;
        this.snDpaWhyNot = snDpaWhyNot;
        this.surfmarketDpaAgreement = surfmarketDpaAgreement;
        this.surfnetDpaAgreement = surfnetDpaAgreement;
        this.whatData = whatData;
        this.aoRefused = aoRefused;
        this.certificationValidFrom = certificationValidFrom;
        this.certificationValidTo = certificationValidTo;
        this.gdprIsInWiki = gdprIsInWiki;
    }

    public String getAccessData() {
        return accessData;
    }

    public void setAccessData(String accessData) {
        this.accessData = accessData;
    }

    public Boolean isCertification() {
        return certification;
    }

    public void setCertification(Boolean certification) {
        this.certification = certification;
    }

    public String getCertificationLocation() {
        return certificationLocation;
    }

    public void setCertificationLocation(String certificationLocation) {
        this.certificationLocation = certificationLocation;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public Boolean isPrivacyPolicy() {
        return privacyPolicy;
    }

    public void setPrivacyPolicy(Boolean privacyPolicy) {
        this.privacyPolicy = privacyPolicy;
    }

    public String getPrivacyPolicyUrl() {
        return privacyPolicyUrl;
    }

    public void setPrivacyPolicyUrl(String privacyPolicyUrl) {
        this.privacyPolicyUrl = privacyPolicyUrl;
    }

    public String getSecurityMeasures() {
        return securityMeasures;
    }

    public void setSecurityMeasures(String securityMeasures) {
        this.securityMeasures = securityMeasures;
    }

    public String getSnDpaWhyNot() {
        return snDpaWhyNot;
    }

    public void setSnDpaWhyNot(String snDpaWhyNot) {
        this.snDpaWhyNot = snDpaWhyNot;
    }

    public Boolean isSurfmarketDpaAgreement() {
        return surfmarketDpaAgreement;
    }

    public void setSurfmarketDpaAgreement(Boolean surfmarketDpaAgreement) {
        this.surfmarketDpaAgreement = surfmarketDpaAgreement;
    }

    public Boolean isSurfnetDpaAgreement() {
        return surfnetDpaAgreement;
    }

    public void setSurfnetDpaAgreement(Boolean surfnetDpaAgreement) {
        this.surfnetDpaAgreement = surfnetDpaAgreement;
    }

    public String getWhatData() {
        return whatData;
    }

    public void setWhatData(String whatData) {
        this.whatData = whatData;
    }

    public Boolean isAoRefused() {
        return aoRefused;
    }

    public void setAoRefused(Boolean aoRefused) {
        this.aoRefused = aoRefused;
    }

    public String getCertificationValidFrom() {
        return certificationValidFrom;
    }

    public void setCertificationValidFrom(String certificationValidFrom) {
        this.certificationValidFrom = certificationValidFrom;
    }

    public String getCertificationValidTo() {
        return certificationValidTo;
    }

    public void setCertificationValidTo(String certificationValidTo) {
        this.certificationValidTo = certificationValidTo;
    }

    public Boolean isGdprIsInWiki() {
        return gdprIsInWiki;
    }

    public void setGdprIsInWiki(Boolean gdprIsInWiki) {
        this.gdprIsInWiki = gdprIsInWiki;
    }
}
