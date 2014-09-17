package nl.surfnet.coin.selfservice.api.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.domain.CoinUser;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static java.lang.String.format;

public class EnrichJson {

  private Map<Class<?>, BiConsumer<JsonElement, Object>> mapping = new HashMap();

  private static BiConsumer<JsonElement, Object> NOOP = (jsonObject, payload) -> { };

  private static Consumer<JsonObject> AddLinksToInstitutionIdentityProvider = idp -> {
    JsonObject links = new JsonObject();
    links.addProperty("switch", format("/idp/current/%s", idp.getAsJsonPrimitive("id").getAsString()));
    idp.add("_links", links);
  };

  private final JsonElement json;

  public EnrichJson(JsonElement json) {
    this.json = json;

    mapping.put(CoinUser.class, (coinUserJsonElement, payload) -> {
      JsonObject links = new JsonObject();
      links.addProperty("self", "/users/me");
      JsonObject coinUser = coinUserJsonElement.getAsJsonObject();
      coinUser.add("_links", links);
      coinUser.addProperty("superUser", ((CoinUser)payload).isSuperUser());
      coinUser.getAsJsonArray("institutionIdps").forEach(idp -> AddLinksToInstitutionIdentityProvider.accept(idp.getAsJsonObject()));
    });
    mapping.put(Service.class, (serviceJsonElement, payload) -> {
      JsonObject links = new JsonObject();
      JsonObject serviceAsJsonObject = serviceJsonElement.getAsJsonObject();
      links.addProperty("self", format("/services/id/%s", serviceAsJsonObject.getAsJsonPrimitive("id").getAsLong()));
      serviceAsJsonObject.add("_links", links);
    });
  }

  public static EnrichJson with(JsonElement json) {
    return new EnrichJson(json);
  }

  public void forPayload(Object payload) {
    JsonElement payloadAsJsonElement = json.getAsJsonObject().get("payload");
    if(payloadAsJsonElement.isJsonObject()) {
      mapping.getOrDefault(payload.getClass(), NOOP).accept(payloadAsJsonElement, payload);
    } else {
      Class<?> classOfPayloadElement = ((List) payload).get(0).getClass();
      payloadAsJsonElement.getAsJsonArray().forEach(jsonElementInArray -> mapping.getOrDefault(classOfPayloadElement, NOOP).accept(jsonElementInArray, payload));
    }
  }
}
