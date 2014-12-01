package nl.surfnet.coin.selfservice.domain;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.api.rest.EnrichJson;
import nl.surfnet.coin.selfservice.api.rest.RestResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import static java.lang.String.format;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CoinUserTest {

  private CoinUser coinUser;


  private Gson gson;

  @Before
  public void setUp() throws Exception {
    gson = new Gson();

    coinUser = new CoinUser();
    coinUser.addAttribute("foo", Arrays.asList("bar"));
    coinUser.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN));
    coinUser.addInstitutionIdp(new InstitutionIdentityProvider("id", "name", "institutionId"));
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
