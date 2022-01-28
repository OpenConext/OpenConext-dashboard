package dashboard.control;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import dashboard.domain.CoinUser;
import org.junit.Test;

import java.io.IOException;
import java.util.Locale;

import static org.junit.Assert.assertNotNull;

public class RestResponseTest {

    @Test
    public void testSerializeToJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_NULL);
        CoinUser coinUser = new CoinUser();
        coinUser.setDisplayName("foobar");
        RestResponse<CoinUser> response = RestResponse.of(Locale.ENGLISH, coinUser);

        String json = mapper.writeValueAsString(response);
        assertNotNull(json);
    }

}
