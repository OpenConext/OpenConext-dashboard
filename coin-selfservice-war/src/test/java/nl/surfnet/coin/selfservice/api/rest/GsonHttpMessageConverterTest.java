package nl.surfnet.coin.selfservice.api.rest;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import nl.surfnet.coin.csa.model.Category;
import nl.surfnet.coin.csa.model.CategoryValue;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.http.MockHttpOutputMessage;

import java.util.Arrays;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GsonHttpMessageConverterTest {

  private GsonHttpMessageConverter converter = new GsonHttpMessageConverter();
  private MockHttpOutputMessage outputMessage;

  @Before
  public void setUp() throws Exception {
    outputMessage = new MockHttpOutputMessage();
  }

  @Test
  public void testWrite() throws Exception {
    MockHttpOutputMessage outputMessage = new MockHttpOutputMessage();
    converter.write(new RestResponse(Locale.ENGLISH, RestDataFixture.coinUser("foo", "2", "3")), MediaType.APPLICATION_JSON, outputMessage);
    JsonElement jsonElement = new JsonParser().parse(outputMessage.getBodyAsString());
    String actual = jsonElement.getAsJsonObject().getAsJsonObject("payload").getAsJsonPrimitive("uid").getAsString();
    assertEquals("foo", actual);
  }

  @Test
  public void testCategoryValue() throws Exception {
    CategoryValue categoryValue = new CategoryValue("");
    Category category = new Category();
    categoryValue.setCategory(category);
    category.setValues(Arrays.asList(categoryValue));

    converter.write(new RestResponse(Locale.ENGLISH, categoryValue), MediaType.APPLICATION_JSON, outputMessage);
    assertNotNull(outputMessage.getBodyAsString());
  }
}
