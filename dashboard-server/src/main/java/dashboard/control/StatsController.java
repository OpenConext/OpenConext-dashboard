package dashboard.control;

import dashboard.domain.CoinUser;
import dashboard.domain.IdentityProvider;
import dashboard.domain.InstitutionIdentityProvider;
import dashboard.manage.EntityType;
import dashboard.util.SpringSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import dashboard.domain.Provider;
import dashboard.domain.ServiceProvider;
import dashboard.manage.Manage;
import dashboard.stats.Stats;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

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
    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_VIEWER','DASHBOARD_SUPER_USER')")
    @GetMapping("loginTimeFrame")
    public List<Object> loginTimeFrame(@RequestParam("from") long from,
                                                    @RequestParam("to") long to,
                                                    @RequestParam("scale") String scale,
                                                    @RequestParam(value = "spEntityId", required = false) Optional<String> spEntityId) {
        return stats.loginTimeFrame(from, to, scale, spEntityId);
    }

    //Used for retrieval of all logins for all SP's
    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_VIEWER','DASHBOARD_SUPER_USER')")
    @GetMapping("loginAggregated")
    public List<Object> loginAggregated(@RequestParam("period") String period,
                                                     @RequestParam(value = "spEntityId", required = false) Optional<String> spEntityId) {
        return stats.loginAggregated(period,spEntityId);
    }

    //Used for retrieval of all logins for one SP without a period
    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_VIEWER','DASHBOARD_SUPER_USER')")
    @GetMapping("uniqueLoginCount")
    public List<Object> uniqueLoginCount(@RequestParam("from") long from,
                                                      @RequestParam("to") long to,
                                                      @RequestParam(value = "spEntityId") String spEntityId) {
        return stats.uniqueLoginCount(from, to, spEntityId);
    }

    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_VIEWER','DASHBOARD_SUPER_USER')")
    @GetMapping("serviceProviders")
    public List<Map<String, Object>> serviceProviders(Locale locale) {
        CoinUser user = SpringSecurity.getCurrentUser();
        IdentityProvider idp = user.getSwitchedToIdp().orElse(user.getIdp());
        List<ServiceProvider> sps;
        if (idp.isAllowedAll()) {
            sps = manage.getLinkedServiceProviders(idp.getId());
        } else {
            sps = idp.getAllowedEntityIds().stream()
                    .map(entityId -> manage.getServiceProvider(entityId, EntityType.saml20_sp, false))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(toList());
        }
        return sps.stream().map(sp -> mapServiceProvider(sp, locale.getLanguage())).collect(Collectors.toList());
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
