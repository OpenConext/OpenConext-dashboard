package selfservice.api.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import selfservice.domain.CoinUser;
import selfservice.domain.IdentityProvider;
import selfservice.domain.Provider;
import selfservice.domain.ServiceProvider;
import selfservice.manage.Manage;
import selfservice.stats.Stats;
import selfservice.util.SpringSecurity;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard/api/stats")
public class StatsController implements Constants {

    private Manage manage;
    private Stats stats;

    @Autowired
    public StatsController(Stats stats,
                           Manage manage) {
        this.manage = manage;
        this.stats = stats;
    }

    //Used for retrieval of all logins for one SP
    @GetMapping("loginTimeFrame")
    public List<Map<String, Object>> loginTimeFrame(@RequestParam("from") long from,
                                                    @RequestParam("to") long to,
                                                    @RequestParam("scale") String scale,
                                                    @RequestParam(value = "spEntityId", required = false) Optional<String> spEntityId,
                                                    @RequestParam(value = "state", required = false, defaultValue = "all") String state) {
        return stats.loginTimeFrame(from, to, scale, spEntityId, state);
    }

    //Used for retrieval of all logins for all SP's
    @GetMapping("loginAggregated")
    public List<Map<String, Object>> loginAggregated(@RequestParam("period") String period,
                                                     @RequestParam(value = "spEntityId", required = false) Optional<String> spEntityId,
                                                     @RequestParam(value = "state", required = false, defaultValue = "all") String state) {
        return stats.loginAggregated(period,spEntityId, state);
    }

    //Used for retrieval of all logins for one SP without a period
    @GetMapping("uniqueLoginCount")
    public List<Map<String, Object>> uniqueLoginCount(@RequestParam("from") long from,
                                                      @RequestParam("to") long to,
                                                      @RequestParam(value = "spEntityId") String spEntityId,
                                                      @RequestParam(value = "state", required = false, defaultValue = "all") String state) {
        return stats.uniqueLoginCount(from, to, spEntityId, state);
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

}
