package nl.surfnet.coin.selfservice.api.rest;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

public class GsonHttpMessageConverter extends AbstractHttpMessageConverter<RestResponse> {
  private static class ExcludeJsonIgnore implements ExclusionStrategy {
    public boolean shouldSkipField(FieldAttributes f) {
      return f.getAnnotation(JsonIgnore.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
      return clazz.isAnnotationPresent(JsonIgnore.class);
    }
  }

  private Gson gson;

  public GsonHttpMessageConverter() {
    this.gson = new GsonBuilder().setExclusionStrategies(new ExcludeJsonIgnore()).create();
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
    AddRestLinks.to(json).forClass(objectRestResponse.getPayload().getClass());
    JsonWriter jsonWriter = new JsonWriter(new OutputStreamWriter(outputMessage.getBody(), "UTF-8"));
    try {
      gson.toJson(json, jsonWriter);
    } finally {
      jsonWriter.flush();
    }
  }
}
