package selfservice.util;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.junit.Test;

import selfservice.util.AttributeMapFilter.ServiceAttribute;

public class AttributeMapFilterTest {

  @Test
  public void filteringAttributesShouldAddUserValues() {
    Map<String, List<Object>> serviceAttributes = ImmutableMap.of(
        "urn:mace:dir:attribute-def:uid", ImmutableList.of("*"),
        "urn:mace:dir:attribute-def:sn", ImmutableList.of("*"));
    Map<String, List<String>> userAttributes = ImmutableMap.of(
        "Shib-surName", ImmutableList.of("Doe"),
        "Shib-uid", ImmutableList.of("uid"));

    Collection<ServiceAttribute> filterAttributes = AttributeMapFilter.filterAttributes(serviceAttributes, userAttributes);

    ServiceAttribute expectedServiceAttribute = new ServiceAttribute("urn:mace:dir:attribute-def:uid", "*");
    expectedServiceAttribute.addUserValues("uid");

    assertThat(filterAttributes, hasSize(2));
    assertThat(filterAttributes, hasItem(expectedServiceAttribute));
  }
}
