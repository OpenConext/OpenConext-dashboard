package selfservice.api.rest;

import selfservice.domain.CoinAuthority;
import selfservice.domain.CoinUser;
import selfservice.domain.InstitutionIdentityProvider;
import selfservice.service.Csa;
import selfservice.util.SpringSecurity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsersController extends BaseController {

  @Resource
  private Csa csa;

  @RequestMapping("/me")
  public ResponseEntity<RestResponse> me() {
    return new ResponseEntity<>(createRestResponse(SpringSecurity.getCurrentUser()), HttpStatus.OK);
  }

  @RequestMapping("/super/idps")
  public ResponseEntity<RestResponse> idps() {
    CoinUser currentUser = SpringSecurity.getCurrentUser();
    if (!currentUser.isSuperUser()) {
      return new ResponseEntity<RestResponse>(HttpStatus.FORBIDDEN);
    }

    List<InstitutionIdentityProvider> idps = csa.getAllInstitutionIdentityProviders();
    Collections.sort(idps, (lh, rh) -> lh.getName().compareTo(rh.getName()));

    List<String> roles = Arrays.asList(CoinAuthority.Authority.ROLE_DASHBOARD_VIEWER.name(), CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN.name());

    HashMap<String, List<?>> payload = new HashMap<>();
    payload.put("idps", idps);
    payload.put("roles", roles);

    return new ResponseEntity<>(createRestResponse(payload), HttpStatus.OK);
  }

  @RequestMapping("/me/switch-to-idp")
  public ResponseEntity<RestResponse> currentIdp(@RequestParam(value = "idpId", required = false) String switchToIdp, @RequestParam(value = "role", required = false) String role, HttpServletResponse response) {
    SpringSecurity.setSwitchedToIdp(csa, switchToIdp, role);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
