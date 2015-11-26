package selfservice.api.rest;

import selfservice.domain.InstitutionIdentityProvider;
import selfservice.domain.LicenseContactPerson;
import selfservice.service.Csa;
import selfservice.util.SpringSecurity;
import selfservice.sab.Sab;
import selfservice.sab.SabPerson;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.*;

import static selfservice.api.rest.Constants.HTTP_X_IDP_ENTITY_ID;

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
    return new ResponseEntity<RestResponse>(createRestResponse(roleAssignments), HttpStatus.OK);
  }

  @RequestMapping("/licensecontactpersons")
  public ResponseEntity<RestResponse> licenseContactPerson(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    List<LicenseContactPerson> licenseContactPersons = csa.licenseContactPersons(idpEntityId);
    return new ResponseEntity<RestResponse>(createRestResponse(licenseContactPersons), HttpStatus.OK);
  }

}
