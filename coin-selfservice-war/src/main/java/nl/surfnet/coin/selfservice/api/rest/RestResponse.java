package nl.surfnet.coin.selfservice.api.rest;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonUnwrapped;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps responses and adds optional links.
 */
public class RestResponse<T> {

  private T payload;

  public RestResponse(T payload) {
    this.payload = payload;
  }

  public T getPayload() {
    return payload;
  }
}
