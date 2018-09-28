package selfservice.api.dashboard;

import com.google.common.collect.ImmutableList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import selfservice.domain.CoinUser;
import selfservice.domain.IdentityProvider;
import selfservice.domain.Provider;
import selfservice.domain.ServiceProvider;
import selfservice.manage.Manage;
import selfservice.util.SpringSecurity;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@RestController
@RequestMapping("/dashboard/api/stats")
public class StatsController implements Constants {

    private RestTemplate restTemplate;
    private String baseUrl;
    private Manage manage;

    @Autowired
    public StatsController(@Value("${statsUser}") String user,
                           @Value("${statsPassword}") String password,
                           @Value("${statsBaseUrl}") String baseUrl,
                           Manage manage) {
        this.restTemplate = new RestTemplate(clientHttpRequestFactory(7500));
        this.baseUrl = baseUrl;
        this.manage = manage;

        this.restTemplate.setInterceptors(ImmutableList.of((request, body, execution) -> {
            HttpHeaders headers = request.getHeaders();
            headers.setContentType(APPLICATION_JSON);
            headers.setAccept(ImmutableList.of(APPLICATION_JSON));
            headers.set(AUTHORIZATION, authorizationHeaderValue(user, password));

            return execution.execute(request, body);
        }));

    }

    //Used for retrieval of all logins for one SP
    @GetMapping("loginTimeFrame")
    public String loginTimeFrame(@RequestParam("from") long from,
                                 @RequestParam("to") long to,
                                 @RequestParam("scale") String scale,
                                 @RequestParam(value = "spEntityId") String spEntityId,
                                 @RequestParam(value = "state", required = false, defaultValue = "all") String state) {
        String url = String.format(
                "%s/public/login_time_frame?from=%s&to=%s&include_unique=true&scale=%s&epoch=ms&state=%s&idp_id=%s&sp_id=%s",
                baseUrl, from, to, scale, state, currentUserIdp(), spEntityId);
        return restTemplate.getForEntity(url, String.class).getBody();
    }

    //Used for retrieval of all logins for all SP's
    @GetMapping("loginAggregated")
    public String loginAggregated(@RequestParam("period") String period,
                                  @RequestParam(value = "state", required = false, defaultValue = "all") String state) {
        String url = String.format(
                "%s/public/login_aggregated?period=%s&include_unique=true&state=%s&idp_id=%s&group_by=sp_id",
                baseUrl, period, state, currentUserIdp());
        return restTemplate.getForEntity(url, String.class).getBody();
    }

    //Used for retrieval of all logins for one SP without a period
    @GetMapping("uniqueLoginCount")
    public String uniqueLoginCount(@RequestParam("from") long from,
                                   @RequestParam("to") long to,
                                   @RequestParam(value = "spEntityId") String spEntityId,
                                   @RequestParam(value = "state", required = false, defaultValue = "all") String state) {
        String url = String.format(
                "%s/public/unique_login_count?from=%s&to=%s&include_unique=true&epoch=ms&state=%s&idp_id=%s&sp_id=%s",
                baseUrl, from, to, state, currentUserIdp(), spEntityId);
        return restTemplate.getForEntity(url, String.class).getBody();
    }

    @GetMapping("serviceProviders")
    public List<Map<String, Object>> serviceProviders(Locale locale) {
        return manage.getLinkedServiceProviders(currentUserIdp()).stream().map(sp -> mapServiceProvider(sp, locale.getLanguage())).collect(Collectors.toList());
    }

    private Map<String, Object> mapServiceProvider(ServiceProvider sp, String language) {
        Map<String, Object> result = new HashMap<>();
        result.put("value", sp.getId());
        String nameEN = sp.getName(Provider.Language.EN);
        String nameNL = sp.getName(Provider.Language.NL);
        String name = ("en".equals(language) && StringUtils.hasText(nameEN) ? nameEN : nameNL);
        result.put("display", StringUtils.hasText(name) ? name : sp.getId());
        return result;
    }

    private String currentUserIdp() {
        CoinUser user = SpringSecurity.getCurrentUser();

        IdentityProvider idp = user.getSwitchedToIdp().orElse(user.getIdp());
        return idp.getId();
    }
}
