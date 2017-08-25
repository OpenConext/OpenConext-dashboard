package selfservice.api.dashboard;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import selfservice.api.dashboard.GsonHttpMessageConverter;
import selfservice.api.dashboard.RestResponse;
import selfservice.domain.Category;
import selfservice.domain.CategoryValue;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.util.Arrays;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GsonHttpMessageConverterTest {
  private GsonHttpMessageConverter converter;
  private MockHttpOutputMessage outputMessage;

  @Before
  public void setUp() throws Exception {
    outputMessage = new MockHttpOutputMessage();
    converter = new GsonHttpMessageConverter("https://foo", "oauth/authorize.php","bar", "scope", "/foobar");
  }

  @Test
  public void testWrite() throws Exception {
    MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
    converter.write(RestResponse.of(Locale.ENGLISH, RestDataFixture.coinUser("foo", "2", "3")), MediaType.APPLICATION_JSON, outputMessage);
    JsonElement jsonElement = new JsonParser().parse(outputMessage.getBodyAsString());
    String actual = jsonElement.getAsJsonObject().getAsJsonObject("payload").getAsJsonPrimitive("uid").getAsString();
    assertEquals("foo", actual);
  }

  @Test
  public void testCategoryValue() throws Exception {
    Category category = new Category();
    CategoryValue categoryValue = new CategoryValue("", category);
    category.setValues(Arrays.asList(categoryValue));

    converter.write(RestResponse.of(Locale.ENGLISH, categoryValue), MediaType.APPLICATION_JSON, outputMessage);
    assertNotNull(outputMessage.getBodyAsString());
  }
}
