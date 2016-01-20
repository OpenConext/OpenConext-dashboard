package selfservice.api.dashboard;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;

import com.google.common.collect.ImmutableList;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import selfservice.domain.Policy;

@RestController("/dashboard/api/policies")
public class PoliciesController extends BaseController {

  @RequestMapping(method = GET)
  public RestResponse<List<Policy>> listPolicies() {
    return createRestResponse(ImmutableList.of(
      new Policy("policyName1", "policyDescription1"),
      new Policy("policyName2", "policyDescription2")
    ));
  }

}
