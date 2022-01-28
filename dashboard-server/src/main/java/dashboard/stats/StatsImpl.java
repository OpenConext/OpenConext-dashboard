package dashboard.stats;

import com.google.common.collect.ImmutableList;
import dashboard.control.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class StatsImpl implements Stats, Constants {

    private RestTemplate restTemplate;
    private String baseUrl;

    @Autowired
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

            return execution.execute(request, body);
        }));
    }

    public List<Object> loginTimeFrame(long from, long to, String scale, Optional<String> spEntityIdOptional) {
        StringBuilder url = new StringBuilder(String.format(
                "%s/public/login_time_frame?from=%s&to=%s&include_unique=true&scale=%s&epoch=ms&idp_id=%s",
                baseUrl, from, to, scale, currentUserIdp()));
        spEntityIdOptional.ifPresent(spEntityId -> url.append(String.format("&sp_id=%s", spEntityId)));
        return restTemplate.getForEntity(url.toString(), List.class).getBody();
    }

    public List<Object> loginAggregated(String period, Optional<String> spEntityIdOptional) {
        StringBuilder url = new StringBuilder(String.format(
                "%s/public/login_aggregated?period=%s&include_unique=true&idp_id=%s&group_by=sp_id",
                baseUrl, period, currentUserIdp()));
        spEntityIdOptional.ifPresent(spEntityId -> url.append(String.format("&sp_id=%s", spEntityId)));
        return restTemplate.getForEntity(url.toString(), List.class).getBody();
    }

    public List<Object> uniqueLoginCount(long from, long to, String spEntityId) {
        String url = String.format(
                "%s/public/unique_login_count?from=%s&to=%s&include_unique=true&epoch=ms&idp_id=%s&sp_id=%s",
                baseUrl, from, to, currentUserIdp(), spEntityId);
        return restTemplate.getForEntity(url, List.class).getBody();
    }

}

