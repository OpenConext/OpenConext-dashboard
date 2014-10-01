package nl.surfnet.coin.selfservice.util;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Maps;

import java.util.*;
import java.util.regex.Pattern;

import static com.google.common.collect.Collections2.transform;
import static com.google.common.collect.Maps.transformEntries;

public class AttributeMapFilter {

  public static class ServiceAttribute {
    private final String name;
    private final List<String> filters;
    private final List<String> userValues;

    public ServiceAttribute(String name, List<String> filters) {
      this.name = name;
      this.filters = filters;
      this.userValues = new ArrayList<>();
    }

    public ServiceAttribute(String name, String... filters) {
      this(name, Arrays.asList(filters));
    }

    public String getName() {
      return name;
    }

    public List<String> getFilters() {
      return filters;
    }

    public List<String> getUserValues() {
      return userValues;
    }

    public void addUserValues(String... userValues) {
      this.addUserValues(Arrays.asList(userValues));
    }

    public void addUserValues(List<String> userValues) {
      this.userValues.addAll(userValues);
    }


    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ServiceAttribute that = (ServiceAttribute) o;

      if (filters != null ? !filters.equals(that.filters) : that.filters != null) return false;
      if (name != null ? !name.equals(that.name) : that.name != null) return false;
      if (userValues != null ? !userValues.equals(that.userValues) : that.userValues != null) return false;

      return true;
    }

    @Override
    public int hashCode() {
      int result = name != null ? name.hashCode() : 0;
      result = 31 * result + (filters != null ? filters.hashCode() : 0);
      result = 31 * result + (userValues != null ? userValues.hashCode() : 0);
      return result;
    }

    @Override
    public String toString() {
      return "ServiceAttribute{" +
        "name='" + name + '\'' +
        ", filters=" + filters +
        ", userValues=" + userValues +
        '}';
    }

  }

  public static List<String> valuesToShow(final List<String> filters, List<String> rawValues) {
    if (filters.isEmpty()) {
      return rawValues;
    }
    final Collection<Pattern> patterns = transform(filters, new Function<String, Pattern>() {
      @Override
      public Pattern apply(String input) {
        return Pattern.compile(input.replaceAll("\\*", ".*"));
      }
    });
    Predicate<String> inFilter = new Predicate<String>() {
      @Override
      public boolean apply(String input) {
        for (Pattern pattern : patterns) {
          if (pattern.matcher(input).matches()) {
            return true; //stop at first match
          }
        }
        return false;
      }
    };
    return new ArrayList(Collections2.filter(rawValues, inFilter));
  }

  public static Collection<ServiceAttribute> filterAttributes(Map<String, List<Object>> serviceAttributes, final Map<String, List<String>> userAttributes) {
    // cast List<Object> to List<String>
    Map<String, List<String>> serviceAttributesAsString = transformEntries(serviceAttributes, new Maps.EntryTransformer<String, List<Object>, List<String>>() {
      @Override
      public List<String> transformEntry(String key, List<Object> value) {
        return (List<String>) (List<?>) value;
      }
    });

    // make them into real object of type ServiceAttribute
    Collection<ServiceAttribute> rawServiceAttributes = transform(serviceAttributesAsString.entrySet(), new Function<Map.Entry<String, List<String>>, ServiceAttribute>() {
      @Override
      public ServiceAttribute apply(Map.Entry<String, List<String>> input) {
        return new ServiceAttribute(input.getKey(), input.getValue());
      }
    });

    // add the filtered user values.
    return transform(rawServiceAttributes, new Function<ServiceAttribute, ServiceAttribute>() {
      @Override
      public ServiceAttribute apply(ServiceAttribute serviceAttribute) {
        if (userAttributes.containsKey(serviceAttribute.getName())) {
          List<String> userValues = valuesToShow(serviceAttribute.getFilters(), userAttributes.get(serviceAttribute.getName()));
          serviceAttribute.addUserValues(userValues);
        }
        return serviceAttribute;
      }
    });
  }
}
