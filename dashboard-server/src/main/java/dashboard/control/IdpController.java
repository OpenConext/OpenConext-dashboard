package dashboard.control;

import static java.util.function.Function.identity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import dashboard.domain.CoinUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import dashboard.domain.IdentityProvider;
import dashboard.sab.Sab;
import dashboard.sab.SabPerson;
import dashboard.util.SpringSecurity;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/dashboard/api/idp", produces = MediaType.APPLICATION_JSON_VALUE)
public class IdpController extends BaseController {

    private static final Logger LOG = LoggerFactory.getLogger(IdpController.class);

    public static final List<String> INTERESTING_ROLES = ImmutableList.of("SURFconextbeheerder", "SURFconextverantwoordelijke");

    @Autowired
    private Sab sabClient;

    @RequestMapping("/current/roles")
    public ResponseEntity<RestResponse<Map<String, Collection<SabPerson>>>> roles(@RequestHeader(Constants.HTTP_X_IDP_ENTITY_ID) String idpEntityId) {
        Map<String, Collection<SabPerson>> roleAssignments = SpringSecurity.getCurrentUser().getByEntityId(idpEntityId)
                .map(idp -> this.personsInRole(idp.getInstitutionId()))
                .orElse(ImmutableMap.of());

        return new ResponseEntity<>(createRestResponse(roleAssignments), HttpStatus.OK);
    }

    @RequestMapping("/sab/roles")
    public ResponseEntity<RestResponse<Map<String, Collection<SabPerson>>>> sabRoles(@RequestParam("institutionId") String institutionId) {
        CoinUser currentUser = SpringSecurity.getCurrentUser();

        if (!currentUser.isSuperUser()) {
            LOG.warn("Sab roles endpoint is only allowed for superUser, not for {}", currentUser);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        Map<String, Collection<SabPerson>> roles = personsInRole(institutionId);
        return new ResponseEntity<>(createRestResponse(roles), HttpStatus.OK);
    }

    private Map<String, Collection<SabPerson>> personsInRole(String institutionId) {
        if (Strings.isNullOrEmpty(institutionId)) {
            return ImmutableMap.of();
        }
        return INTERESTING_ROLES.stream().collect(Collectors.toMap(
                identity(),
                role -> sabClient.getPersonsInRoleForOrganization(institutionId, role)));
    }

}
