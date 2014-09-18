package nl.surfnet.coin.selfservice.api.rest;

import com.google.gson.*;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class AddRestLinksTest {

  private Gson gson;

  @Before
  public void setUp() throws Exception {
    gson = new GsonBuilder().setExclusionStrategies(new ExcludeJsonIgnore()).create();
  }

  @Test
  public void testAddLinksToCoinUser() throws Exception {
    CoinUser coinUser = RestDataFixture.coinUser("ben");
    JsonElement jsonElement = createJsonResponse(coinUser);

    new AddRestLinks(jsonElement).forPayload(coinUser);

    assertEquals(getLinksFromRoot(jsonElement).getAsJsonPrimitive("self").getAsString(), "/users/me");

  }

  @Test
  public void testAddLinksToListOfServices() throws Exception {
    Service service1 = RestDataFixture.serviceWithSpEntityId("id-1");
    Service service2 = RestDataFixture.serviceWithSpEntityId("id-2", new RestDataFixture.ServiceUpdater() {
      @Override
      public void apply(Service service) {
        service.setId(2l);
      }
    });

    List<Service> payload = asList(service1, service2);
    JsonElement jsonElement = createJsonResponse(payload);
    new AddRestLinks(jsonElement).forPayload(payload);

    assertEquals("/services/id/1", getFirstServiceFromRoot(jsonElement).getAsJsonObject("_links").getAsJsonPrimitive("self").getAsString());
  }

  @Test
  public void testAddLinksToSingleService() throws Exception {
    Service service1 = RestDataFixture.serviceWithSpEntityId("id-1", new RestDataFixture.ServiceUpdater() {
      @Override
      public void apply(Service service) {
        service.setId(10l);
      }
    });

    JsonElement jsonElement = createJsonResponse(service1);
    new AddRestLinks(jsonElement).forPayload(service1);

    assertEquals("/services/id/10", getLinksFromRoot(jsonElement).getAsJsonPrimitive("self").getAsString());

  }

  private JsonObject getFirstServiceFromRoot(JsonElement jsonElement) {
    return getPayloadAsJsonArrayFromRoot(jsonElement).get(0).getAsJsonObject();
  }

  private JsonArray getPayloadAsJsonArrayFromRoot(JsonElement jsonElement) {
    return getPayloadFromRoot(jsonElement).getAsJsonArray();
  }

  private JsonElement createJsonResponse(Object object) {
    return gson.toJsonTree(new RestResponse(Locale.ENGLISH, object));
  }

  private JsonObject getLinksFromRoot(JsonElement jsonElement) {
    return getPayloadAsJsonObjectFromRoot(jsonElement).getAsJsonObject("_links");
  }

  private JsonElement getPayloadFromRoot(JsonElement jsonElement) {
    return jsonElement.getAsJsonObject().get("payload");
  }

  private JsonObject getPayloadAsJsonObjectFromRoot(JsonElement jsonElement) {
    return getPayloadFromRoot(jsonElement).getAsJsonObject();
  }
}
