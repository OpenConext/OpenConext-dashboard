package selfservice.domain;

public class ServiceProviderSettings {
  private String spEntityId;
  private boolean hasGuestEnabled;
  private boolean noConsentRequired;
  private boolean publishedInEdugain;
  
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
  
  
}
