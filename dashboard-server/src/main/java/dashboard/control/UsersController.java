package dashboard.control;

import dashboard.domain.*;
import dashboard.domain.CoinAuthority.Authority;
import dashboard.mail.MailBox;
import dashboard.manage.*;
import dashboard.service.ActionsService;
import dashboard.service.Services;
import dashboard.util.SpringSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Strings.isNullOrEmpty;
import static dashboard.control.Constants.HTTP_X_IDP_ENTITY_ID;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping(value = "/dashboard/api/users", produces = MediaType.APPLICATION_JSON_VALUE)
@SuppressWarnings("unchecked")
public class UsersController extends BaseController {

    @Autowired
    private Manage manage;

    @Autowired
    private Services services;

    @Autowired
    private ActionsService actionsService;

    @Autowired
    private MailBox mailbox;

    @Value("${manage.manageBaseUrl}")
    private String manageBaseUrl;

    @Value("${dashboard.feature.stepup}")
    private boolean dashboardStepupEnabled;

    private static final Logger LOG = LoggerFactory.getLogger(UsersController.class);

    @RequestMapping("/me")
    public RestResponse<CoinUser> me() {
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        currentUser.setDashboardStepupEnabled(dashboardStepupEnabled);
        return createRestResponse(currentUser);
    }

    @PreAuthorize("hasRole('DASHBOARD_SUPER_USER')")
    @PostMapping("/inviteRequest")
    public ResponseEntity<RestResponse<Object>> inviteRequest(@RequestBody InviteRequest inviteRequest) {
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        String spEntityId = inviteRequest.getSpEntityId();

        String emails = inviteRequest.getContactPersons().stream()
                .map(cp -> cp.getName() + "<" + cp.getEmailAddress() + ">")
                .collect(Collectors.joining(", "));
        String emailTo = inviteRequest.getContactPersons().stream()
                .map(ContactPerson::getEmailAddress)
                .collect(Collectors.joining(", "));

        String body = "Invite request initiated by dashboard super user. Mails sent to: " + emails + ".";
        if (inviteRequest.isContainsMessage()) {
            body = body + " The invitation message from the SURFconext super user:\n" + inviteRequest.getMessage();
        }

        Action action = Action.builder()
                .userEmail(currentUser.getEmail())
                .userName(currentUser.getFriendlyName())
                .body(body)
                .personalMessage(inviteRequest.getMessage())
                .emailTo(emailTo)
                .typeMetaData(inviteRequest.getTypeMetaData())
                .idpId(inviteRequest.getIdpEntityId())
                .spId(spEntityId)
                .type(Action.Type.LINKINVITE).build();

        action = actionsService.create(action);
        mailbox.sendInviteMail(inviteRequest, action);

        return ResponseEntity.ok(createRestResponse(action));
    }

    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_SUPER_USER')")
    @PutMapping("/inviteRequest")
    public ResponseEntity<RestResponse<Object>> updateInviteRequest(@RequestBody UpdateInviteRequest updateInviteRequest,
                                                                    Locale locale) throws IOException {
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        String commentWithUser = String.format("%s / %s has %s this Invite request.", currentUser.getUid(), currentUser.getFriendlyName(),
                updateInviteRequest.getStatus().name().toLowerCase());
        String comment = updateInviteRequest.getComment();
        if (StringUtils.hasText(comment)) {
            commentWithUser = commentWithUser.concat(" User comment: ").concat(comment);
        }
        String jiraKey = updateInviteRequest.getJiraKey();
        if (UpdateInviteRequest.Status.ACCEPTED.equals(updateInviteRequest.getStatus())) {
            try {
                boolean connected = this.automaticallyCreateConnection(locale, updateInviteRequest);
                if (connected) {
                    updateInviteRequest.setConnectWithoutInteraction(true);
                    commentWithUser = commentWithUser.concat("\n" +
                            "The connection in Manage is already made as the SP is configured to automatically connect without interaction");
                } else {
                    IdentityProvider identityProvider = currentUser.getCurrentIdp();
                    EntityType entityType = EntityType.valueOf(updateInviteRequest.getTypeMetaData());
                    final List<ChangeRequest> changeRequests = manage.createConnectionRequests(identityProvider,
                            updateInviteRequest.getSpEntityId(), entityType, updateInviteRequest.getOptionalLoaLevel());
                    commentWithUser = commentWithUser.concat("\n" +
                            "To create the connection in Manage a change request is made:\n");
                    //lambda cann only deal with final variables
                    for (ChangeRequest changeRequest : changeRequests) {
                        String identifier = changeRequest.getMetaDataId();
                        String entityTypeValue = identifier.equals(identityProvider.getInternalId()) ? EntityType.saml20_idp.name() : entityType.name();
                        commentWithUser = commentWithUser.concat(String.format("%s/metadata/%s/%s/requests\n", manageBaseUrl, entityTypeValue, identifier));

                        changeRequest.setAuditData(AuditData.context("Invite request accepted and connected SP " +
                                updateInviteRequest.getSpEntityId(), jiraKey));
                        manage.createChangeRequests(changeRequest);
                    }
                }
                actionsService.approveInviteRequest(jiraKey, commentWithUser, connected);
            } catch (Exception e) {
                LOG.error("Something went wrong in remote API", e);
                actionsService.comment(jiraKey,
                        "The connection could not be made automatically due to an error in remote API: " + e.getMessage() +
                                ". Please contact the dashboard TPM to check the logs.");
                throw e;
            }

        } else {
            actionsService.rejectInviteRequest(jiraKey, commentWithUser);
        }
        return ResponseEntity.ok(createRestResponse(updateInviteRequest));
    }

    private boolean automaticallyCreateConnection(Locale locale, UpdateInviteRequest updateInviteRequest) throws IOException {
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        IdentityProvider idp = currentUser.getSwitchedToIdp().orElse(currentUser.getIdp());
        Optional<Service> serviceOptional = this.automaticallyCreateConnectionAllowed(currentUser, locale, updateInviteRequest);

        if (serviceOptional.isPresent()) {
            Service service = serviceOptional.get();
            Action action = Action.builder()
                    .userEmail(currentUser.getEmail())
                    .userName(currentUser.getFriendlyName())
                    .body(updateInviteRequest.getComment())
                    .idpId(idp.getId())
                    .spId(updateInviteRequest.getSpEntityId())
                    .typeMetaData(updateInviteRequest.getTypeMetaData())
                    .connectWithoutInteraction(true)
                    .shouldSendEmail(service.sendsEmailWithoutInteraction())
                    .service(service)
                    .type(Action.Type.LINKREQUEST).build();

            actionsService.connectWithoutInteraction(action, updateInviteRequest.getOptionalLoaLevel());
            return true;
        }
        return false;
    }

    private Optional<Service> automaticallyCreateConnectionAllowed(CoinUser currentUser, Locale locale, UpdateInviteRequest updateInviteRequest) throws IOException {
        IdentityProvider idp = currentUser.getSwitchedToIdp().orElse(currentUser.getIdp());
        String idpEntityId = idp.getId();
        if (isNullOrEmpty(idp.getInstitutionId())) {
            return Optional.empty();
        }
        String spEntityId = updateInviteRequest.getSpEntityId();

        List<Service> services = this.services.getServicesForIdp(idpEntityId, true, locale);
        Optional<Service> optional = services.stream().filter(s -> s.getSpEntityId().equals(spEntityId)).findFirst();

        if (optional.isPresent()) {
            Service service = optional.get();

            String institutionId = service.getInstitutionId();
            boolean idpAndSpShareInstitution = (institutionId != null) && institutionId.equals(idp.getInstitutionId());
            if (idpAndSpShareInstitution || service.connectsWithoutInteraction()) {
                return optional;
            }
        }
        return Optional.empty();
    }

    @PreAuthorize("hasRole('DASHBOARD_SUPER_USER')")
    @PostMapping("/resendInviteRequest")
    public ResponseEntity<RestResponse<Object>> resendInviteRequest(@RequestBody ResendInviteRequest resendInviteRequest) throws IOException, MessagingException {
        JiraFilter jiraFilter = new JiraFilter();
        jiraFilter.setKey(resendInviteRequest.getJiraKey());
        Action action = actionsService.searchTasks(resendInviteRequest.getIdpId(), jiraFilter).getIssues().stream().findAny()
                .orElseThrow(() -> new IllegalArgumentException(String.format("Jira issue with key %s for IdP %s not found.", resendInviteRequest.getJiraKey(), resendInviteRequest.getIdpId())));
        String emailTo = action.getEmailTo();
        if (!StringUtils.hasText(emailTo)) {
            throw new IllegalArgumentException(String.format("There are no emails set on issue %s", resendInviteRequest.getJiraKey()));
        }
        mailbox.sendInviteMailReminder(action, resendInviteRequest.getComments());
        actionsService.updateOptionalMessage(resendInviteRequest.getJiraKey(), resendInviteRequest.getComments());
        return ResponseEntity.ok(createRestResponse(resendInviteRequest));
    }

    @RequestMapping("/disableConsent")
    public List<Consent> disableConsent() {
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        if (currentUser.isGuest()) {
            return Collections.emptyList();
        }
        IdentityProvider idp = currentUser.getSwitchedToIdp().orElse(currentUser.getIdp());
        String id = idp.getId();
        return manage.getIdentityProvider(id, false)
                .orElseThrow(() -> new IllegalArgumentException(String.format("IdP %s not found", id)))
                .getDisableConsent();
    }


    @PreAuthorize("hasAnyRole('DASHBOARD_SUPER_USER')")
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

        CoinUser currentUser = SpringSecurity.getCurrentUser();
        IdentityProvider idp = currentUser.getSwitchedToIdp().orElse(currentUser.getIdp());
        boolean eraseMails = currentUser.isGuest() || (currentUser.isDashboardMember() && !idp.isDisplayAdminEmailsInDashboard());
        if (eraseMails) {
            usersServices = usersServices.stream().map(service -> ServicesController.eraseMailsFromService(service)).collect(toList());
        }
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

    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_VIEWER','DASHBOARD_SUPER_USER')")
    @RequestMapping("/me/switch-to-idp")
    public ResponseEntity<Void> currentIdp(
            @RequestParam(value = "idpId", required = false) String switchToIdp,
            @RequestParam(value = "role", required = false) String role) {

        if (isNullOrEmpty(switchToIdp)) {
            SpringSecurity.clearSwitchedIdp();
        } else {
            IdentityProvider identityProvider = manage.getIdentityProvider(switchToIdp, false)
                    .orElseThrow(() -> new SecurityException(switchToIdp + " does not exist"));

            SpringSecurity.setSwitchedToIdp(identityProvider, role);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_VIEWER','DASHBOARD_SUPER_USER')")
    @RequestMapping(value = "/me/consent", method = RequestMethod.POST)
    public ResponseEntity<RestResponse<Object>> updateConsentSettings(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                                                      @RequestBody Consent consent) {
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        if (currentUser.isSuperUser() || (!currentUser.isDashboardAdmin() && currentUser.isDashboardViewer())) {
            LOG.warn("Consent endpoint is not allowed for superUser / dashboardViewer, currentUser {}", currentUser);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (currentUser.getCurrentLoaLevel() < 2 && dashboardStepupEnabled) {
            LOG.warn("Consent endpoint requires LOA level 2 or higher, currentUser {}", currentUser);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        IdentityProvider idp = currentUser.getSwitchedToIdp().orElse(currentUser.getIdp());
        List<Consent> currentDisableConsent = idp.getDisableConsent();
        Optional<Consent> previousConsent = currentDisableConsent.stream().filter(c -> c.getSpEntityId().equals(consent.getSpEntityId())).findAny();

        if ((!previousConsent.isPresent()
                && consent.getType().equals(ConsentType.MINIMAL_CONSENT)
                && !StringUtils.hasText(consent.getExplanationEn())
                && !StringUtils.hasText(consent.getExplanationNl())) ||
                previousConsent.map(c -> c.getType().equals(consent.getType()) &&
                        Objects.equals(c.getExplanationEn(), consent.getExplanationEn()) &&
                        Objects.equals(c.getExplanationNl(), consent.getExplanationNl())).orElse(Boolean.FALSE)) {
            return ResponseEntity.ok(createRestResponse(Collections.singletonMap("no-changes", true)));
        }
        Map<String, Object> pathUpdates = Map.of("disableConsent", Map.of(
                "name", consent.getSpEntityId(),
                "type", consent.getType().name().toLowerCase(),
                "explanation:en", consent.getExplanationEn(),
                "explanation:nl", consent.getExplanationNl()
        ));
        Action action = Action.builder()
                .userEmail(currentUser.getEmail())
                .userName(currentUser.getFriendlyName())
                .idpId(idpEntityId)
                .spId(consent.getSpEntityId())
                .typeMetaData(consent.getTypeMetaData())
                .consent(consent)
                .manageUrls(Collections.singletonList(String.format("%s/metadata/%s/%s/requests", manageBaseUrl, EntityType.saml20_idp.name(), idp.getInternalId())))
                .type(Action.Type.CHANGE).build();

        action = actionsService.create(action);

        Map<String, Object> auditData = AuditData.context("Update consent settings for SP " + consent.getSpEntityId(), action.getJiraKey());
        ChangeRequest changeRequest = new ChangeRequest(idp.getInternalId(), EntityType.saml20_idp.name(), pathUpdates,
                auditData, true, PathUpdateType.ADDITION);
        manage.createChangeRequests(changeRequest);

        return ResponseEntity.ok(createRestResponse(action));
    }

    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_SUPER_USER')")
    @RequestMapping(value = "/me/surfsecureid", method = RequestMethod.POST)
    public ResponseEntity<RestResponse<Object>> updateSurfSecureId(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                                                   @RequestBody LoaLevelChange loaLevelChange) throws IOException {
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        if (currentUser.isSuperUser() || !currentUser.isDashboardAdmin()) {
            LOG.warn("SURF secure ID endpoint is not allowed for superUser / dashboardViewer, currentUser {}", currentUser);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (currentUser.getCurrentLoaLevel() < 3 && dashboardStepupEnabled) {
            LOG.warn("SURFsecureID endpoint is not allowed without LOA level 3, currentUser {}", currentUser);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        ServiceProvider serviceProvider = this.manage.getServiceProvider(loaLevelChange.getEntityId(), EntityType.valueOf(loaLevelChange.getEntityType()), false)
                .orElseThrow(IllegalArgumentException::new);
        String minimalLoaLevel = serviceProvider.getMinimalLoaLevel();
        if (StringUtils.hasText(minimalLoaLevel)) {
            LOG.warn("SURF secure ID endpoint is not allowed for SP that already has a loa-level {}", serviceProvider);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        IdentityProvider idp = currentUser.getSwitchedToIdp().orElse(currentUser.getIdp());
        //Need to make a copy otherwise the state of the currentUser is changed
        List<Map<String, String>> currentStepupEntities = idp.getStepupEntities().stream().map(HashMap::new).collect(toList());
        Optional<Map<String, String>> previousLoa = currentStepupEntities.stream()
                .filter(entity -> entity.get("name").equals(loaLevelChange.getEntityId()))
                .findFirst();

        if ((!previousLoa.isPresent() && !StringUtils.hasText(loaLevelChange.getLoaLevel())) ||
                previousLoa.map(loa -> loa.get("level").equals(loaLevelChange.getLoaLevel())).orElse(Boolean.FALSE)) {
            return ResponseEntity.ok(createRestResponse(Collections.singletonMap("no-changes", true)));
        }

        Map<String, Object> pathUpdates = Map.of("stepupEntities",
                Map.of("name", loaLevelChange.getEntityId(),
                        "level", loaLevelChange.getLoaLevel()));
        boolean removal = previousLoa.isPresent() && !StringUtils.hasText(loaLevelChange.getLoaLevel());
        PathUpdateType pathUpdateType = removal ?
                PathUpdateType.REMOVAL : PathUpdateType.ADDITION;

        Action action = Action.builder()
                .userEmail(currentUser.getEmail())
                .userName(currentUser.getFriendlyName())
                .idpId(idpEntityId)
                .spId(loaLevelChange.getEntityId())
                .loaLevel(loaLevelChange.getLoaLevel())
                .manageUrls(Collections.singletonList(String.format("%s/metadata/%s/%s/requests", manageBaseUrl, EntityType.saml20_idp.name(), idp.getInternalId())))
                .type(Action.Type.CHANGE).build();

        action = actionsService.create(action);

        String ctx = removal ? "Removed" : "Added";
        Map<String, Object> auditData = AuditData.context(ctx + " SURFsecureID settings for SP " + loaLevelChange.getEntityId(), action.getJiraKey());
        ChangeRequest changeRequest = new ChangeRequest(idp.getInternalId(), EntityType.saml20_idp.name(),
                pathUpdates, auditData, true, pathUpdateType);
        manage.createChangeRequests(changeRequest);

        return ResponseEntity.ok(createRestResponse(action));

    }

    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_SUPER_USER')")
    @RequestMapping(value = "/me/mfa", method = RequestMethod.POST)
    public ResponseEntity<RestResponse<Object>> updateMFA(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                                          @RequestBody MFAChange mfaChange) throws IOException {
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        if (currentUser.isSuperUser() || !currentUser.isDashboardAdmin()) {
            LOG.warn("SURF secure ID endpoint is not allowed for superUser / dashboardViewer, currentUser {}", currentUser);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (currentUser.getCurrentLoaLevel() < 3 && dashboardStepupEnabled) {
            LOG.warn("MFA endpoint is not allowed without LOA level 3, currentUser {}", currentUser);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        IdentityProvider idp = currentUser.getSwitchedToIdp().orElse(currentUser.getIdp());
        List<Map<String, String>> mfaEntities = idp.getMfaEntities().stream().map(HashMap::new).collect(toList());
        Optional<Map<String, String>> previousMfa = mfaEntities.stream()
                .filter(entity -> entity.get("name").equals(mfaChange.getEntityId()))
                .findFirst();

        if ((!previousMfa.isPresent() && !StringUtils.hasText(mfaChange.getAuthnContextLevel())) ||
                previousMfa.map(mfa -> mfa.get("level").equals(mfaChange.getAuthnContextLevel())).orElse(Boolean.FALSE)) {
            return ResponseEntity.ok(createRestResponse(Collections.singletonMap("no-changes", true)));
        }

        Map<String, Object> pathUpdates = Map.of("mfaEntities", Map.of(
                "name", mfaChange.getEntityId(),
                "level", mfaChange.getAuthnContextLevel()
        ));
        Action action = Action.builder()
                .userEmail(currentUser.getEmail())
                .userName(currentUser.getFriendlyName())
                .idpId(idpEntityId)
                .spId(mfaChange.getEntityId())
                .mfaLevel(mfaChange.getAuthnContextLevel())
                .manageUrls(Collections.singletonList(String.format("%s/metadata/%s/%s/requests", manageBaseUrl, EntityType.saml20_idp.name(), idp.getInternalId())))
                .type(Action.Type.CHANGE).build();

        action = actionsService.create(action);
        String ctx = previousMfa.isPresent() ? "Changed" : "Added";
        Map<String, Object> auditData = AuditData.context(ctx + " MFA settings for SP " + mfaChange.getEntityId(), action.getJiraKey());
        ChangeRequest changeRequest = new ChangeRequest(idp.getInternalId(), EntityType.saml20_idp.name(),
                pathUpdates, auditData, true, PathUpdateType.ADDITION);
        manage.createChangeRequests(changeRequest);

        return ResponseEntity.ok(createRestResponse(action));

    }

    @PreAuthorize("hasAnyRole('DASHBOARD_ADMIN','DASHBOARD_VIEWER','DASHBOARD_SUPER_USER')")
    @RequestMapping(value = "/me/settings", method = RequestMethod.POST)
    public ResponseEntity<RestResponse<Object>> updateSettings(@RequestHeader(HTTP_X_IDP_ENTITY_ID) String idpEntityId,
                                                               @RequestBody Settings settings) throws IOException {
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        if (currentUser.isSuperUser() || (!currentUser.isDashboardAdmin() && currentUser.isDashboardViewer())) {
            LOG.warn("Settings endpoint is not allowed for superUser / dashboardViewer, currentUser {}", currentUser);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        if (currentUser.getCurrentLoaLevel() < 2 && dashboardStepupEnabled) {
            LOG.warn("Settings endpoint is not allowed without LOA level 2, currentUser {}", currentUser);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        IdentityProvider idp = currentUser.getSwitchedToIdp().orElse(currentUser.getIdp());

        Map<String, Object> pathUpdates = getPathUpdates(settings, idp);
        if (pathUpdates.isEmpty()) {
            return ResponseEntity.ok(createRestResponse(Collections.singletonMap("no-changes", true)));
        }
        Action action = Action.builder()
                .userEmail(currentUser.getEmail())
                .userName(currentUser.getFriendlyName())
                .idpId(idpEntityId)
                .settings(settings)
                .typeMetaData(settings.getTypeMetaData())
                .manageUrls(Collections.singletonList(String.format("%s/metadata/%s/%s/requests", manageBaseUrl, EntityType.saml20_idp.name(), idp.getInternalId())))
                .type(Action.Type.CHANGE).build();

        action = actionsService.create(action);

        Map<String, Object> auditData = AuditData.context("Update metadata for IdP " + idpEntityId, action.getJiraKey());
        ChangeRequest changeRequest = new ChangeRequest(idp.getInternalId(), EntityType.saml20_idp.name(),
                pathUpdates, auditData, false, null);
        manage.createChangeRequests(changeRequest);

        return ResponseEntity.ok(createRestResponse(action));
    }

    protected Map<String, Object> getPathUpdates(Settings settings, IdentityProvider idp) throws IOException {
        Map<String, Object> pathUpdates = new HashMap<>();

        this.diff(pathUpdates, idp.getKeywords().get("en"), settings.getKeywordsEn(), "keywords:en");
        this.diff(pathUpdates, idp.getKeywords().get("nl"), settings.getKeywordsNl(), "keywords:nl");
        this.diff(pathUpdates, idp.getKeywords().get("pt"), settings.getKeywordsPt(), "keywords:pt");

        this.diff(pathUpdates, idp.getHomeUrls().get("en"), settings.getOrganisationUrlEn(), "OrganizationURL:en");
        this.diff(pathUpdates, idp.getHomeUrls().get("nl"), settings.getOrganisationUrlNl(), "OrganizationURL:nl");
        this.diff(pathUpdates, idp.getHomeUrls().get("pt"), settings.getOrganisationUrlPt(), "OrganizationURL:pt");

        this.diff(pathUpdates, idp.getOrganisationDisplayNames().get("en"), settings.getOrganisationDisplayNameEn(), "OrganizationDisplayName:en");
        this.diff(pathUpdates, idp.getOrganisationDisplayNames().get("nl"), settings.getOrganisationDisplayNameNl(), "OrganizationDisplayName:nl");
        this.diff(pathUpdates, idp.getOrganisationDisplayNames().get("pt"), settings.getOrganisationDisplayNamePt(), "OrganizationDisplayName:pt");

        this.diff(pathUpdates, idp.getOrganisationNames().get("en"), settings.getOrganisationNameEn(), "OrganizationName:en");
        this.diff(pathUpdates, idp.getOrganisationNames().get("nl"), settings.getOrganisationNameNl(), "OrganizationName:nl");
        this.diff(pathUpdates, idp.getOrganisationNames().get("pt"), settings.getOrganisationNamePt(), "OrganizationName:pt");

        this.diff(pathUpdates, idp.getDescriptions().get("en"), settings.getDescriptionsEn(), "description:en");
        this.diff(pathUpdates, idp.getDescriptions().get("nl"), settings.getDescriptionsNl(), "description:nl");
        this.diff(pathUpdates, idp.getDescriptions().get("pt"), settings.getDescriptionsPt(), "description:pt");

        this.diff(pathUpdates, idp.getDisplayNames().get("en"), settings.getDisplayNamesEn(), "displayName:en");
        this.diff(pathUpdates, idp.getDisplayNames().get("nl"), settings.getDisplayNamesNl(), "displayName:nl");
        this.diff(pathUpdates, idp.getDisplayNames().get("pt"), settings.getDisplayNamesPt(), "displayName:pt");

        this.diff(pathUpdates, idp.isPublishedInEdugain(), settings.isPublishedInEdugain(),
                "coin:publish_in_edugain");

        if (changed(idp.isConnectToRSServicesAutomatically(), settings.isConnectToRSServicesAutomatically())) {
            pathUpdates.put("metaDataFields.coin:entity_categories:1", "http://refeds.org/category/research-and-scholarship");
        }

        this.diff(pathUpdates, idp.isAllowMaintainersToManageAuthzRules(), settings.isAllowMaintainersToManageAuthzRules(),
                "coin:allow_maintainers_to_manage_authz_rules");

        this.diff(pathUpdates, idp.isDisplayAdminEmailsInDashboard(), settings.isDisplayAdminEmailsInDashboard(),
                "coin:display_admin_emails_in_dashboard");

        this.diff(pathUpdates, idp.isDisplayStatsInDashboard(), settings.isDisplayStatsInDashboard(),
                "coin:display_stats_in_dashboard");

        this.diff(pathUpdates, idp.getState(), settings.getStateType() != null ? settings.getStateType().name() : null, "state");

        diffContactPersons(pathUpdates, idp.getContactPersons(), settings.getContactPersons());

        return pathUpdates;
    }

    private String givenName(List<String> names) {
        return names.get(0);
    }

    private String surName(List<String> names) {
        return names.size() > 1 ? String.join(" ", names.subList(1, names.size())) : "";
    }

    private void diffContactPersons(Map<String, Object> pathUpdates, List<ContactPerson> contactPersons,
                                    List<ContactPerson> newContactPersons) {
        if (CollectionUtils.isEmpty(contactPersons) && CollectionUtils.isEmpty(newContactPersons)) {
            return;
        }
        for (int i = 0; i < contactPersons.size(); i++) {
            ContactPerson contactPerson = contactPersons.get(i);
            if (newContactPersons != null && newContactPersons.size() >= (i + 1)) {
                ContactPerson newContactPerson = newContactPersons.get(i);
                List<String> names = Arrays.asList(contactPerson.getName().split(" "));
                String givenName = givenName(names);
                String surName = surName(names);
                List<String> newNames = Arrays.asList(newContactPerson.getName().split(" "));
                String newGivenName = givenName(newNames);
                String newSurName = surName(newNames);

                diff(pathUpdates, givenName, newGivenName, "contacts:" + i + ":givenName");
                diff(pathUpdates, surName, newSurName, "contacts:" + i + ":surName");
                diff(pathUpdates, contactPerson.getEmailAddress(), newContactPerson.getEmailAddress(),
                        "contacts:" + i + ":emailAddress");
                diff(pathUpdates, contactPerson.getTelephoneNumber(), newContactPerson.getTelephoneNumber(),
                        "contacts:" + i + ":telephoneNumber");
                diff(pathUpdates, contactPerson.getContactPersonType(), newContactPerson.getContactPersonType(),
                        "contacts:" + i + ":contactType");
            }
        }
    }

    private void diff(Map<String, Object> pathUpdates, Object oldValue, Object newValue, String attributeName) {
        if (changed(oldValue, newValue)) {
            if (newValue instanceof String) {
                newValue = StringUtils.hasText((String) newValue) ? newValue : null;
            }
            pathUpdates.put(String.format("metaDataFields.%s", attributeName), newValue);
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
