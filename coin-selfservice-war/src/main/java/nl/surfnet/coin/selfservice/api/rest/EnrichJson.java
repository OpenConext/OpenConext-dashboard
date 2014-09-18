package nl.surfnet.coin.selfservice.api.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.domain.CoinUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;

public class EnrichJson {

  private static interface JsonApplier {
    public void apply(JsonElement element, Object payload);
  }

  private Map<Class<?>, JsonApplier> mapping = new HashMap();

  private static JsonApplier AddLinksToInstitutionIdentityProvider = new JsonApplier() {
    @Override
    public void apply(JsonElement element, Object payload) {
      JsonObject links = new JsonObject();
      links.addProperty("switch", format("/idp/current/%s", element.getAsJsonObject().getAsJsonPrimitive("id").getAsString()));
      element.getAsJsonObject().add("_links", links);
    }
  };

  private final JsonElement json;

  public EnrichJson(JsonElement json) {
    this.json = json;


    mapping.put(CoinUser.class, new JsonApplier() {
      @Override
      public void apply(JsonElement coinUserJsonElement, Object payload) {
        JsonObject links = new JsonObject();
        links.addProperty("self", "/users/me");
        JsonObject coinUser = coinUserJsonElement.getAsJsonObject();
        coinUser.add("_links", links);
        coinUser.addProperty("superUser", ((CoinUser) payload).isSuperUser());
        for (JsonElement idp : coinUser.getAsJsonArray("institutionIdps")) {
          AddLinksToInstitutionIdentityProvider.apply(idp, payload);
        }
      }
    });

    mapping.put(Service.class, new JsonApplier() {
      @Override
      public void apply(JsonElement serviceJsonElement, Object payload) {
        JsonObject links = new JsonObject();
        JsonObject serviceAsJsonObject = serviceJsonElement.getAsJsonObject();
        links.addProperty("self", format("/services/id/%s", serviceAsJsonObject.getAsJsonPrimitive("id").getAsLong()));
        serviceAsJsonObject.add("_links", links);
      }
    });
  }

  public static EnrichJson with(JsonElement json) {
    return new EnrichJson(json);
  }

  public void forPayload(Object payload) {
    JsonElement payloadAsJsonElement = json.getAsJsonObject().get("payload");
    if (payloadAsJsonElement.isJsonObject()) {
      if (mapping.containsKey(payload.getClass())) {
        mapping.get(payload.getClass()).apply(payloadAsJsonElement, payload);
      }
    } else {
      Class<?> classOfPayloadElement = ((List) payload).get(0).getClass();
      for (JsonElement jsonElement : payloadAsJsonElement.getAsJsonArray()) {
        if (mapping.containsKey(classOfPayloadElement)) {
          mapping.get(classOfPayloadElement).apply(jsonElement, payload);
        }
      }
    }
  }
}
