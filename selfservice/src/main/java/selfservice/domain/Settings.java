package selfservice.domain;

import java.util.List;

public class Settings {

  private String keywordsNl;
  private String keywordsEn;
  private boolean publishedInEdugain;
  private String comments;
  private ConsentType consentType;
  
  private List<ContactPerson> contactPersons;
  private List<ServiceProviderSettings> serviceProviderSettings;
  
  public Settings() {
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
  
  public boolean isPublishedInEdugain() {
    return publishedInEdugain;
  }
  
  public void setPublishedInEdugain(boolean publishedInEdugain) {
    this.publishedInEdugain = publishedInEdugain;
  }

  public List<ContactPerson> getContactPersons() {
    return contactPersons;
  }

  public void setContactPersons(List<ContactPerson> contactPersons) {
    this.contactPersons = contactPersons;
  }

  public List<ServiceProviderSettings> getServiceProviderSettings() {
    return serviceProviderSettings;
  }

  public void setServiceProviderSettings(List<ServiceProviderSettings> serviceProviderSettings) {
    this.serviceProviderSettings = serviceProviderSettings;
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
}
