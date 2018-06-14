package selfservice.api.dashboard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import selfservice.shibboleth.ShibbolethHeader;
import selfservice.util.SpringSecurity;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;

public class GsonHttpMessageConverter extends AbstractHttpMessageConverter<RestResponse<?>> {

  public static final GsonBuilder GSON_BUILDER = new GsonBuilder()
    .setExclusionStrategies(new ExcludeJsonIgnore())
    .enableComplexMapKeySerialization()
    .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter().nullSafe())
    .registerTypeAdapter(ShibbolethHeader.class, new ShibbolethHeaderTypeAdapter().nullSafe());
  private boolean statsEnabled;

  private Gson gson;

  private String statsBaseUrl;
  private String statsAuthorizePath;
  private String statsClientId;
  private String statsScope;
  private String statsRedirectUri;

  public GsonHttpMessageConverter(String statsBaseUr, String statsAuthorizePath, String
      statsClientId, String statsScope, String statsRedirectUri, boolean statsEnabled) {
    this.gson = GSON_BUILDER.create();
    this.statsBaseUrl = statsBaseUr;
    this.statsAuthorizePath = statsAuthorizePath;
    this.statsClientId = statsClientId;
    this.statsScope = statsScope;
    this.statsRedirectUri = statsRedirectUri;
    this.statsEnabled = statsEnabled;
  }

  @Override
  public boolean canRead(Class<?> clazz, MediaType mediaType) {
    return false;
  }

  @Override
  public boolean canWrite(Class<?> clazz, MediaType mediaType) {
    return clazz.isAssignableFrom(RestResponse.class) && mediaType != null && mediaType.equals(MediaType
      .APPLICATION_JSON);
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
  protected RestResponse<?> readInternal(Class<? extends RestResponse<?>> clazz, HttpInputMessage inputMessage)
    throws IOException, HttpMessageNotReadableException {
    throw new UnsupportedOperationException("nyi");
  }

  @Override
  protected void writeInternal(RestResponse<?> objectRestResponse, HttpOutputMessage outputMessage) throws
    IOException, HttpMessageNotWritableException {
    JsonElement json = gson.toJsonTree(objectRestResponse);
    EnrichJson.forUser(
      statsEnabled,
      SpringSecurity.getCurrentUser(),
      format(
        "%s/%s?response_type=token&client_id=%s&scope=%s&redirect_uri=%s",
        statsBaseUrl,
        statsAuthorizePath,
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
    if (headers == null || headers.getContentType() == null || headers.getContentType().getCharset() == null) {
      return StandardCharsets.UTF_8;
    }
    return headers.getContentType().getCharset();
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

  private static final class ZonedDateTimeTypeAdapter extends TypeAdapter<ZonedDateTime> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Override
    public void write(JsonWriter out, ZonedDateTime value) throws IOException {
      out.value(DATE_FORMATTER.format(value));
    }

    @Override
    public ZonedDateTime read(JsonReader in) throws IOException {
      return ZonedDateTime.parse(in.nextString(), DATE_FORMATTER);
    }
  }

  private static final class ShibbolethHeaderTypeAdapter extends TypeAdapter<ShibbolethHeader> {
    @Override
    public void write(JsonWriter out, ShibbolethHeader value) throws IOException {
      ShibbolethHeader header = (ShibbolethHeader) value;
      out.value(header.getValue());
    }

    @Override
    public ShibbolethHeader read(JsonReader in) throws IOException {
      return ShibbolethHeader.findByValue(in.nextString());
    }
  }

}
