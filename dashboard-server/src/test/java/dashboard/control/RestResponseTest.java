package dashboard.control;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import dashboard.domain.CoinUser;
import org.junit.Test;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

import java.util.Locale;

import static org.junit.Assert.assertNotNull;

public class RestResponseTest {

    @Test
    public void testSerializeToJson() {
        ObjectMapper mapper = JsonMapper.builder()
                .changeDefaultPropertyInclusion(incl -> incl.withValueInclusion(Include.NON_NULL))
                .build();
        CoinUser coinUser = new CoinUser();
        coinUser.setDisplayName("foobar");
        RestResponse<CoinUser> response = RestResponse.of(Locale.ENGLISH, coinUser);

        String json = mapper.writeValueAsString(response);
        assertNotNull(json);
    }

}
