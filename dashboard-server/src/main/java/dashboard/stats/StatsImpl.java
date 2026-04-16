package dashboard.stats;

import com.google.common.collect.ImmutableList;
import dashboard.control.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@SuppressWarnings("unchecked")
public class StatsImpl implements Stats, Constants {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    private static final Logger LOG = LoggerFactory.getLogger(StatsImpl.class);

    public StatsImpl(@Value("${statsUser}") String user,
                     @Value("${statsPassword}") String password,
                     @Value("${statsBaseUrl}") String baseUrl) {
        this.restTemplate = new RestTemplate(clientHttpRequestFactory(10 * 1000));
        this.baseUrl = baseUrl;

        this.restTemplate.setInterceptors(ImmutableList.of((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.setContentType(APPLICATION_JSON);
            headers.setAccept(ImmutableList.of(APPLICATION_JSON));
            headers.set(AUTHORIZATION, authorizationHeaderValue(user, password));

            if (LOG.isDebugEnabled()) {
                LOG.debug("Outgoing request URI: " + request.getURI());
            }

            return execution.execute(request, body);
        }));
    }

    public List<Object> loginTimeFrame(long from, long to, String scale, Optional<String> spEntityIdOptional) {
        UriComponentsBuilder builder = baseBuilder("/public/login_time_frame")
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("scale", scale)
                .queryParam("epoch", "ms");
        spEntityIdOptional.ifPresent(sp -> builder.queryParam("sp_id", sp));

        URI uri = builder.build().encode().toUri();
        return restTemplate.getForEntity(uri, List.class).getBody();
    }

    public List<Object> loginAggregated(String period, Optional<String> spEntityIdOptional) {
        UriComponentsBuilder builder = baseBuilder("/public/login_aggregated")
                .queryParam("period", period)
                .queryParam("group_by", "sp_id");
        spEntityIdOptional.ifPresent(sp -> builder.queryParam("sp_id", sp));

        URI uri = builder.build().encode().toUri();
        return restTemplate.getForEntity(uri, List.class).getBody();
    }

    public List<Object> uniqueLoginCount(long from, long to, String spEntityId) {
        UriComponentsBuilder builder = baseBuilder("/public/unique_login_count")
                .queryParam("from", from)
                .queryParam("to", to)
                .queryParam("epoch", "ms")
                .queryParam("sp_id", spEntityId);

        URI uri = builder.build().encode().toUri();
        return restTemplate.getForEntity(uri, List.class).getBody();
    }

    private UriComponentsBuilder baseBuilder(String path) {
        return UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path(path)
                .queryParam("include_unique", true)
                .queryParam("idp_id", getCurrentUserIdp());
    }

    protected String getCurrentUserIdp() {
        return currentUserIdp();
    }

}

