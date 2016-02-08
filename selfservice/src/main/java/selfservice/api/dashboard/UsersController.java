package selfservice.api.dashboard;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import selfservice.domain.CoinAuthority;
import selfservice.domain.CoinUser;
import selfservice.domain.IdentityProvider;
import selfservice.service.IdentityProviderService;
import selfservice.util.SpringSecurity;

@Controller
@RequestMapping(value = "/dashboard/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsersController extends BaseController {

  @Autowired
  private IdentityProviderService idpService;

  @RequestMapping("/me")
  public ResponseEntity<RestResponse<CoinUser>> me() {
    return new ResponseEntity<>(createRestResponse(SpringSecurity.getCurrentUser()), HttpStatus.OK);
  }

  @RequestMapping("/super/idps")
  public ResponseEntity<RestResponse<Map<String, List<?>>>> idps() {
    CoinUser currentUser = SpringSecurity.getCurrentUser();
    if (!currentUser.isSuperUser()) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    List<IdentityProvider> idps = Lists.newArrayList(idpService.getAllIdentityProviders());
    idps.sort((lh, rh) -> lh.getName().compareTo(rh.getName()));

    List<String> roles = Arrays.asList(CoinAuthority.Authority.ROLE_DASHBOARD_VIEWER.name(), CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN.name());

    HashMap<String, List<?>> payload = new HashMap<>();
    payload.put("idps", idps);
    payload.put("roles", roles);

    return new ResponseEntity<>(createRestResponse(payload), HttpStatus.OK);
  }

  @RequestMapping("/me/switch-to-idp")
  public ResponseEntity<Void> currentIdp(
      @RequestParam(value = "idpId", required = false) String switchToIdp,
      @RequestParam(value = "role", required = false) String role,
      HttpServletResponse response) {

    if (Strings.isNullOrEmpty(switchToIdp)) {
      SpringSecurity.clearSwitchedIdp();
    } else {
      IdentityProvider identityProvider = idpService.getIdentityProvider(switchToIdp)
          .orElseThrow(() -> new SecurityException(switchToIdp + " does not exist"));

      SpringSecurity.setSwitchedToIdp(identityProvider, role);
    }

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
