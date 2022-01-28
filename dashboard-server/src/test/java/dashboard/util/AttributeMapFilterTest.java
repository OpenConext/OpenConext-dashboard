package dashboard.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import dashboard.shibboleth.ShibbolethHeader;
import dashboard.util.AttributeMapFilter.ServiceAttribute;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static dashboard.shibboleth.ShibbolethHeader.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

public class AttributeMapFilterTest {

    @Test
    public void filteringAttributesShouldAddUserValues() {
        Map<String, List<String>> serviceAttributes = ImmutableMap.of(
                "urn:mace:dir:attribute-def:uid", ImmutableList.of("*"),
                "urn:mace:dir:attribute-def:sn", ImmutableList.of("*"));
        Map<ShibbolethHeader, List<String>> userAttributes = ImmutableMap.of(
                Shib_SurName, ImmutableList.of("Doe"),
                Shib_Uid, ImmutableList.of("uid"));

        Collection<ServiceAttribute> filterAttributes = AttributeMapFilter.filterAttributes(serviceAttributes, userAttributes);

        ServiceAttribute expectedServiceAttribute = new ServiceAttribute("urn:mace:dir:attribute-def:uid", "*");
        expectedServiceAttribute.addUserValues("uid");

        assertThat(filterAttributes, hasSize(2));
        assertThat(filterAttributes, hasItem(expectedServiceAttribute));
    }

    @Test
    public void filterMultiValueAttributeShouldMatchReturnValuesThatMatchFilter() {
        List<String> valueFilters = ImmutableList.of("urn:mace:terena.org:tcs:personal-user", "urn:mace:terena.org:tcs:escience-user");
        String attributeName = "urn:mace:dir:attribute-def:eduPersonEntitlement";

        Map<String, List<String>> serviceAttributes = ImmutableMap.of(attributeName, valueFilters);

        Map<ShibbolethHeader, List<String>> userAttributes = ImmutableMap.of(
                Shib_EduPersonEntitlement, ImmutableList.of("urn:x-surfnet:surf.nl:surfdrive:quota:100", "urn:mace:terena.org:tcs:personal-user"));

        Collection<ServiceAttribute> filteredAttributes = AttributeMapFilter.filterAttributes(serviceAttributes, userAttributes);

        ServiceAttribute expectedServiceAttribute = new ServiceAttribute(attributeName, valueFilters);
        expectedServiceAttribute.addUserValues("urn:mace:terena.org:tcs:personal-user");

        assertThat(filteredAttributes, contains(expectedServiceAttribute));
    }
}
