package csa.model;

import java.io.Serializable;

public class CrmArticle implements Serializable {

  private static final long serialVersionUID = 0L;

  private String guid;

  private String appleAppStoreUrl;
  private String androidPlayStoreUrl;

  public String getAndroidPlayStoreUrl() {
    return androidPlayStoreUrl;
  }

  public void setAndroidPlayStoreUrl(String androidPlayStoreUrl) {
    this.androidPlayStoreUrl = androidPlayStoreUrl;
  }

  public String getAppleAppStoreUrl() {
    return appleAppStoreUrl;
  }

  public void setAppleAppStoreUrl(String appleAppStoreUrl) {
    this.appleAppStoreUrl = appleAppStoreUrl;
  }

  public String getGuid() {
    return guid;
  }

  public void setGuid(String guid) {
    this.guid = guid;
  }
}
