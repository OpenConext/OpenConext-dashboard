package nl.surfnet.coin.selfservice.domain;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.api.rest.AddRestLinks;
import nl.surfnet.coin.selfservice.api.rest.RestResponse;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static java.lang.String.format;
import static org.junit.Assert.assertNotNull;

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
  public void testSerializeToJson() throws IOException {
    JsonElement json = gson.toJsonTree(new RestResponse<>(coinUser));
    AddRestLinks.to(json).forClass(coinUser.getClass());
    System.out.println(json);
    assertNotNull(json);
  }

}
