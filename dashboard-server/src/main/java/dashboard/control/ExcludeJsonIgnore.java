package dashboard.control;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class ExcludeJsonIgnore implements ExclusionStrategy {
  public boolean shouldSkipField(FieldAttributes f) {
    return f.getAnnotation(JsonIgnore.class) != null;
  }

  @Override
  public boolean shouldSkipClass(Class<?> clazz) {
    return clazz.isAnnotationPresent(JsonIgnore.class);
  }
}
