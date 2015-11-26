package selfservice.api.rest;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import org.codehaus.jackson.annotate.JsonIgnore;

public class ExcludeJsonIgnore implements ExclusionStrategy {
  public boolean shouldSkipField(FieldAttributes f) {
    return f.getAnnotation(JsonIgnore.class) != null;
  }

  @Override
  public boolean shouldSkipClass(Class<?> clazz) {
    return clazz.isAnnotationPresent(JsonIgnore.class);
  }
}
