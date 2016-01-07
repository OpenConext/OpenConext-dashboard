package selfservice.api.dashboard;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;

import selfservice.domain.CoinUser;

public class RestResponseTest {

  @Test
  public void testSerializeToJson() throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.setSerializationInclusion(Include.NON_NULL);
    CoinUser coinUser = new CoinUser();
    coinUser.setDisplayName("foobar");
    RestResponse response = new RestResponse(Locale.ENGLISH, coinUser);

    String json = mapper.writeValueAsString(response);
    assertNotNull(json);
  }

}
