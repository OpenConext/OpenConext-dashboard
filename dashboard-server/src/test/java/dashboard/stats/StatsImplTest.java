package dashboard.stats;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import org.junit.Rule;
import org.junit.Test;

import java.util.List;
import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class StatsImplTest {

    private final String idp = "http://login.surf.nl/adfs/services/trust?q=1&k=2";

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8891);

    private final StatsImpl stats = new StatsImpl("user", "password","http://localhost:8891"){
        @Override
        protected String getCurrentUserIdp() {
            return idp;
        }
    };

    @Test
    public void shouldEncodeIdpIdExactlyOnce() {
        stubFor(get(urlPathEqualTo("/public/login_time_frame"))
                .withQueryParam("idp_id", equalTo(idp))
                .withQueryParam("from", equalTo("1000"))
                .withQueryParam("to", equalTo("2000"))
                .willReturn(okJson("[]")));

        List<Object> result = stats.loginTimeFrame(1000, 2000, "day", Optional.empty());

        assertNotNull(result);

        // Verify request was made correctly
        verify(getRequestedFor(urlPathEqualTo("/public/login_time_frame"))
                .withQueryParam("idp_id", equalTo(idp)));

        List<ServeEvent> events = wireMockRule.getAllServeEvents();
        String rawUrl = events.get(0).getRequest().getUrl();
        assertEquals("/public/login_time_frame?include_unique=true&idp_id=http://login.surf.nl/adfs/services/trust?q%3D1%26k%3D2&from=1000&to=2000&scale=day&epoch=ms", rawUrl);
    }}