package selfservice.api.rest;

import com.google.common.collect.Collections2;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import selfservice.domain.CoinUser;
import selfservice.domain.Service;
import selfservice.util.AttributeMapFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class that will enrich json we send back to clients.
 * Since dashboard does not control all classes that are serialized into
 * JSON this class can be used to add properties to json.
 * <p/>
 * <b>This class can not be an instance variable on a spring managed bean since it uses the current user.</b>
 */
public class EnrichJson {

  private final static Logger logger = LoggerFactory.getLogger(EnrichJson.class);

  public static final String FILTERED_USER_ATTRIBUTES = "filteredUserAttributes";
  public static final String SUPER_USER = "superUser";
  public static final String DASHBOARD_ADMIN = "dashboardAdmin";
  public static final String STATS_URL = "statsUrl";

  private Map<Class<?>, JsonApplier> mapping = new HashMap<>();

  private final CoinUser currentUser;
  private JsonElement json;

  private interface JsonApplier {
    void apply(JsonElement element, Object payload);
  }

  private EnrichJson(CoinUser coinUser, final String statsUrl) {
    logger.debug("Using {} for user {}", statsUrl, coinUser.getDisplayName());
    this.currentUser = coinUser;
    final Gson gson = GsonHttpMessageConverter.GSON_BUILDER.create();

    mapping.put(CoinUser.class, new JsonApplier() {
      @Override
      public void apply(JsonElement coinUserJsonElement, Object payload) {
        JsonObject coinUser = coinUserJsonElement.getAsJsonObject();
        coinUser.addProperty(SUPER_USER, ((CoinUser) payload).isSuperUser());
        coinUser.addProperty(DASHBOARD_ADMIN, ((CoinUser) payload).isDashboardAdmin());
        coinUser.addProperty(STATS_URL, statsUrl);

      }
    });

    mapping.put(Service.class, new JsonApplier() {
      @Override
      public void apply(JsonElement serviceJsonElement, Object payload) {
        Service service = (Service) payload;
        JsonArray filteredUserAttributes = new JsonArray();
        if (service.getArp() != null && !service.getArp().isNoArp() && !service.getArp().isNoAttrArp()) {
          Collection<JsonElement> jsonElements = Collections2.transform(AttributeMapFilter.filterAttributes(service.getArp().getAttributes(), currentUser.getAttributeMap()), input -> gson.toJsonTree(input));
          for (JsonElement jsonElement : jsonElements) {
            filteredUserAttributes.add(jsonElement);
          }
        }
        JsonObject serviceAsJsonObject = serviceJsonElement.getAsJsonObject();
        serviceAsJsonObject.add(FILTERED_USER_ATTRIBUTES, filteredUserAttributes);
      }
    });
  }

  public EnrichJson json(JsonElement json) {
    this.json = json;
    return this;
  }

  public static EnrichJson forUser(CoinUser currentUser, String statsUrl) {
    return new EnrichJson(currentUser, statsUrl);
  }

  public void forPayload(Object payload) {
    JsonElement payloadAsJsonElement = json.getAsJsonObject().get("payload");
    if (payloadAsJsonElement.isJsonObject()) {
      if (mapping.containsKey(payload.getClass())) {
        mapping.get(payload.getClass()).apply(payloadAsJsonElement, payload);
      }
    } else if (!payloadAsJsonElement.isJsonPrimitive()) {
      JsonArray jsonArray = payloadAsJsonElement.getAsJsonArray();
      for (int i = 0; i < jsonArray.size(); i++) {
        Class<?> classOfPayloadElement = ((List) payload).get(i).getClass();

        if (mapping.containsKey(classOfPayloadElement)) {
          mapping.get(classOfPayloadElement).apply(jsonArray.get(i), ((List) payload).get(i));
        }
      }
    }
  }
}
