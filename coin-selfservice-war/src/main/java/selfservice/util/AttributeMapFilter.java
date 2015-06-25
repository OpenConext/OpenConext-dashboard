package selfservice.util;

import selfservice.shibboleth.ShibbolethPreAuthenticatedProcessingFilter;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

  private static List<String> valuesToShow(final List<String> filters, List<String> rawValues) {
    if (filters.isEmpty()) {
      return rawValues;
    }
    Collection<Pattern> patterns = filters.stream().map(input -> Pattern.compile(input.replaceAll("\\*", ".*"))).collect(Collectors.toList());
    return rawValues.stream().filter(value -> patterns.stream().filter(pattern -> pattern.matcher(value).matches()).findFirst().isPresent()).collect(Collectors.toList());
  }

  public static Collection<ServiceAttribute> filterAttributes(Map<String, List<Object>> serviceAttributes, final Map<String, List<String>> userAttributes) {
    // cast List<Object> to List<String>
    Map<String, List<String>> serviceAttributesAsString = transformEntries(serviceAttributes, (key, value) -> (List<String>) (List<?>) value);

    // make them into real object of type ServiceAttribute
    Collection<ServiceAttribute> rawServiceAttributes = transform(serviceAttributesAsString.entrySet(), input -> new ServiceAttribute(input.getKey(), input.getValue()));
    // add the filtered user values.
    return rawServiceAttributes.stream().map(serviceAttribute -> {
      String shibHeader = ShibbolethPreAuthenticatedProcessingFilter.shibHeaders.get(serviceAttribute.getName());
      List<String> values = userAttributes.get(shibHeader);
      if (!CollectionUtils.isEmpty(values)) {
        List<String> userValues = valuesToShow(serviceAttribute.getFilters(), values);
        serviceAttribute.addUserValues(userValues);
      }
      return serviceAttribute;
    }).collect(Collectors.toList());
  }
}
