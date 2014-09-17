package nl.surfnet.coin.selfservice.api.rest;


import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import java.util.*;

import static nl.surfnet.coin.selfservice.api.rest.Constants.HTTP_X_IDP_ENTITY_ID;

@Controller
@RequestMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsersController extends BaseController {

  @Resource
  private Csa csa;

  @RequestMapping("/me")
  public ResponseEntity<RestResponse> me() {
    return new ResponseEntity(this.createRestResponse(SpringSecurity.getCurrentUser()), HttpStatus.OK);
  }

  @RequestMapping("/super/idps")
  public ResponseEntity<RestResponse> idps() {

    List<InstitutionIdentityProvider> idps = csa.getAllInstitutionIdentityProviders();
    Collections.sort(idps, new Comparator<InstitutionIdentityProvider>() {
      @Override
      public int compare(final InstitutionIdentityProvider lh, final InstitutionIdentityProvider rh) {
        return lh.getName().compareTo(rh.getName());
      }
    });

    List<String> roles = Arrays.asList(CoinAuthority.Authority.ROLE_DASHBOARD_VIEWER.name(), CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN.name());

    HashMap<String, List> payload = new HashMap<String, List>();
    payload.put("idps", idps);
    payload.put("roles", roles);

    return new ResponseEntity(this.createRestResponse(payload), HttpStatus.OK);
  }


  @RequestMapping("/me/switch-to-idp")
  public ResponseEntity currentIdp(@RequestParam("idpId") String switchToIdp, HttpServletResponse response) {
    SpringSecurity.setCurrentIdp(switchToIdp);
    response.setHeader(HTTP_X_IDP_ENTITY_ID, switchToIdp);
    return new ResponseEntity(HttpStatus.OK);
  }


}
