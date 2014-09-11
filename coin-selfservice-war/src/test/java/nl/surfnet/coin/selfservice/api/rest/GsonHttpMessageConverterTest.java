package nl.surfnet.coin.selfservice.api.rest;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.util.Locale;

import static org.junit.Assert.*;

public class GsonHttpMessageConverterTest {

  private GsonHttpMessageConverter converter = new GsonHttpMessageConverter();

  @Test
  public void testWrite() throws Exception {
    MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
    converter.write(new RestResponse(Locale.ENGLISH, RestDataFixture.coinUser("foo", "2", "3")), MediaType.APPLICATION_JSON, outputMessage);
    JsonElement jsonElement = new JsonParser().parse(outputMessage.getBodyAsString());
    String actual = jsonElement.getAsJsonObject().getAsJsonObject("payload").getAsJsonPrimitive("uid").getAsString();
    assertEquals("foo", actual);
  }
}
