package nl.surfnet.coin.selfservice.api.rest;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps responses and adds optional links.
 */
public class RestResponse<T> {
  public static class Link {
    private String href;

    public Link(String href) {
      this.href = href;
    }

    public String getHref() {
      return href;
    }
  }

  private Map<String, Link> links;
  private T payload;

  public RestResponse(T payload) {
    this.payload = payload;
    this.links = new HashMap<>();
  }

  public RestResponse<T> withSelfRel(String url) {
    this.links.put("self", new Link(url));
    return this;
  }

  @JsonProperty("_links")
  public Map<String, Link> getJsonLinks() {
    if(links.isEmpty()) {
      return null;
    } else {
      return links;
    }
  }

  public T getPayload() {
    return payload;
  }
}
