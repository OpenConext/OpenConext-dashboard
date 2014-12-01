package nl.surfnet.coin.selfservice.api.rest;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.*;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.janus.domain.ARP;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EnrichJsonTest {

  private final static String STATS_URL = "https://foo";
  private Gson gson;
  private CoinUser coinUser;

  @Before
  public void setUp() throws Exception {
    gson = new GsonBuilder().setExclusionStrategies(new ExcludeJsonIgnore()).create();
    coinUser = RestDataFixture.coinUser("ben");
  }

  @Test
  public void testAddsStatsUrlToCoinUser() throws Exception {
    JsonElement jsonElement = createJsonResponse(coinUser);

    EnrichJson.forUser(coinUser, STATS_URL).json(jsonElement).forPayload(coinUser);

    assertEquals(STATS_URL, getPayloadAsJsonObjectFromRoot(jsonElement).getAsJsonPrimitive("statsUrl").getAsString());
  }

  @Test
  public void testSuperUserToCoinUser() throws Exception {
    JsonElement jsonElement = createJsonResponse(coinUser);

    EnrichJson.forUser(coinUser, STATS_URL).json(jsonElement).forPayload(coinUser);

    assertFalse(getPayloadAsJsonObjectFromRoot(jsonElement).getAsJsonPrimitive("superUser").getAsBoolean());
  }

  @Test
  public void testAddDashboardAdminToCoinUser() throws Exception {
    JsonElement jsonElement = createJsonResponse(coinUser);

    EnrichJson.forUser(coinUser, STATS_URL).json(jsonElement).forPayload(coinUser);

    assertFalse(getPayloadAsJsonObjectFromRoot(jsonElement).getAsJsonPrimitive("dashboardAdmin").getAsBoolean());
  }

  @Test
  public void testAddFilteredUserAttributesToListOfServices() throws Exception {
    coinUser.addAttribute("service", asList("bar"));
    Service service1 = RestDataFixture.serviceWithSpEntityId("id-1");
    Service service2 = RestDataFixture.serviceWithSpEntityId("id-2", new RestDataFixture.ServiceUpdater() {
      @Override
      public void apply(Service service) {
        service.setId(2l);
        ARP arp = new ARP();
        arp.setNoArp(false);
        arp.setNoAttrArp(false);
        arp.setAttributes(ImmutableMap.of("service", asList((Object) "bar")));
        service.setArp(arp);
      }
    });

    List<Service> payload = asList(service1, service2);
    JsonElement jsonElement = createJsonResponse(payload);
    EnrichJson.forUser(coinUser, STATS_URL).json(jsonElement).forPayload(payload);

    assertEquals(0, getServiceFromRoot(jsonElement, 0).getAsJsonArray(EnrichJson.FILTERED_USER_ATTRIBUTES).size());
    assertEquals(1, getServiceFromRoot(jsonElement, 1).getAsJsonArray(EnrichJson.FILTERED_USER_ATTRIBUTES).size());
  }

  @Test
  public void testAddFilteredUserAttributesToService() throws Exception {
    coinUser.addAttribute("service", asList("bar"));
    Service service1 = RestDataFixture.serviceWithSpEntityId("id-1", new RestDataFixture.ServiceUpdater() {
      @Override
      public void apply(Service service) {
        service.setId(10l);
        ARP arp = new ARP();
        arp.setNoArp(false);
        arp.setNoAttrArp(false);
        arp.setAttributes(ImmutableMap.of("service", asList((Object) "bar")));
        service.setArp(arp);
      }
    });

    JsonElement jsonElement = createJsonResponse(service1);
    EnrichJson.forUser(coinUser, STATS_URL).json(jsonElement).forPayload(service1);

    assertEquals(1, getPayloadAsJsonObjectFromRoot(jsonElement).getAsJsonArray(EnrichJson.FILTERED_USER_ATTRIBUTES).size());
  }

  private JsonObject getServiceFromRoot(JsonElement jsonElement, int index) {
    return getPayloadAsJsonArrayFromRoot(jsonElement).get(index).getAsJsonObject();
  }

  private JsonArray getPayloadAsJsonArrayFromRoot(JsonElement jsonElement) {
    return getPayloadFromRoot(jsonElement).getAsJsonArray();
  }

  private JsonElement createJsonResponse(Object object) {
    return gson.toJsonTree(new RestResponse(Locale.ENGLISH, object));
  }

  private JsonElement getPayloadFromRoot(JsonElement jsonElement) {
    return jsonElement.getAsJsonObject().get("payload");
  }

  private JsonObject getPayloadAsJsonObjectFromRoot(JsonElement jsonElement) {
    return getPayloadFromRoot(jsonElement).getAsJsonObject();
  }
}
