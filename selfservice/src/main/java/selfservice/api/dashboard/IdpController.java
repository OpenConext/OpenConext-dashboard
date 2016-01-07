package selfservice.api.dashboard;

import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import selfservice.sab.Sab;
import selfservice.sab.SabPerson;
import selfservice.util.LicenseContactPersonService;
import selfservice.util.SpringSecurity;

@Controller
@RequestMapping(value = "/dashboard/api/idp", produces = MediaType.APPLICATION_JSON_VALUE)
public class IdpController extends BaseController {
  public static final List<String> INTERESTING_ROLES = Arrays.asList("SURFconextbeheerder", "SURFconextverantwoordelijke");

  @Autowired
  private Sab sabClient;

  @Autowired
  private LicenseContactPersonService licenseContactPersonService;

  @RequestMapping("/current/roles")
  public ResponseEntity<RestResponse> roles(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    Map<String, Collection<SabPerson>> roleAssignments = new HashMap<>();

    SpringSecurity.getCurrentUser().getByEntityId(idpEntityId).ifPresent(idp -> {
      for (final String role : INTERESTING_ROLES) {
        Collection<SabPerson> rolesForOrganization = sabClient.getPersonsInRoleForOrganization(idp.getInstitutionId(), role);
        roleAssignments.put(role, rolesForOrganization);
      }
    });

    return new ResponseEntity<RestResponse>(createRestResponse(roleAssignments), HttpStatus.OK);
  }

  @RequestMapping("/licensecontactpersons")
  public ResponseEntity<RestResponse> licenseContactPerson(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    return new ResponseEntity<RestResponse>(createRestResponse(licenseContactPersonService.licenseContactPersons(idpEntityId)), HttpStatus.OK);
  }

}
