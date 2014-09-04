package nl.surfnet.coin.selfservice.domain;

import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.api.rest.RestResponse;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class CoinUserTest {
  @Test
  public void testSerializeToJson() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    CoinUser coinUser = new CoinUser();
    coinUser.addAttribute("foo", Arrays.asList("bar"));
    coinUser.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN));
    coinUser.addInstitutionIdp(new InstitutionIdentityProvider("id", "name", "institutionId"));
    coinUser.setDisplayName("foobar");

    String json = mapper.writeValueAsString(new RestResponse<>(coinUser));
    assertNotNull(json);
  }
}
