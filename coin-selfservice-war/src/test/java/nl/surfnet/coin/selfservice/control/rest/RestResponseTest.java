package nl.surfnet.coin.selfservice.control.rest;

import nl.surfnet.coin.selfservice.domain.CoinUser;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class RestResponseTest {
  @Test
  public void testSerializeToJson() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    CoinUser coinUser = new CoinUser();
    coinUser.setDisplayName("foobar");
    RestResponse<CoinUser> response = new RestResponse<>(coinUser).withSelfRel("http://foo");

    String json = mapper.writeValueAsString(response);
    assertNotNull(json);
  }

}
