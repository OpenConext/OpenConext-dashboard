package selfservice.api.dashboard;

import static java.util.function.Function.identity;
import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import selfservice.domain.IdentityProvider;
import selfservice.domain.LicenseContactPerson;
import selfservice.sab.Sab;
import selfservice.sab.SabPerson;
import selfservice.util.LicenseContactPersonService;
import selfservice.util.SpringSecurity;

@Controller
@RequestMapping(value = "/dashboard/api/idp", produces = MediaType.APPLICATION_JSON_VALUE)
public class IdpController extends BaseController {

  public static final List<String> INTERESTING_ROLES = ImmutableList.of("SURFconextbeheerder", "SURFconextverantwoordelijke");

  @Autowired
  private Sab sabClient;

  @Autowired
  private LicenseContactPersonService licenseContactPersonService;

  @RequestMapping("/current/roles")
  public ResponseEntity<RestResponse<Map<String, Collection<SabPerson>>>> roles(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    Map<String, Collection<SabPerson>> roleAssignments = SpringSecurity.getCurrentUser().getByEntityId(idpEntityId)
        .map(this::personsInRole)
        .orElse(ImmutableMap.of());

    return new ResponseEntity<>(createRestResponse(roleAssignments), HttpStatus.OK);
  }

  private Map<String, Collection<SabPerson>> personsInRole(IdentityProvider idp) {
    if (Strings.isNullOrEmpty(idp.getInstitutionId())) {
      return ImmutableMap.of();
    }

    return INTERESTING_ROLES.stream().collect(Collectors.toMap(
        identity(),
        role -> sabClient.getPersonsInRoleForOrganization(idp.getInstitutionId(), role)));
  }

  @RequestMapping("/licensecontactpersons")
  public ResponseEntity<RestResponse<List<LicenseContactPerson>>> licenseContactPerson(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
    return new ResponseEntity<>(createRestResponse(licenseContactPersonService.licenseContactPersons(idpEntityId)), HttpStatus.OK);
  }

}
