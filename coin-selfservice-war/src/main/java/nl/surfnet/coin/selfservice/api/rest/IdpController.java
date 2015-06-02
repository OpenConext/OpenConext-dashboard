package nl.surfnet.coin.selfservice.api.rest;

import nl.surfnet.coin.selfservice.domain.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.service.Csa;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
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
    Optional<InstitutionIdentityProvider> institutionIdentityProvider = SpringSecurity.getCurrentUser().getByEntityId(idpEntityId);
    if (institutionIdentityProvider.isPresent()) {
      for (final String role : INTERESTING_ROLES) {
        Collection<SabPerson> rolesForOrganization = sabClient.getPersonsInRoleForOrganization(
          institutionIdentityProvider.get().getInstitutionId(),
          role
        );
        roleAssignments.put(role, rolesForOrganization);
      }

    }
    return new ResponseEntity(createRestResponse(roleAssignments), HttpStatus.OK);
  }

}
