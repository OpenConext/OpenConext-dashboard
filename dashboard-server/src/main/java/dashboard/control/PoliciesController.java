package dashboard.control;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;
import dashboard.mail.MailBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import dashboard.domain.CoinUser;
import dashboard.domain.Policy;
import dashboard.domain.Policy.Attribute;
import dashboard.domain.ServiceProvider;
import dashboard.manage.EntityType;
import dashboard.manage.Manage;
import dashboard.pdp.PdpService;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;
import static dashboard.util.SpringSecurity.getCurrentUser;

@RestController
@RequestMapping("/dashboard/api/policies")
public class PoliciesController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(PoliciesController.class);
    private static final String ALLOWED_HEADER_VALUE = ImmutableList.of(GET, POST, PUT, DELETE).stream()
        .map(RequestMethod::name)
        .collect(Collectors.joining(","));

    @Value("${dashboard.environment}")
    protected String environment;
    @Autowired
    private PdpService pdpService;
    @Autowired
    private Manage manage;
    @Autowired
    private MailBox mailBox;

    @RequestMapping(method = OPTIONS)
    public ResponseEntity<Void> options(HttpServletResponse response) {
        String allowHeaderValue = pdpService.isAvailable() ? ALLOWED_HEADER_VALUE : "";
        response.setHeader(HttpHeaders.ALLOW, allowHeaderValue);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @RequestMapping(method = GET)
    public RestResponse<List<Policy>> listPolicies() {
        return createRestResponse(pdpService.policies());
    }

    @RequestMapping(method = POST)
    public ResponseEntity<RestResponse<Policy>> createPolicy(@RequestBody Policy policy) {
        return whenDashboardAdmin(() -> {
            LOG.debug("Create a policy: {}", policy);

            ServiceProvider serviceProvider = manage.getServiceProvider(policy.getServiceProviderId(), EntityType
                .saml20_sp, false).get();
            LOG.debug("PolicyEnforcementDecisionRequired:" + serviceProvider.isPolicyEnforcementDecisionRequired());
            if (!serviceProvider.isPolicyEnforcementDecisionRequired()) {
                sendNewPolicyWithoutEnforcementDecisionEnabledEmail(policy, getCurrentUser());
            }

            return createRestResponse(pdpService.create(policy));
        });
    }

    @RequestMapping(method = PUT)
    public ResponseEntity<RestResponse<Policy>> updatePoliciy(@RequestBody Policy policy) {
        return whenDashboardAdmin(() -> {
            LOG.debug("Update a policy: {}", policy);
            return createRestResponse(pdpService.update(policy));
        });
    }

    @RequestMapping(path = "/new", method = GET)
    public ResponseEntity<RestResponse<Policy>> newPolicy() {
        return whenDashboardAdmin(() -> createRestResponse(new Policy()));
    }

    @RequestMapping(path = "/{id}", method = GET)
    public RestResponse<Policy> policy(@PathVariable("id") Long id) {
        return createRestResponse(pdpService.policy(id));
    }

    @RequestMapping(path = "/{id}", method = DELETE)
    public void delete(@PathVariable("id") Long id) {
        whenDashboardAdmin(() -> createRestResponse(pdpService.delete(id)));
    }

    @RequestMapping(path = "/{id}/revisions", method = GET)
    public RestResponse<List<Policy>> revisions(@PathVariable("id") Long id) {
        return createRestResponse(pdpService.revisions(id));
    }

    @RequestMapping(path = "/attributes", method = GET)
    public RestResponse<List<Attribute>> attributes() {
        return createRestResponse(pdpService.allowedAttributes());
    }

    private void sendNewPolicyWithoutEnforcementDecisionEnabledEmail(Policy policy, CoinUser user)  {
        String subject = String.format("Nieuwe autorisatieregel '%s' voor de omgeving '%s'", policy.getServiceProviderName(), environment);

        StringBuilder body = new StringBuilder();
        body.append(String.format(
            "Voor %s is voor het eerst een autorisatieregel (met naam %s) aangemaakt door %s (%s) van %s.\n",
            StringUtils.hasText(policy.getServiceProviderName()) ?
                policy.getServiceProviderName() : policy.getServiceProviderId(), policy.getName(), user
                .getDisplayName(), user.getEmail(), user.getInstitutionId()));
        body.append("In Manage staat voor die dienst nog NIET geconfigureerd dat er moet worden gecontroleerd op " +
            "policies.\n");
        body.append("Als na controle van de regel in de PDP die regel goed lijkt, voeg dan in Manage in het " +
            "Meta-tabblad de Entry 'coin:policy_enforcement_decision_required' toe aan de dienst, ");
        body.append("push de metadata en informeer de aanmaker van de regel.");

        try {
            mailBox.sendAdministrativeMail(body.toString(), subject);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    private <T> ResponseEntity<T> whenDashboardAdmin(Supplier<T> supplier) {
        return getCurrentUser().isDashboardAdmin() ? ResponseEntity.ok(supplier.get()) : new ResponseEntity<T>(FORBIDDEN);
    }


}
