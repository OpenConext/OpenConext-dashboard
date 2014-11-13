package nl.surfnet.coin.selfservice.api.rest;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonUnwrapped;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
