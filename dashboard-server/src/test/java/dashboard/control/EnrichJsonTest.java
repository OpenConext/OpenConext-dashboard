package dashboard.control;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.gson.*;
import dashboard.domain.ARP;
import dashboard.domain.CoinAuthority;
import dashboard.domain.CoinAuthority.Authority;
import dashboard.domain.CoinUser;
import dashboard.domain.Service;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static dashboard.shibboleth.ShibbolethHeader.Shib_DisplayName;
import static dashboard.shibboleth.ShibbolethHeader.Shib_Email;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EnrichJsonTest {

    private Gson gson = new GsonBuilder().setExclusionStrategies(new ExcludeJsonIgnore()).create();

    private boolean statsEnabled = true;

    @Test
    public void testSuperUserToCoinUser() throws Exception {
        CoinUser coinUser = RestDataFixture.coinUser("ben");
        JsonElement jsonElement = createJsonResponse(coinUser);

        EnrichJson.forUser(this.statsEnabled, coinUser).json(jsonElement).forPayload(coinUser);

        assertFalse(getPayloadAsJsonObjectFromRoot(jsonElement).getAsJsonPrimitive("superUser").getAsBoolean());
    }

    @Test
    public void testAddDashboardAdminToCoinUser() throws Exception {
        CoinUser coinUser = RestDataFixture.coinUser("ben");
        JsonElement jsonElement = createJsonResponse(coinUser);

        EnrichJson.forUser(this.statsEnabled, coinUser).json(jsonElement).forPayload(coinUser);

        assertFalse(getPayloadAsJsonObjectFromRoot(jsonElement).getAsJsonPrimitive("dashboardAdmin").getAsBoolean());
    }

    @Test
    public void testAddFilteredUserAttributesToListOfServices() throws Exception {
        CoinUser coinUser = RestDataFixture.coinUser("ben");
        coinUser.addAttribute(Shib_DisplayName, asList("bar"));
        Service service1 = RestDataFixture.serviceWithSpEntityId("id-1");
        Service service2 = RestDataFixture.serviceWithSpEntityId("id-2", service -> {
            service.setId(2l);
            ARP arp = new ARP();
            arp.setNoArp(false);
            arp.setNoAttrArp(false);
            arp.setAttributes(ImmutableMap.of("service", asList("bar")));
            service.setArp(arp);
        });

        List<Service> payload = asList(service1, service2);
        JsonElement jsonElement = createJsonResponse(payload);
        EnrichJson.forUser(this.statsEnabled, coinUser).json(jsonElement).forPayload(payload);

        assertEquals(0, getServiceFromRoot(jsonElement, 0).getAsJsonArray(EnrichJson.FILTERED_USER_ATTRIBUTES).size());
        assertEquals(1, getServiceFromRoot(jsonElement, 1).getAsJsonArray(EnrichJson.FILTERED_USER_ATTRIBUTES).size());
    }

    @Test
    public void testAddFilteredUserAttributesToService() throws Exception {
        CoinUser coinUser = RestDataFixture.coinUser("ben");
        coinUser.addAttribute(Shib_Email, asList("bar"));
        Service service1 = RestDataFixture.serviceWithSpEntityId("id-1", service -> {
            service.setId(10l);
            ARP arp = new ARP();
            arp.setNoArp(false);
            arp.setNoAttrArp(false);
            arp.setAttributes(ImmutableMap.of("Shib-InetOrgPerson-mail", asList("bar")));
            service.setArp(arp);
        });

        JsonElement jsonElement = createJsonResponse(service1);
        EnrichJson.forUser(this.statsEnabled, coinUser).json(jsonElement).forPayload(service1);

        assertEquals(1, getPayloadAsJsonObjectFromRoot(jsonElement).getAsJsonArray(EnrichJson.FILTERED_USER_ATTRIBUTES).size());
    }

    @Test
    public void dashboardAuthoritiesShouldBeFiltered() {
        CoinUser coinUser = RestDataFixture.coinUser("john");

        coinUser.addAuthority(new CoinAuthority(Authority.ROLE_DASHBOARD_ADMIN));

        JsonElement jsonElement = createJsonResponse(coinUser);
        EnrichJson.forUser(this.statsEnabled, coinUser).json(jsonElement).forPayload(coinUser);

        List<JsonElement> authorities = Lists.newArrayList(getPayloadAsJsonObjectFromRoot(jsonElement).getAsJsonArray
                ("grantedAuthorities"));

        assertThat(authorities, hasSize(1));
        assertThat(authorities.get(0).getAsJsonObject().get("authority").getAsString(), is(Authority.ROLE_DASHBOARD_ADMIN
                .name()));
    }

    private JsonObject getServiceFromRoot(JsonElement jsonElement, int index) {
        return getPayloadAsJsonArrayFromRoot(jsonElement).get(index).getAsJsonObject();
    }

    private JsonArray getPayloadAsJsonArrayFromRoot(JsonElement jsonElement) {
        return getPayloadFromRoot(jsonElement).getAsJsonArray();
    }

    private JsonElement createJsonResponse(Object object) {
        return gson.toJsonTree(RestResponse.of(Locale.ENGLISH, object));
    }

    private JsonElement getPayloadFromRoot(JsonElement jsonElement) {
        return jsonElement.getAsJsonObject().get("payload");
    }

    private JsonObject getPayloadAsJsonObjectFromRoot(JsonElement jsonElement) {
        return getPayloadFromRoot(jsonElement).getAsJsonObject();
    }
}
