package nl.surfnet.coin.selfservice.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class PublicService implements Comparable<PublicService> {

  private String name;
  @JsonProperty("logo_url_service")
  private String logoUrl;
  @JsonProperty("website_service")
  private String websiteUrl;
  @JsonProperty("is_surfmarket_connected")
  private boolean hasCrmLink;

  public PublicService(String name, String logoUrl, String websiteUrl, boolean hasCrmLink) {
    this.name = name;
    this.logoUrl = logoUrl;
    this.websiteUrl = websiteUrl;
    this.hasCrmLink = hasCrmLink;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  public boolean isHasCrmLink() {
    return hasCrmLink;
  }

  public void setHasCrmLink(boolean hasCrmLink) {
    this.hasCrmLink = hasCrmLink;
  }


  @Override
  public int compareTo(PublicService other) {
    if (other == null) {
      return 1;
    }
    String otherName =  other.getName();
    if (this.name == null && otherName == null ) {
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
}
