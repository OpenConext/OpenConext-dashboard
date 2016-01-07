package selfservice.util;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Pattern;

import selfservice.shibboleth.ShibbolethPreAuthenticatedProcessingFilter;

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
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      ServiceAttribute that = (ServiceAttribute) o;

      if (filters != null ? !filters.equals(that.filters) : that.filters != null) {
        return false;
      }
      if (name != null ? !name.equals(that.name) : that.name != null) {
        return false;
      }
      if (userValues != null ? !userValues.equals(that.userValues) : that.userValues != null) {
        return false;
      }

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

  private static List<String> valuesToShow(List<String> filters, List<String> rawValues) {
    if (filters.isEmpty()) {
      return rawValues;
    }
    Collection<Pattern> patterns = filters.stream().map(filter -> Pattern.compile(filter.replaceAll("\\*", ".*"))).collect(toList());
    return rawValues.stream().filter(value -> patterns.stream().anyMatch(p -> p.matcher(value).matches())).collect(toList());
  }

  public static Collection<ServiceAttribute> filterAttributes(Map<String, List<String>> serviceAttributes, Map<String, List<String>> userAttributes) {
    Function<ServiceAttribute, List<String>> userValues = (serviceAttribute) -> {
      String shibHeader = ShibbolethPreAuthenticatedProcessingFilter.shibHeaders.get(serviceAttribute.getName());
      return Optional.ofNullable(userAttributes.get(shibHeader))
          .map(v -> valuesToShow(serviceAttribute.getFilters(), v))
          .orElse(Collections.emptyList());
    };

    return serviceAttributes.entrySet().stream()
        .map(entry -> new ServiceAttribute(entry.getKey(), entry.getValue()))
        .map(serviceAttribute -> {
          serviceAttribute.addUserValues(userValues.apply(serviceAttribute));
          return serviceAttribute;
        })
        .collect(toList());
  }

}
