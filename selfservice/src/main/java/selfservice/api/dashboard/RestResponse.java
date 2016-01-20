package selfservice.api.dashboard;

import java.util.Locale;

/**
 * Wraps responses and adds optional links.
 */
public class RestResponse<T> {

  private T payload;
  private String language;

  public RestResponse(Locale locale, T payload) {
    this.payload = payload;
    this.language = locale.getLanguage();
  }

  public T getPayload() {
    return payload;
  }

  public String getLanguage() {
    return language;
  }
}
