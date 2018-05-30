package selfservice.api.dashboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import selfservice.domain.Action;
import selfservice.domain.Change;
import selfservice.domain.CoinAuthority.Authority;
import selfservice.domain.CoinUser;
import selfservice.domain.IdentityProvider;
import selfservice.domain.Provider;
import selfservice.domain.Service;
import selfservice.domain.Settings;
import selfservice.domain.csa.ContactPerson;
import selfservice.manage.Manage;
import selfservice.service.ActionsService;
import selfservice.service.Services;
import selfservice.util.SpringSecurity;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;
import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;

@RestController
@RequestMapping(value = "/dashboard/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsersController extends BaseController {

  @Autowired
  private Manage manage;

  @Autowired
  private Services services;

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

    List<IdentityProvider> idps = manage.getAllIdentityProviders().stream()
        .sorted(Comparator.comparing(Provider::getName))
        .collect(toList());

    List<String> roles = Arrays.asList(Authority.ROLE_DASHBOARD_VIEWER.name(), Authority.ROLE_DASHBOARD_ADMIN.name());

    HashMap<String, List<?>> payload = new HashMap<>();
    payload.put("idps", idps);
    payload.put("roles", roles);

    return new ResponseEntity<>(createRestResponse(payload), HttpStatus.OK);
  }

  @RequestMapping(value = "/me/guest-enabled-services", method = RequestMethod.GET)
  public RestResponse<List<Service>> guestEnabledServiceProviders(Locale locale) {
    List<Service> usersServices = fetchGuestEnabledServiceProviders(locale);
    return createRestResponse(usersServices);
  }

  @RequestMapping(value = "/me/serviceproviders", method = RequestMethod.GET)
  public RestResponse<List<Service>> serviceProviders(Locale locale) throws IOException {
    List<Service> usersServices = getServiceProvidersForCurrentUser(locale);

    return createRestResponse(usersServices);
  }

  private List<Service> getServiceProvidersForCurrentUser(Locale locale) throws IOException {
    CoinUser currentUser = SpringSecurity.getCurrentUser();
    Optional<IdentityProvider> switchedToIdp = currentUser.getSwitchedToIdp();
    //We can not map as a null value is converted to an empty Optional
    String usersInstitutionId = switchedToIdp.isPresent() ? switchedToIdp.get().getInstitutionId() : currentUser.getInstitutionId();

    return isNullOrEmpty(usersInstitutionId) ? Collections.emptyList()
      : services.getInstitutionalServicesForIdp(usersInstitutionId);
  }

  private List<Service> fetchGuestEnabledServiceProviders(Locale locale) {
    String usersInstitutionId = SpringSecurity.getCurrentUser().getInstitutionId();

    return isNullOrEmpty(usersInstitutionId) ? Collections.emptyList()
      : services.getAllServices(locale.getLanguage()).stream()
      .filter(service -> usersInstitutionId.equals(service.getInstitutionId()))
      .filter(service -> manage.getLinkedIdentityProviders(service.getSpEntityId())
        .stream()
        .map(IdentityProvider::getId)
        .collect(toList())
        .contains("https://www.onegini.me")
      )
      .collect(toList());
  }


  @RequestMapping("/me/switch-to-idp")
  public ResponseEntity<Void> currentIdp(
    @RequestParam(value = "idpId", required = false) String switchToIdp,
    @RequestParam(value = "role", required = false) String role,
    HttpServletResponse response) {

    if (isNullOrEmpty(switchToIdp)) {
      SpringSecurity.clearSwitchedIdp();
    } else {
      IdentityProvider identityProvider = manage.getIdentityProvider(switchToIdp)
          .orElseThrow(() -> new SecurityException(switchToIdp + " does not exist"));

      SpringSecurity.setSwitchedToIdp(identityProvider, role);
    }

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @RequestMapping(value = "/me/settings", method = RequestMethod.POST)
  public ResponseEntity<RestResponse<Object>> updateSettings(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                                      Locale locale,
                                                      @RequestBody Settings settings) {
    CoinUser currentUser = SpringSecurity.getCurrentUser();
    if (currentUser.isSuperUser() || (!currentUser.isDashboardAdmin() && currentUser.isDashboardViewer())) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    IdentityProvider idp = currentUser.getIdp();

    List<Change> changes = new ArrayList<>();

    if (changed(settings.getKeywordsEn(), idp.getKeywords().get("en"))) {
      changes.add(new Change(idp.getId(), "keywords:en", idp.getKeywords().get("en"), settings.getKeywordsEn()));
    }
    if (changed(settings.getKeywordsNl(), idp.getKeywords().get("nl"))) {
      changes.add(new Change(idp.getId(), "keywords:nl", idp.getKeywords().get("nl"), settings.getKeywordsNl()));
    }
    if (settings.isPublishedInEdugain() != idp.isPublishedInEdugain()) {
      changes.add(new Change(idp.getId(), "coin:publish_in_edugain",
        Boolean.toString(idp.isPublishedInEdugain()), Boolean.toString(settings.isPublishedInEdugain())));
    }
    List<ContactPerson> contactPersons = idp.getContactPersons();
    List<ContactPerson> newContactPersons = settings.getContactPersons();

    for (int i = 0; i < contactPersons.size(); i++) {
      ContactPerson contactPerson = contactPersons.get(i);
      if (newContactPersons.size() >= (i + 1)) {
        ContactPerson newContactPerson = newContactPersons.get(i);
        if (changed(contactPerson.getName(), newContactPerson.getName())) {
          changes.add(new Change(idp.getId(), "contacts:" + i + ":name",
            contactPerson.getName(), newContactPerson.getName()));
        }
        if (changed(contactPerson.getEmailAddress(), newContactPerson.getEmailAddress())) {
          changes.add(new Change(idp.getId(), "contacts:" + i + ":emailAddress",
            contactPerson.getEmailAddress(), newContactPerson.getEmailAddress()));
        }
        if (changed(contactPerson.getTelephoneNumber(), newContactPerson.getTelephoneNumber())) {
          changes.add(new Change(idp.getId(), "contacts:" + i + ":telephoneNumber",
            contactPerson.getTelephoneNumber(), newContactPerson.getTelephoneNumber()));
        }
        if (contactPerson.getContactPersonType() != newContactPerson.getContactPersonType()) {
          changes.add(new Change(idp.getId(), "contacts:" + i + ":contactType",
            contactPerson.getContactPersonType().toString(), newContactPerson.getContactPersonType().toString()));
        }
      }
    }
    List<Service> serviceProviders = this.getServiceProvidersForCurrentUser(locale);
    List<Service> services = this.fetchGuestEnabledServiceProviders(locale);

    settings.getServiceProviderSettings().forEach(sp -> {
      Optional<Service> first = serviceProviders.stream().filter(service -> service.getSpEntityId().equals(sp
        .getSpEntityId())).findFirst();
      first.ifPresent(service -> {
        boolean guestEnabled = services.stream().anyMatch(s -> s.getSpEntityId().equals(service.getSpEntityId()));
        if (sp.isHasGuestEnabled() != guestEnabled) {
          changes.add(new Change(sp.getSpEntityId(), "Guest Login Enabled", Boolean.toString(guestEnabled), Boolean
            .toString(sp.isHasGuestEnabled())));
        }
        if (sp.isNoConsentRequired() != service.isNoConsentRequired()) {
          changes.add(new Change(sp.getSpEntityId(), "coin:no_consent_required",
            Boolean.toString(service.isNoConsentRequired()), Boolean.toString(sp.isNoConsentRequired())));
        }
        if (sp.isPublishedInEdugain() != service.isPublishedInEdugain()) {
          changes.add(new Change(sp.getSpEntityId(), "coin:publish_in_edugain",
            Boolean.toString(service.isPublishedInEdugain()), Boolean.toString(sp.isPublishedInEdugain())));
        }
        if (changed(sp.getDescriptionEn(), service.getDescriptions().get("en"))) {
          changes.add(new Change(sp.getSpEntityId(), "description:en", service.getDescriptions().get("en"),
            sp.getDescriptionEn()));
        }
        if (changed(sp.getDescriptionNl(), service.getDescriptions().get("nl"))) {
          changes.add(new Change(sp.getSpEntityId(), "description:nl", service.getDescriptions().get("nl"),
            sp.getDescriptionNl()));
        }
      });
    });
    if (changes.isEmpty()) {
      return ResponseEntity.ok(createRestResponse(Collections.singletonMap("no-changes", true)));
    }

    Action action = Action.builder()
      .userEmail(currentUser.getEmail())
      .userName(currentUser.getUsername())
      .idpId(idpEntityId)
      .settings(settings)
      .type(Action.Type.CHANGE).build();

    actionsService.create(action, changes);

    return ResponseEntity.ok(createRestResponse(action));
  }

  private boolean changed(String oldValue, String newValue) {
    boolean oldValueHasText = StringUtils.hasText(oldValue);
    boolean newValueHasText = StringUtils.hasText(newValue);
    if (!oldValueHasText && !newValueHasText) {
      return false;
    }
    if (oldValueHasText && !newValueHasText) {
      return true;
    }
    if (!oldValueHasText && newValueHasText) {
      return true;
    }
    return !oldValue.equals(newValue);
  }

}
