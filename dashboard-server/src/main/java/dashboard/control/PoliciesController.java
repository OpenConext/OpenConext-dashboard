package dashboard.control;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;
import dashboard.domain.*;
import dashboard.mail.MailBox;
import dashboard.manage.EntityType;
import dashboard.manage.Manage;
import dashboard.pdp.PdpService;
import dashboard.util.SpringSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static dashboard.util.SpringSecurity.getCurrentUser;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.web.bind.annotation.RequestMethod.*;

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

    @Value("${dashboard.feature.stepup}")
    private boolean dashboardStepupEnabled;

    @RequestMapping(method = OPTIONS)
    public ResponseEntity<Void> options(HttpServletResponse response) {
        String allowHeaderValue = pdpService.isAvailable() ? ALLOWED_HEADER_VALUE : "";
        response.setHeader(HttpHeaders.ALLOW, allowHeaderValue);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_VIEWER','DASHBOARD_SUPER_USER')")
    @RequestMapping(method = GET)
    public RestResponse<List<Policy>> listPolicies() {
        return createRestResponse(pdpService.policies());
    }

    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_VIEWER','DASHBOARD_SUPER_USER')")
    @RequestMapping(method = POST)
    public ResponseEntity<RestResponse<Policy>> createPolicy(@RequestBody Policy policy) {
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        if (currentUser.getCurrentLoaLevel() < 2 && dashboardStepupEnabled) {
            LOG.warn("Consent endpoint requires LOA level 2 or higher, currentUser {}", currentUser);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return whenDashboardAdmin(() -> {
            LOG.debug("Create a policy: {}", policy);
            Optional<ServiceProvider> serviceProviderOptional = manage.getServiceProvider(policy.getServiceProviderId(), EntityType
                    .saml20_sp, false);
            ServiceProvider serviceProvider = serviceProviderOptional.orElseGet(() -> manage.getServiceProvider(policy.getServiceProviderId(), EntityType
                    .oidc10_rp, false).get());

            LOG.debug("PolicyEnforcementDecisionRequired:" + serviceProvider.isPolicyEnforcementDecisionRequired());

            if (!serviceProvider.isPolicyEnforcementDecisionRequired()) {
                sendNewPolicyWithoutEnforcementDecisionEnabledEmail(policy, getCurrentUser());
            }

            return createRestResponse(pdpService.create(policy));
        });
    }

    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_VIEWER','DASHBOARD_SUPER_USER')")
    @RequestMapping(method = PUT)
    public ResponseEntity<RestResponse<Policy>> updatePolicy(@RequestBody Policy policy) {
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        if (currentUser.getCurrentLoaLevel() < 2 && dashboardStepupEnabled) {
            LOG.warn("Consent endpoint requires LOA level 2 or higher, currentUser {}", currentUser);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        return whenDashboardAdmin(() -> {
            LOG.debug("Update a policy: {}", policy);
            return createRestResponse(pdpService.update(policy));
        });
    }

    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_VIEWER','DASHBOARD_SUPER_USER')")
    @RequestMapping(path = "/new", method = GET)
    public ResponseEntity<RestResponse<Policy>> newPolicy() {
        return whenDashboardAdmin(() -> createRestResponse(new Policy()));
    }

    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_VIEWER','DASHBOARD_SUPER_USER')")
    @RequestMapping(path = "/{id}", method = GET)
    public RestResponse<Policy> policy(@PathVariable("id") Long id) {
        return createRestResponse(pdpService.policy(id));
    }

    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_VIEWER','DASHBOARD_SUPER_USER')")
    @RequestMapping(path = "/{id}", method = DELETE)
    public void delete(@PathVariable("id") Long id) {
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        if (currentUser.getCurrentLoaLevel() < 2 && dashboardStepupEnabled) {
            String msg = String.format("Consent endpoint requires LOA level 2 or higher, currentUser %s", currentUser);
            LOG.warn(msg);
            throw new AuthorizationServiceException(msg);
        }
        whenDashboardAdmin(() -> createRestResponse(pdpService.delete(id)));
    }

    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_VIEWER','DASHBOARD_SUPER_USER')")
    @RequestMapping(path = "/{id}/revisions", method = GET)
    public RestResponse<List<Policy>> revisions(@PathVariable("id") Long id) {
        return createRestResponse(pdpService.revisions(id));
    }

    @RequestMapping(path = "/attributes", method = GET)
    public RestResponse<List<Attribute>> attributes() {
        return createRestResponse(pdpService.allowedAttributes());
    }

    private void sendNewPolicyWithoutEnforcementDecisionEnabledEmail(Policy policy, CoinUser user) {
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
        CoinUser currentUser = getCurrentUser();
        IdentityProvider idp = currentUser.getSwitchedToIdp().orElse(currentUser.getIdp());
        return (currentUser.isDashboardAdmin() || (idp != null && idp.isAllowMaintainersToManageAuthzRules())) ?
                ResponseEntity.ok(supplier.get()) : new ResponseEntity<T>(FORBIDDEN);
    }


}
