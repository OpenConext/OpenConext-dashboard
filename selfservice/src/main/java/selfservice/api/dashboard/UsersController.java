package selfservice.api.dashboard;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;
import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import selfservice.cache.ServicesCache;
import selfservice.domain.CoinAuthority.Authority;
import selfservice.service.ActionsService;
import selfservice.domain.Action;
import selfservice.domain.CoinUser;
import selfservice.domain.IdentityProvider;
import selfservice.domain.Service;
import selfservice.domain.Settings;
import selfservice.serviceregistry.ServiceRegistry;
import selfservice.util.SpringSecurity;

@RestController
@RequestMapping(value = "/dashboard/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsersController extends BaseController {

  @Autowired
  private ServiceRegistry serviceRegistry;

  @Autowired
  private ServicesCache servicesCache;
  
  @Autowired
  private ActionsService actionsService;


  @RequestMapping("/me")
  public RestResponse<CoinUser> me() {
    return createRestResponse(SpringSecurity.getCurrentUser());
  }

  @RequestMapping("/super/idps")
  public ResponseEntity<RestResponse<Map<String, List<?>>>> idps() {
    CoinUser currentUser = SpringSecurity.getCurrentUser();

    if (!currentUser.isSuperUser()) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    List<IdentityProvider> idps = serviceRegistry.getAllIdentityProviders().stream()
        .sorted((lh, rh) -> lh.getName().compareTo(rh.getName()))
        .collect(toList());

    List<String> roles = Arrays.asList(Authority.ROLE_DASHBOARD_VIEWER.name(), Authority.ROLE_DASHBOARD_ADMIN.name());

    HashMap<String, List<?>> payload = new HashMap<>();
    payload.put("idps", idps);
    payload.put("roles", roles);

    return new ResponseEntity<>(createRestResponse(payload), HttpStatus.OK);
  }

  @RequestMapping(value = "/me/guest-enabled-services", method = RequestMethod.GET)
  public RestResponse<List<Service>> guestEnabledServiceProviders(Locale locale) {
    String usersInstitutionId = SpringSecurity.getCurrentUser().getInstitutionId();

    List<Service> usersServices = isNullOrEmpty(usersInstitutionId) ? Collections.emptyList()
        : servicesCache.getAllServices(locale.getLanguage()).stream()
            .filter(service -> usersInstitutionId.equals(service.getInstitutionId()))
            .filter(service -> serviceRegistry
                .getLinkedIdentityProviders(service.getSpEntityId())
                .stream()
                .map(IdentityProvider::getId)
                .collect(toList())
                .contains("https://www.onegini.me")
            )
            .collect(toList());

    return createRestResponse(usersServices);
  }
  
  @RequestMapping(value = "/me/serviceproviders", method = RequestMethod.GET)
  public RestResponse<List<Service>> serviceProviders(Locale locale) {
    String usersInstitutionId = SpringSecurity.getCurrentUser().getInstitutionId();

    List<Service> usersServices = isNullOrEmpty(usersInstitutionId) ? Collections.emptyList()
        : servicesCache.getAllServices(locale.getLanguage()).stream()
            .filter(service -> usersInstitutionId.equals(service.getInstitutionId()))
            .collect(toList());

    return createRestResponse(usersServices);
  }

  @RequestMapping("/me/switch-to-idp")
  public ResponseEntity<Void> currentIdp(
      @RequestParam(value = "idpId", required = false) String switchToIdp,
      @RequestParam(value = "role", required = false) String role,
      HttpServletResponse response) {

    if (isNullOrEmpty(switchToIdp)) {
      SpringSecurity.clearSwitchedIdp();
    } else {
      IdentityProvider identityProvider = serviceRegistry.getIdentityProvider(switchToIdp)
          .orElseThrow(() -> new SecurityException(switchToIdp + " does not exist"));

      SpringSecurity.setSwitchedToIdp(identityProvider, role);
    }

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
  
  @RequestMapping(value = "/me/settings", method = RequestMethod.POST)
  public ResponseEntity<RestResponse<Action>> connect(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId, @RequestBody Settings settings) {
    CoinUser currentUser = SpringSecurity.getCurrentUser();
    if (currentUser.isSuperUser() || (!currentUser.isDashboardAdmin() && currentUser.isDashboardViewer())) {
      new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    if (isNullOrEmpty(currentUser.getIdp().getInstitutionId())) {
      new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    Action action = Action.builder()
        .userEmail(currentUser.getEmail())
        .userName(currentUser.getUsername())
        .idpId(idpEntityId)
        .settings(settings)
        .type(Action.Type.QUESTION).build();

    actionsService.create(action);

    return ResponseEntity.ok(createRestResponse(action));
  }

}
