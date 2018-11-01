package dashboard.control;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import dashboard.domain.Category;
import dashboard.domain.CategoryValue;
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
        converter = new GsonHttpMessageConverter(true);
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
        CategoryValue categoryValue = new CategoryValue("");
        category.setValues(Arrays.asList(categoryValue));

        converter.write(RestResponse.of(Locale.ENGLISH, categoryValue), MediaType.APPLICATION_JSON, outputMessage);
        assertNotNull(outputMessage.getBodyAsString());
    }
}
