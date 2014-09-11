package nl.surfnet.coin.selfservice.api.rest;

import com.google.gson.*;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import org.junit.Before;
import org.junit.Test;

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

    new AddRestLinks(jsonElement).forClass(CoinUser.class);

    assertEquals(getLinksFromRoot(jsonElement).getAsJsonPrimitive("self").getAsString(), "/users/me");

  }

  @Test
  public void testAddLinksToListOfServices() throws Exception {
    Service service1 = RestDataFixture.serviceWithSpEntityId("id-1");
    Service service2 = RestDataFixture.serviceWithSpEntityId("id-2", service -> service.setId(2l));

    JsonElement jsonElement = createJsonResponse(asList(service1, service2));
    new AddRestLinks(jsonElement).forClass(Service.class);

    assertEquals("/services/id/1", getFirstServiceFromRoot(jsonElement).getAsJsonObject("_links").getAsJsonPrimitive("self").getAsString());
  }

  @Test
  public void testAddLinksToSingleService() throws Exception {
    Service service1 = RestDataFixture.serviceWithSpEntityId("id-1", service -> service.setId(10l));

    JsonElement jsonElement = createJsonResponse(service1);
    new AddRestLinks(jsonElement).forClass(Service.class);

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
