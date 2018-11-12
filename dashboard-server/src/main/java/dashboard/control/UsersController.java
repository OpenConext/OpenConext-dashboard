package dashboard.control;

import dashboard.domain.*;
import dashboard.mail.MailBox;
import dashboard.manage.EntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import dashboard.domain.CoinAuthority.Authority;
import dashboard.manage.Manage;
import dashboard.service.ActionsService;
import dashboard.service.Services;
import dashboard.util.SpringSecurity;

import javax.mail.MessagingException;
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
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toList;
import static dashboard.control.Constants.HTTP_X_IDP_ENTITY_ID;

@RestController
@RequestMapping(value = "/dashboard/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UsersController extends BaseController {

    @Autowired
    private Manage manage;

    @Autowired
    private Services services;

    @Autowired
    private ActionsService actionsService;

    @Autowired
    private MailBox mailbox;

    private static final Logger LOG = LoggerFactory.getLogger(UsersController.class);

    @RequestMapping("/me")
    public RestResponse<CoinUser> me() {
        return createRestResponse(SpringSecurity.getCurrentUser());
    }

    @PostMapping("/inviteRequest")
    public ResponseEntity<RestResponse<Object>> inviteRequest(@RequestBody InviteRequest inviteRequest) throws IOException, MessagingException {
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        String spEntityId = inviteRequest.getSpEntityId();

        String emails = inviteRequest.getContactPersons().stream()
                .map(cp -> cp.getName() + "<" + cp.getEmailAddress() + ">")
                .collect(Collectors.joining(", "));

        Action action = Action.builder()
                .userEmail(currentUser.getEmail())
                .userName(currentUser.getFriendlyName())
                .body("Invite request initiated by dashboard super user. Mails sent to: " + emails)
                .idpId(inviteRequest.getIdpEntityId())
                .spId(spEntityId)
                .type(Action.Type.LINKINVITE).build();

        action = actionsService.create(action, Collections.emptyList());
        mailbox.sendInviteMail(inviteRequest, action);

        return ResponseEntity.ok(createRestResponse(action));
    }

    @PutMapping("/inviteRequest")
    public ResponseEntity<RestResponse<Object>> updateInviteRequest(@RequestBody UpdateInviteRequest updateInviteRequest) throws IOException, MessagingException {
        String commentWithUser = SpringSecurity.getCurrentUser().getUsername()
                .concat(" has " + updateInviteRequest.getStatus().name().toLowerCase() + " the InviteRequest. ");

        String comment = updateInviteRequest.getComment();
        if (StringUtils.hasText(comment)) {
            commentWithUser.concat(comment);
        }
        if (UpdateInviteRequest.Status.ACCEPTED.equals(updateInviteRequest.getStatus())) {
            actionsService.approveInviteRequest(updateInviteRequest.getJiraKey(), commentWithUser);
        } else {
            actionsService.rejectInviteRequest(updateInviteRequest.getJiraKey(), commentWithUser);
        }
        return ResponseEntity.ok(createRestResponse(updateInviteRequest));
    }

    @RequestMapping("/disableConsent")
    public List<Consent> disableConsent() {
        String id = SpringSecurity.getCurrentUser().getIdp().getId();
        return manage.getIdentityProvider(id, false)
                .orElseThrow(() -> new IllegalArgumentException(String.format("IdP %s not found", id)))
                .getDisableConsent();
    }


    @RequestMapping("/super/idps")
    public ResponseEntity<RestResponse<Map<String, List<?>>>> idps() {
        CoinUser currentUser = SpringSecurity.getCurrentUser();

        if (!currentUser.isSuperUser()) {
            LOG.warn("IdP's endpoint is only allowed for superUser, not for {}", currentUser);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<IdentityProvider> idps = manage.getAllIdentityProviders().stream()
                .sorted(Comparator.comparing(Provider::getName))
                .collect(toList());

        List<String> roles = Arrays.asList(Authority.ROLE_DASHBOARD_VIEWER.name(),
                Authority.ROLE_DASHBOARD_ADMIN.name());

        HashMap<String, List<?>> payload = new HashMap<>();
        payload.put("idps", idps);
        payload.put("roles", roles);

        return new ResponseEntity<>(createRestResponse(payload), HttpStatus.OK);
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
        String usersInstitutionId = switchedToIdp.isPresent() ? switchedToIdp.get().getInstitutionId() :
                currentUser.getInstitutionId();

        return isNullOrEmpty(usersInstitutionId) ? Collections.emptyList()
                : services.getInstitutionalServicesForIdp(usersInstitutionId, locale);
    }

    @RequestMapping("/me/switch-to-idp")
    public ResponseEntity<Void> currentIdp(
            @RequestParam(value = "idpId", required = false) String switchToIdp,
            @RequestParam(value = "role", required = false) String role,
            HttpServletResponse response) {

        if (isNullOrEmpty(switchToIdp)) {
            SpringSecurity.clearSwitchedIdp();
        } else {
            IdentityProvider identityProvider = manage.getIdentityProvider(switchToIdp, false)
                    .orElseThrow(() -> new SecurityException(switchToIdp + " does not exist"));

            SpringSecurity.setSwitchedToIdp(identityProvider, role);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/me/consent", method = RequestMethod.POST)
    public ResponseEntity<RestResponse<Object>> updateConsentSettings(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                                                      @RequestBody Consent consent) throws IOException {
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        if (currentUser.isSuperUser() || (!currentUser.isDashboardAdmin() && currentUser.isDashboardViewer())) {
            LOG.warn("Consent endpoint is not allowed for superUser / dashboardViewer, currentUser {}", currentUser);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        IdentityProvider idp = currentUser.getIdp();
        Optional<Consent> previousConsent = idp.getDisableConsent().stream().filter(c -> c.getSpEntityId().equals(consent.getSpEntityId())).findAny();
        String idIdp = idp.getId();

        List<Change> changes = getChanges(idIdp, previousConsent, consent);
        if (changes.isEmpty()) {
            return ResponseEntity.ok(createRestResponse(Collections.singletonMap("no-changes", true)));
        }
        Action action = Action.builder()
                .userEmail(currentUser.getEmail())
                .userName(currentUser.getFriendlyName())
                .idpId(idpEntityId)
                .spId(consent.getSpEntityId())
                .consent(consent)
                .type(Action.Type.CHANGE).build();

        actionsService.create(action, changes);

        return ResponseEntity.ok(createRestResponse(action));

    }

    @RequestMapping(value = "/me/settings", method = RequestMethod.POST)
    public ResponseEntity<RestResponse<Object>> updateSettings(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                                               Locale locale,
                                                               @RequestBody Settings settings) throws IOException {
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        if (currentUser.isSuperUser() || (!currentUser.isDashboardAdmin() && currentUser.isDashboardViewer())) {
            LOG.warn("Settings endpoint is not allowed for superUser / dashboardViewer, currentUser {}", currentUser);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        IdentityProvider idp = currentUser.getIdp();

        List<Change> changes = getChanges(locale, settings, idp);
        if (changes.isEmpty()) {
            return ResponseEntity.ok(createRestResponse(Collections.singletonMap("no-changes", true)));
        }

        Action action = Action.builder()
                .userEmail(currentUser.getEmail())
                .userName(currentUser.getFriendlyName())
                .idpId(idpEntityId)
                .settings(settings)
                .type(Action.Type.CHANGE).build();

        actionsService.create(action, changes);

        return ResponseEntity.ok(createRestResponse(action));
    }

    protected List<Change> getChanges(String idpId, Optional<Consent> previousConsentOptional, Consent consent) throws IOException {
        List<Change> changes = new ArrayList<>();

        if (!previousConsentOptional.isPresent()
                && consent.getType().equals(ConsentType.DEFAULT_CONSENT)
                && StringUtils.isEmpty(consent.getExplanationEn())
                && StringUtils.isEmpty(consent.getExplanationEn())) {
            return changes;
        }
        Consent previousConsent = previousConsentOptional.orElse(new Consent());

        this.diff(changes, idpId, previousConsent.getSpEntityId(), consent.getSpEntityId(), "consent:sp:name");
        this.diff(changes, idpId, previousConsent.getExplanationEn(), consent.getExplanationEn(), "consent:explanation:en");
        this.diff(changes, idpId, previousConsent.getExplanationNl(), consent.getExplanationNl(), "consent:explanation:nl");
        this.diff(changes, idpId, previousConsent.getType(), consent.getType(), "consent:type");

        return changes;
    }

    protected List<Change> getChanges(Locale locale, Settings settings, IdentityProvider idp) throws IOException {
        List<Change> changes = new ArrayList<>();

        String idpId = idp.getId();

        this.diff(changes, idpId, idp.getKeywords().get("en"), settings.getKeywordsEn(), "keywords:en");
        this.diff(changes, idpId, idp.getKeywords().get("nl"), settings.getKeywordsNl(), "keywords:nl");

        this.diff(changes, idpId, idp.getDescriptions().get("en"), settings.getDescriptionsEn(), "description:en");
        this.diff(changes, idpId, idp.getDescriptions().get("nl"), settings.getDescriptionsNl(), "description:nl");

        this.diff(changes, idpId, idp.getDisplayNames().get("en"), settings.getDisplayNamesEn(), "displayName:en");
        this.diff(changes, idpId, idp.getDisplayNames().get("nl"), settings.getDisplayNamesNl(), "displayName:nl");

        this.diff(changes, idpId, idp.isPublishedInEdugain(), settings.isPublishedInEdugain(),
                "coin:publish_in_edugain");

        this.diff(changes, idpId, idp.isConnectToRSServicesAutomatically(), settings.isConnectToRSServicesAutomatically(),
                "coin:entity_categories:1 - http://refeds.org/category/research-and-scholarship");

        this.diff(changes, idpId, idp.getLogoUrl(), settings.getLogoUrl(), "logo:0:url");

        this.diff(changes, idpId, idp.getState(), settings.getStateType() != null ? settings.getStateType().name() : null, "state");

        diffContactPersons(changes, idpId, idp.getContactPersons(), settings.getContactPersons());

        List<Service> serviceProviders = this.getServiceProvidersForCurrentUser(locale);

        settings.getServiceProviderSettings().forEach(sp -> {
            Optional<Service> first = serviceProviders.stream()
                    .filter(service -> service.getSpEntityId().equals(sp.getSpEntityId()))
                    .findFirst();
            first.ifPresent(service -> {
                String id = service.getSpEntityId();

                diff(changes, id, service.getDescriptions().get("en"), sp.getDescriptionEn(), "description:en");
                diff(changes, id, service.getDescriptions().get("nl"), sp.getDescriptionNl(), "description:nl");

                diff(changes, id, service.getDisplayNames().get("en"), sp.getDisplayNameEn(), "displayName:en");
                diff(changes, id, service.getDisplayNames().get("nl"), sp.getDisplayNameNl(), "displayName:nl");

                diff(changes, id, service.isPublishedInEdugain(), sp.isPublishedInEdugain(), "coin:publish_in_edugain");
                diff(changes, id, service.isGuestEnabled(), sp.isHasGuestEnabled(), "Guest Login Enabled");
                diff(changes, id, service.isNoConsentRequired(), sp.isNoConsentRequired(), "coin:no_consent_required");

                diff(changes, idpId, service.getState(), sp.getStateType() != null ? sp.getStateType().name() : null, "state");

                diffContactPersons(changes, id, service.getContactPersons(), sp.getContactPersons());
            });
        });
        return changes;
    }

    private void diffContactPersons(List<Change> changes, String id, List<ContactPerson> contactPersons,
                                    List<ContactPerson> newContactPersons) {
        if (CollectionUtils.isEmpty(contactPersons) && CollectionUtils.isEmpty(newContactPersons)) {
            return;
        }
        for (int i = 0; i < contactPersons.size(); i++) {
            ContactPerson contactPerson = contactPersons.get(i);
            if (newContactPersons != null && newContactPersons.size() >= (i + 1)) {
                ContactPerson newContactPerson = newContactPersons.get(i);
                diff(changes, id, contactPerson.getName(), newContactPerson.getName(), "contacts:" + i + ":name");
                diff(changes, id, contactPerson.getEmailAddress(), newContactPerson.getEmailAddress(),
                        "contacts:" + i + ":emailAddress");
                diff(changes, id, contactPerson.getTelephoneNumber(), newContactPerson.getTelephoneNumber(),
                        "contacts:" + i + ":telephoneNumber");
                diff(changes, id, contactPerson.getContactPersonType(), newContactPerson.getContactPersonType(),
                        "contacts:" + i + ":contactType");
            }
        }
    }

    private void diff(List<Change> changes, String id, Object oldValue, Object newValue, String attributeName) {
        if (changed(oldValue, newValue)) {
            changes.add(new Change(id, attributeName, String.format("%s", oldValue), String.format("%s", newValue)));
        }
    }

    private boolean changed(Object oldValue, Object newValue) {
        boolean oldNotNull = oldValue instanceof String ? StringUtils.hasText((String) oldValue) : oldValue != null;
        boolean newNotNull = newValue instanceof String ? StringUtils.hasText((String) newValue) : newValue != null;
        if (!oldNotNull && !newNotNull) {
            return false;
        }
        if (oldNotNull && !newNotNull) {
            return true;
        }
        if (!oldNotNull && newNotNull) {
            return true;
        }
        return !oldValue.equals(newValue);
    }

}
