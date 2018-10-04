package dashboard.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static dashboard.shibboleth.ShibbolethHeader.Shib_DisplayName;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.junit.Before;
import org.junit.Test;

import dashboard.control.EnrichJson;
import dashboard.control.RestResponse;

public class CoinUserTest {

  private CoinUser coinUser;

  private Gson gson;

  @Before
  public void setUp() throws Exception {
    gson = new Gson();

    coinUser = new CoinUser();
    coinUser.addAttribute(Shib_DisplayName, Arrays.asList("bar"));
    coinUser.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN));
    coinUser.addInstitutionIdp(new IdentityProvider("id", "institutionId", "name", 1L));
    coinUser.setDisplayName("foobar");
  }

  @Test
  public void testIsSuperUser() throws IOException {
    assertFalse(coinUser.isSuperUser());
    coinUser.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_SUPER_USER));

    assertTrue(coinUser.isSuperUser());
  }

  @Test
  public void testSerializeToJson() {
    JsonElement json = gson.toJsonTree(RestResponse.of(Locale.ENGLISH, coinUser));
    EnrichJson.forUser(true, coinUser).json(json).forPayload(coinUser);
    assertNotNull(json);
  }

}
