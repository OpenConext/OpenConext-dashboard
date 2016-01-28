package selfservice.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static selfservice.shibboleth.ShibbolethHeader.Shib_DisplayName;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.junit.Before;
import org.junit.Test;

import selfservice.api.dashboard.EnrichJson;
import selfservice.api.dashboard.RestResponse;

public class CoinUserTest {

  private CoinUser coinUser;

  private Gson gson;

  @Before
  public void setUp() throws Exception {
    gson = new Gson();

    coinUser = new CoinUser();
    coinUser.addAttribute(Shib_DisplayName, Arrays.asList("bar"));
    coinUser.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN));
    coinUser.addInstitutionIdp(new IdentityProvider("id", "institutionId", "name"));
    coinUser.setDisplayName("foobar");
  }

  @Test
  public void testIsSuperUser() throws IOException {
    assertFalse(coinUser.isSuperUser());
    coinUser.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_SUPER_USER));

    assertTrue(coinUser.isSuperUser());
  }

  @Test
  public void testSerializeToJson() throws IOException {
    JsonElement json = gson.toJsonTree(new RestResponse(Locale.ENGLISH, coinUser));
    EnrichJson.forUser(coinUser, "/foo").json(json).forPayload(coinUser);
    assertNotNull(json);
  }

}
