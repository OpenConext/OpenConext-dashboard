package nl.surfnet.coin.selfservice.api.rest;


import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.OfferedService;
import nl.surfnet.sab.Sab;
import nl.surfnet.sab.SabPerson;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.*;

import static nl.surfnet.coin.selfservice.api.rest.Constants.HTTP_X_IDP_ENTITY_ID;

@Controller
@RequestMapping(value = "/idp", produces = MediaType.APPLICATION_JSON_VALUE)
public class IdpController extends BaseController {
  public static final List<String> INTERESTING_ROLES = Arrays.asList("SURFconextbeheerder", "SURFconextverantwoordelijke");

  @Resource
  private Sab sabClient;

  @Resource
  private Csa csa;

  @RequestMapping("/current/roles")
  public ResponseEntity<RestResponse> roles(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    Map<String, Collection<SabPerson>> roleAssignments = new HashMap<>();
    for (final String role : INTERESTING_ROLES) {
      roleAssignments.put(role, sabClient.getPersonsInRoleForOrganization(idpEntityId, role));
    }
    return new ResponseEntity(createRestResponse(roleAssignments), HttpStatus.OK);
  }

  @RequestMapping("/current/services")
  public ResponseEntity<RestResponse> services(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    List<OfferedService> offeredServices = csa.findOfferedServicesFor(idpEntityId);
    return new ResponseEntity(createRestResponse(offeredServices), HttpStatus.OK);
  }
}
