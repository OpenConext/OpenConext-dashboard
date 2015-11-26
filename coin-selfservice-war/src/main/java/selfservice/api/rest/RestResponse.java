package selfservice.api.rest;

import java.util.Locale;

/**
 * Wraps responses and adds optional links.
 */
public class RestResponse {

  private Object payload;

  private String language;

  public RestResponse(Locale locale, Object payload) {
    this.payload = payload;
    this.language = locale.getLanguage();
  }

  public Object getPayload() {
    return payload;
  }

  public String getLanguage() {
    return language;
  }
}
