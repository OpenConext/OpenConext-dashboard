package csa.shibboleth;

/**
 * Lists the http header-names under which Shibboleth makes SAML attributes available on the HttpServletRequest
 */
public enum ShibbolethRequestHeaders {

  UID("name-id"),
  DISPLAY_NAME("shib-displayname"),
  EMAIL("shib-email"),
  IDP_ID("shib-identity-provider");

  private final String headerName;

  public String getHeaderName() {
    return headerName;
  }

  ShibbolethRequestHeaders(String headerName) {
    this.headerName = headerName;
  }
}
