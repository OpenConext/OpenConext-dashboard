package selfservice.api.dashboard;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Joiner;
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
import org.springframework.web.bind.annotation.RestController;

import selfservice.domain.Policy;
import selfservice.domain.Policy.Attribute;
import selfservice.service.PdpService;

@RestController
@RequestMapping("/dashboard/api/policies")
public class PoliciesController extends BaseController {

  private static final Logger LOG = LoggerFactory.getLogger(PoliciesController.class);

  @Autowired
  private PdpService pdpService;

  @Value("${dashboard.feature.policies}") boolean policiesEnabled;

  @RequestMapping(method = OPTIONS)
  public ResponseEntity<Void> options(HttpServletResponse response) {
    if (policiesEnabled) {
      // should add check if pdp is available with protected api
      response.setHeader(HttpHeaders.ALLOW, Joiner.on(",").join(ImmutableList.of(GET, POST, PUT, DELETE)));
    }

    return new ResponseEntity<Void>(HttpStatus.OK);
  }

  @RequestMapping(method = GET)
  public RestResponse<List<Policy>> listPolicies() {
    return createRestResponse(pdpService.policies());
  }

  @RequestMapping(method = POST)
  public RestResponse<Policy> createPolicy(@RequestBody Policy policy) {
    LOG.info("Create a policy: {}", policy);
    return createRestResponse(pdpService.create(policy));
  }

  @RequestMapping(method = PUT)
  public RestResponse<Policy> updatePoliciy(@RequestBody Policy policy) {
    LOG.info("Update a policy: {}", policy);
    return createRestResponse(pdpService.update(policy));
  }

  @RequestMapping(path = "/new", method = GET)
  public RestResponse<Policy> newPolicy() {
    return createRestResponse(new Policy());
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
