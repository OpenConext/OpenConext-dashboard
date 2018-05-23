package selfservice.api.dashboard;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import selfservice.domain.CoinAuthority.Authority;
import selfservice.domain.CoinUser;
import selfservice.domain.Service;
import selfservice.util.AttributeMapFilter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class that will enrich json we send back to clients.
 * Since dashboard does not control all classes that are serialized into
 * JSON this class can be used to add properties to json.
 * <p>
 * <b>This class can not be an instance variable on a spring managed bean since it uses the current user.</b>
 */
public class EnrichJson {

  public static final String FILTERED_USER_ATTRIBUTES = "filteredUserAttributes";
  public static final String SUPER_USER = "superUser";
  public static final String DASHBOARD_ADMIN = "dashboardAdmin";
  public static final String STATS_URL = "statsUrl";
  private final static Logger logger = LoggerFactory.getLogger(EnrichJson.class);
  private Map<Class<?>, JsonApplier> mapping = new HashMap<>();

  private CoinUser currentUser;
  private boolean localProfile;
  private JsonElement json;

  @SuppressWarnings("unchecked")
  private EnrichJson(Environment environment, CoinUser coinUser, String statsUrl) {
    logger.debug("Using {} for user {}", statsUrl, coinUser.getDisplayName());
    this.currentUser = coinUser;
    this.localProfile = environment.acceptsProfiles("local");
    Gson gson = GsonHttpMessageConverter.GSON_BUILDER.create();

    mapping.put(CoinUser.class, (coinUserJsonElement, payload) -> {
      JsonObject user = coinUserJsonElement.getAsJsonObject();

      filterDashboardAuthorities(user);

      user.addProperty(SUPER_USER, ((CoinUser) payload).isSuperUser());
      user.addProperty(DASHBOARD_ADMIN, ((CoinUser) payload).isDashboardAdmin());
      user.addProperty(STATS_URL, statsUrl);
      user.addProperty("localProfile", localProfile);
    });

    mapping.put(Service.class, (serviceJsonElement, payload) -> {
      Service service = (Service) payload;
      JsonArray filteredUserAttributes = new JsonArray();
      if (service.getArp() != null && !service.getArp().isNoArp() && !service.getArp().isNoAttrArp()) {
        AttributeMapFilter.filterAttributes(service.getArp()
          .getAttributes(), currentUser.getAttributeMap()).stream()
          .map(gson::toJsonTree)
          .forEach(filteredUserAttributes::add);
      }
      serviceJsonElement.getAsJsonObject().add(FILTERED_USER_ATTRIBUTES, filteredUserAttributes);
    });
  }

  public static EnrichJson forUser(Environment environment, CoinUser currentUser, String statsUrl) {
    return new EnrichJson(environment, currentUser, statsUrl);
  }

  private void filterDashboardAuthorities(JsonObject user) {
    Iterator<JsonElement> authorities = user.getAsJsonArray("grantedAuthorities").iterator();

    while (authorities.hasNext()) {
      JsonElement authority = authorities.next();
      if (authority.isJsonObject() && !Authority.valueOf(authority.getAsJsonObject().get("authority").getAsString())
        .isDashboardAuthority()) {
        authorities.remove();
      }
    }
  }

  public EnrichJson json(JsonElement json) {
    this.json = json;
    return this;
  }

  public void forPayload(Object payload) {
    checkNotNull(json);
    checkNotNull(payload);

    JsonElement payloadAsJsonElement = json.getAsJsonObject().get("payload");
    if (payloadAsJsonElement.isJsonObject()) {
      if (mapping.containsKey(payload.getClass())) {
        mapping.get(payload.getClass()).apply(payloadAsJsonElement, payload);
      }
    } else if (payloadAsJsonElement.isJsonArray()) {
      JsonArray jsonArray = payloadAsJsonElement.getAsJsonArray();
      for (int i = 0; i < jsonArray.size(); i++) {
        Class<?> classOfPayloadElement = ((List<?>) payload).get(i).getClass();

        if (mapping.containsKey(classOfPayloadElement)) {
          mapping.get(classOfPayloadElement).apply(jsonArray.get(i), ((List<?>) payload).get(i));
        }
      }
    }
  }

  private interface JsonApplier {
    void apply(JsonElement element, Object payload);
  }
}
