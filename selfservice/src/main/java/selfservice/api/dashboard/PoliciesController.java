package selfservice.api.dashboard;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import com.google.common.collect.ImmutableList;
import com.google.common.net.HttpHeaders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import selfservice.domain.Policy;
import selfservice.domain.Policy.Attribute;
import selfservice.domain.ServiceProvider;
import selfservice.pdp.PdpService;
import selfservice.serviceregistry.ServiceRegistry;
import selfservice.util.SpringSecurity;

@RestController
@RequestMapping("/dashboard/api/policies")
public class PoliciesController extends BaseController {

  private static final Logger LOG = LoggerFactory.getLogger(PoliciesController.class);
  private static final String ALLOWED_HEADER_VALUE = ImmutableList.of(GET, POST, PUT, DELETE).stream()
      .map(RequestMethod::name)
      .collect(Collectors.joining(","));

  @Autowired
  private PdpService pdpService;

  @Autowired
  private ServiceRegistry serviceRegistry;

  @Value("${dashboard.feature.policies}")
  protected boolean policiesEnabled;

  @RequestMapping(method = OPTIONS)
  public ResponseEntity<Void> options(HttpServletResponse response) {
    String allowHeaderValue = policiesEnabled && pdpService.isAvailable() ? ALLOWED_HEADER_VALUE : "";

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

      ServiceProvider serviceProvider = serviceRegistry.getServiceProvider(policy.getServiceProviderId()).get();
      if (!serviceProvider.isPolicyEnforcementDecisionRequired()) {
        LOG.warn("We should send an email");
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

  private <T> ResponseEntity<T> whenDashboardAdmin(Supplier<T> supplier) {
    return SpringSecurity.getCurrentUser().isDashboardAdmin()
        ? ResponseEntity.ok(supplier.get())
        : new ResponseEntity<T>(HttpStatus.FORBIDDEN);
  }

  @RequestMapping(path = "/{id}", method = GET)
  public RestResponse<Policy> policy(@PathVariable("id") Long id) {
    return createRestResponse(pdpService.policy(id));
  }

  @RequestMapping(path = "/{id}", method = DELETE)
  public void delete(@PathVariable("id") Long id) {
    pdpService.delete(id);
  }

  @RequestMapping(path = "/{id}/revisions", method = GET)
  public RestResponse<List<Policy>> revisions(@PathVariable("id") Long id) {
    return createRestResponse(pdpService.revisions(id));
  }

  @RequestMapping(path = "/attributes", method = GET)
  public RestResponse<List<Attribute>> attributes() {
    return createRestResponse(pdpService.allowedAttributes());
  }

}
