package selfservice.api.rest;

import static java.lang.String.format;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import selfservice.util.SpringSecurity;

public class GsonHttpMessageConverter extends AbstractHttpMessageConverter<RestResponse> {

  public static final GsonBuilder GSON_BUILDER = new GsonBuilder().setExclusionStrategies(new ExcludeJsonIgnore());

  private Gson gson;

  private String statsBaseUrl;
  private String statsClientId;
  private String statsScope;
  private String statsRedirectUri;

  public GsonHttpMessageConverter(String statsBaseUr, String statsClientId, String statsScope, String statsRedirectUri) {
    this.gson = GSON_BUILDER.create();
    this.statsBaseUrl = statsBaseUr;
    this.statsClientId = statsClientId;
    this.statsScope = statsScope;
    this.statsRedirectUri = statsRedirectUri;
  }

  @Override
  public boolean canRead(Class<?> clazz, MediaType mediaType) {
    return false;
  }

  @Override
  public boolean canWrite(Class<?> clazz, MediaType mediaType) {
    return clazz.isAssignableFrom(RestResponse.class) && mediaType.equals(MediaType.APPLICATION_JSON);
  }

  @Override
  public List<MediaType> getSupportedMediaTypes() {
    return Arrays.asList(MediaType.APPLICATION_JSON);
  }

  @Override
  protected boolean supports(Class<?> clazz) {
    return false;
  }

  @Override
  protected RestResponse readInternal(Class<? extends RestResponse> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
    throw new UnsupportedOperationException("nyi");
  }

  @Override
  protected void writeInternal(RestResponse objectRestResponse, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
    JsonElement json = gson.toJsonTree(objectRestResponse);
    EnrichJson.forUser(
        SpringSecurity.getCurrentUser(),
        format(
            "%s/oauth/authorize.php?response_type=token&client_id=%s&scope=%s&redirect_uri=%s",
            statsBaseUrl,
            statsClientId,
            statsScope,
            statsRedirectUri
            )
        )
        .json(json)
        .forPayload(objectRestResponse.getPayload());

    Charset charset = getCharset(outputMessage.getHeaders());

    try (OutputStreamWriter jsonWriter = new OutputStreamWriter(outputMessage.getBody(), charset)) {
      gson.toJson(json, jsonWriter);
    } catch (JsonIOException e) {
      throw new HttpMessageNotWritableException("Could not write JSON: " + e.getMessage(), e);
    }
  }

  private Charset getCharset(HttpHeaders headers) {
    if (headers == null || headers.getContentType() == null || headers.getContentType().getCharSet() == null) {
      return StandardCharsets.UTF_8;
    }
    return headers.getContentType().getCharSet();
  }

  public void setStatsBaseUrl(String statsBaseUrl) {
    this.statsBaseUrl = statsBaseUrl;
  }

  public void setStatsClientId(String statsClientId) {
    this.statsClientId = statsClientId;
  }

  public void setStatsScope(String statsScope) {
    this.statsScope = statsScope;
  }

  public void setStatsRedirectUri(String statsRedirectUri) {
    this.statsRedirectUri = statsRedirectUri;
  }
}
