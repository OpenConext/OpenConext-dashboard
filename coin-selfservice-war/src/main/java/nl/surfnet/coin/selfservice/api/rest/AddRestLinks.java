package nl.surfnet.coin.selfservice.api.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nl.surfnet.coin.selfservice.domain.CoinUser;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import static java.lang.String.format;

/**
* Created by lars on 09/09/14.
*/
public class AddRestLinks {

  private Map<Class<?>, Consumer<JsonObject>> mapping = new HashMap();

  private static Consumer<JsonObject> NOOP = jsonObject -> {
  };

  private static Consumer<JsonObject> AddLinksToInstitutionIdentityProvider = idp -> {
    JsonObject links = new JsonObject();
    links.addProperty("switch", format("/idp/current/%s", idp.getAsJsonPrimitive("id").getAsString()));
    idp.add("_links", links);
  };

  private final JsonElement json;

  public AddRestLinks(JsonElement json) {
    this.json = json;

    mapping.put(CoinUser.class, coinUser -> {
      JsonObject links = new JsonObject();
      links.addProperty("self", "/users/me");
      coinUser.add("_links", links);
      coinUser.getAsJsonArray("institutionIdps").forEach(idp -> AddLinksToInstitutionIdentityProvider.accept(idp.getAsJsonObject()));
    });
  }

  public static AddRestLinks to(JsonElement json) {
    return new AddRestLinks(json);
  }

  public void forClass(Class<?> aClass) {
    JsonObject payload = json.getAsJsonObject().getAsJsonObject("payload");

    mapping.getOrDefault(aClass, NOOP).accept(payload);

  }
}
