package selfservice.domain;

import java.util.List;

import selfservice.domain.csa.ContactPerson;

public class Settings {
  private String keywordsNl;
  private String keywordsEn;
  private boolean publishedInEdugain;
  
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
  
}
