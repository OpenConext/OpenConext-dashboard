package dashboard.shibboleth;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import dashboard.domain.*;
import dashboard.manage.Manage;
import dashboard.sab.Sab;
import dashboard.service.impl.JiraClient;
import lombok.Setter;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;
import static dashboard.control.LoginController.IDP_ID_COOKIE_NAME;
import static dashboard.domain.CoinAuthority.Authority.*;
import static dashboard.shibboleth.ShibbolethHeader.*;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.*;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;

public class ShibbolethPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    public static final Map<String, ShibbolethHeader> shibHeaders;
    private static final Splitter shibHeaderValueSplitter = Splitter.on(';').omitEmptyStrings();
    private final static Logger LOG = LoggerFactory.getLogger(ShibbolethPreAuthenticatedProcessingFilter.class);

    static {
        shibHeaders = ImmutableMap.<String, ShibbolethHeader>builder()
                .put("urn:mace:dir:attribute-def:uid", Shib_Uid)
                .put("urn:mace:dir:attribute-def:mail", Shib_Email)
                .put("urn:mace:terena.org:attribute-def:schacHomeOrganization", Shib_HomeOrg)
                .put("urn:mace:dir:attribute-def:isMemberOf", Shib_MemberOf)
                .put("urn:mace:dir:attribute-def:eduPersonEntitlement", Shib_EduPersonEntitlement)
                .put("urn:mace:dir:attribute-def:displayName", Shib_DisplayName)
                .put("urn:mace:dir:attribute-def:sn", Shib_SurName)
                .put("urn:mace:dir:attribute-def:givenName", Shib_GivenName)
                .put("urn:mace:dir:attribute-def:cn", Shib_CommonName)
                .put("urn:mace:dir:attribute-def:ou", Shib_OrgUnit)
                .put("urn:mace:dir:attribute-def:eduPersonAffiliation", Shib_EduPersonAffiliation)
                .put("urn:mace:dir:attribute-def:eduPersonScopedAffiliation", Shib_EduPersonScopedAffiliation)
                .put("urn:mace:dir:attribute-def:eduPersonTargetedID", Shib_EduPersonTargetedID)
                .put("urn:mace:dir:attribute-def:eduPersonPrincipalName", Shib_EduPersonPN)
                .put("urn:mace:dir:attribute-def:eduPersonOrcid", Shib_EduPersonOrcid)
                .put("urn:mace:dir:attribute-def:preferredLanguage", Shib_PreferredLanguage)
                .put("urn:mace:terena.org:attribute-def:schacHomeOrganizationType", Shib_SchacHomeOrganizationType)
                .put("urn:schac:attribute-def:schacPersonalUniqueCode", Shib_SchacPersonalUniqueCode)
                .put("urn:mace:surffederatie_nl:attribute-def:nlEduPersonOrgUnit", Shib_NlEduPersonOrgUnit)
                .put("urn:mace:surffederatie.nl:attribute-def:nlEduPersonStudyBranch", Shib_NlEduPersonStudyBranch)
                .put("urn:mace:surffederatie.nl:attribute-def:nlStudielinkNummer", Shib_NlStudielinkNummer)
                .put("urn:mace:surf.nl:attribute-def:eckid", Shib_SURFEckid)
                .put("urn:mace:surf.nl:attribute-def:surf-autorisaties", Shib_SURFautorisaties)
                .build();
    }


    private String organization;

    private Manage manage;
    private Sab sab;
    private final JiraClient jiraClient;
    @Setter
    private String dashboardAdmin;
    @Setter
    private String dashboardViewer;
    @Setter
    private List<String> dashboardSuperUser;
    @Setter
    private String adminSurfConextIdpRole;
    @Setter
    private String viewerSurfConextIdpRole;
    private boolean isManageConsentEnabled;
    private boolean jiraDown;
    private boolean isOidcEnabled;
    private String hideTabs;
    private String supportedLanguages;
    private String defaultLoa;
    private List<String> loaLevels;
    private List<String> authnContextLevels;
    private boolean dashboardStepupEnabled;

    ShibbolethPreAuthenticatedProcessingFilter(JiraClient jiraClient) {
        this.jiraClient = jiraClient;
    }

    public ShibbolethPreAuthenticatedProcessingFilter(AuthenticationManager authenticationManager,
                                                      Manage manage,
                                                      Sab sab,
                                                      JiraClient jiraClient,
                                                      String dashboardAdmin,
                                                      String dashboardViewer,
                                                      String dashboardSuperUser,
                                                      String adminSufConextIdpRole,
                                                      String viewerSurfConextIdpRole,
                                                      boolean isManageConsentEnabled,
                                                      boolean isOidcEnabled,
                                                      boolean dashboardStepupEnabled,
                                                      boolean jiraDown,
                                                      String hideTabs,
                                                      String supportedLanguages,
                                                      String organization,
                                                      String defaultLoa,
                                                      List<String> loaLevels,
                                                      List<String> authnContextLevels) {
        setAuthenticationManager(authenticationManager);
        this.manage = manage;
        this.sab = sab;
        this.jiraClient = jiraClient;
        this.dashboardAdmin = dashboardAdmin;
        this.dashboardSuperUser = Stream.of(dashboardSuperUser.split(",")).map(String::trim).collect(toList());
        this.dashboardViewer = dashboardViewer;
        this.adminSurfConextIdpRole = adminSufConextIdpRole;
        this.viewerSurfConextIdpRole = viewerSurfConextIdpRole;
        this.isManageConsentEnabled = isManageConsentEnabled;
        this.dashboardStepupEnabled = dashboardStepupEnabled;
        this.isOidcEnabled = isOidcEnabled;
        this.jiraDown = jiraDown;
        this.hideTabs = hideTabs;
        this.supportedLanguages = supportedLanguages;
        this.organization = organization;
        this.defaultLoa = defaultLoa;
        this.loaLevels = loaLevels;
        this.authnContextLevels = authnContextLevels;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null && !request.getRequestURI().endsWith("health") && !request.getRequestURI().endsWith("ico")) {
            ArrayList<String> list = Collections.list(headerNames);
            LOG.info("Received headers {}", list.stream().collect(toMap(
                    name -> name,
                    name -> {
                        Enumeration<String> headers = request.getHeaders(name);
                        return headers != null ? Collections.list(headers) : Collections.emptyList();
                    })));
        }
        Optional<String> uidOptional = getFirstShibHeaderValue(Name_Id, request);
        Optional<String> authorityOptional = getFirstShibHeaderValue(Shib_Authenticating_Authority, request);
        if (!uidOptional.isPresent() && !authorityOptional.isPresent()) {
            CoinUser guestUser = new GuestUser();
            setUserConfigurationData(guestUser);
            return guestUser;
        }
        String uid = uidOptional.orElseThrow(() -> new IllegalArgumentException(String.format("Missing %s Shibboleth header (%s)",
                Name_Id.getValue(), request.getRequestURL())));

        String idpId = authorityOptional.orElseThrow(() -> new IllegalArgumentException(String.format("Missing %s Shibboleth header (%s)",
                Shib_Authenticating_Authority.getValue(), request.getRequestURL())));

        CoinUser coinUser = new CoinUser();
        coinUser.setUid(uid);
        coinUser.setDisplayName(getFirstShibHeaderValue(Shib_DisplayName, request).orElse(null));
        coinUser.setGivenName(getFirstShibHeaderValue(Shib_GivenName, request).orElse(null));
        coinUser.setSurName(getFirstShibHeaderValue(Shib_SurName, request).orElse(null));
        coinUser.setEmail(getFirstShibHeaderValue(Shib_Email, request).orElse(null));
        coinUser.setSchacHomeOrganization(getFirstShibHeaderValue(Shib_HomeOrg, request).orElse(null));
        coinUser.setCurrentLoaLevel(getFirstShibHeaderValue(Shib_AuthnContext_Class, request).map(shibAuthnContextClass -> this.convertLoaLevel(shibAuthnContextClass)).orElse(1));
        setUserConfigurationData(coinUser);

        Map<ShibbolethHeader, List<String>> attributes = shibHeaders.values().stream()
                .filter(h -> StringUtils.hasText(request.getHeader(h.getValue())))
                .collect(toMap(h -> h, h -> getShibHeaderValues(h, request)));
        coinUser.setAttributeMap(attributes);

        List<String> groups = getShibHeaderValues(Shib_MemberOf, request);
        this.addDashboardRoleForMemberships(coinUser, groups);

        List<IdentityProvider> institutionIdentityProviders = getInstitutionIdentityProviders(idpId);

        checkState(!isEmpty(institutionIdentityProviders), "no InstitutionIdentityProviders found for '" + idpId + "'");

        if (institutionIdentityProviders.size() == 1) {
            IdentityProvider idp = institutionIdentityProviders.get(0);
            coinUser.setIdp(idp);
            coinUser.addInstitutionIdp(idp);
        } else {
            coinUser.setIdp(getCurrentIdp(idpId, institutionIdentityProviders));
            coinUser.getInstitutionIdps().addAll(institutionIdentityProviders);
            Collections.sort(coinUser.getInstitutionIdps(), Comparator.comparing(Provider::getName));
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Stream.of(cookies).filter(cookie -> IDP_ID_COOKIE_NAME.equals(cookie.getName()))
                        .findAny()
                        .ifPresent(cookie -> {
                            IdentityProvider switchedToIdp = institutionIdentityProviders.stream()
                                    .filter(idp -> idp.getId().equals(cookie.getValue())).findAny().orElse(null);
                            coinUser.setSwitchedToIdp(switchedToIdp);
                        });
            }
        }
        //We need to get the last part, urn:mace:surfnet.nl:surfnet.nl:sab:role:SURF*
        List<String> sabRoles = this.getShibHeaderValues(Shib_SURFautorisaties, request);
        List<String> surfAutorisaties = sabRoles.stream()
                .filter(s -> s.contains("sab:role"))
                .map(autorisatie -> autorisatie.substring(autorisatie.lastIndexOf(":") + 1))
                .toList();
        Optional<String> optionalOrganisation = sabRoles.stream()
                .filter(s -> s.contains("sab:organizationGUID"))
                .map(autorisatie -> autorisatie.substring(autorisatie.lastIndexOf(":") + 1))
                .findFirst();

        LOG.debug("SAB: received roles {} and organization {}",
                surfAutorisaties,
                optionalOrganisation);


        List<String> institutionIds = institutionIdentityProviders.stream()
                .filter(provider -> StringUtils.hasText(provider.getInstitutionId()))
                .map(provider -> provider.getInstitutionId().toLowerCase()).toList();

        LOG.debug("Manage: received institution ID's {} ", institutionIds);

        this.addDashboardRoleForEntitlements(coinUser, surfAutorisaties, optionalOrganisation,
                institutionIds);

        institutionIdentityProviders.stream()
                .filter(idp -> hasText(idp.getInstitutionId()))
                .findFirst()
                .ifPresent(idp -> coinUser.setInstitutionId(idp.getInstitutionId()));

        if (CollectionUtils.isEmpty(coinUser.getAuthorities())) {
            coinUser.addAuthority(new CoinAuthority(ROLE_DASHBOARD_MEMBER));
        }

        if (coinUser.isDashboardMember()) {
            IdentityProvider idp = coinUser.getIdp();
            idp.getContactPersons().clear();
            coinUser.getInstitutionIdps().forEach(anIdp -> {
                anIdp.getContactPersons().clear();
            });
        }

        String idpEntityId = coinUser.getIdp().getId();
        JiraFilter jiraFilter = new JiraFilter();
        jiraFilter.setTypes(Arrays.asList(Action.Type.LINKINVITE, Action.Type.LINKREQUEST, Action.Type.UNLINKINVITE, Action.Type.UNLINKREQUEST));
        jiraFilter.setStatuses(Arrays.asList("In Progress", "Waiting for customer"));

        JiraResponse jiraResponse = jiraClient.searchTasks(idpEntityId, jiraFilter);
        List<Action> issues = jiraResponse.getIssues();
        Set<String> invitationRequestEntities = issues.stream().map(action -> action.getSpId()).collect(toSet());

        coinUser.setInvitationRequestEntities(invitationRequestEntities);
        return coinUser;
    }

    private void setUserConfigurationData(CoinUser coinUser) {
        coinUser.setManageConsentEnabled(this.isManageConsentEnabled);
        coinUser.setOidcEnabled(this.isOidcEnabled);
        coinUser.setHideTabs(this.hideTabs);
        coinUser.setSupportedLanguages(this.supportedLanguages);
        coinUser.setOrganization(this.organization);
        coinUser.setDefaultLoa(this.defaultLoa);
        coinUser.setLoaLevels(this.loaLevels);
        coinUser.setAuthnContextLevels(this.authnContextLevels);
        coinUser.setDashboardStepupEnabled(this.dashboardStepupEnabled);
        coinUser.setJiraDown(jiraDown);
    }

    private void addDashboardRoleForMemberships(CoinUser user, List<String> groups) {
        if (!Collections.disjoint(groups, dashboardSuperUser)) {
            user.addAuthority(new CoinAuthority(ROLE_DASHBOARD_SUPER_USER));
        } else if (groups.contains(dashboardAdmin)) {
            user.addAuthority(new CoinAuthority(ROLE_DASHBOARD_ADMIN));
        } else if (groups.contains(dashboardViewer)) {
            user.addAuthority(new CoinAuthority(ROLE_DASHBOARD_VIEWER));
        }
    }

    private void addDashboardRoleForEntitlements(CoinUser user,
                                                 List<String> sabRoles,
                                                 Optional<String> optionalOrganisation,
                                                 List<String> institutionIds) {
        String organisation = optionalOrganisation.orElse(null);
        boolean institutionIdMatch = StringUtils.hasText(organisation) &&
                institutionIds.contains(organisation.toLowerCase());
        if (institutionIdMatch) {
            if (sabRoles.stream().anyMatch(role -> role.trim().equalsIgnoreCase(adminSurfConextIdpRole.trim()))) {
                user.addAuthority(new CoinAuthority(ROLE_DASHBOARD_ADMIN));
            } else if (sabRoles.stream().anyMatch(role -> role.trim().equalsIgnoreCase(viewerSurfConextIdpRole.trim()))) {
                user.addAuthority(new CoinAuthority(ROLE_DASHBOARD_VIEWER));
            }
            LOG.info("Added grantedAuthorities {} to user {}", user.getAuthorities(), user.getUid());
        } else {
            LOG.warn("SAB: received autorisaties, but the institutionIds from Manage {} do not match the organization name from SAB {}",
                    institutionIds, organisation);
        }
    }

    private List<IdentityProvider> getInstitutionIdentityProviders(String idpId) {
        Optional<IdentityProvider> optionalIdentityProvider = manage.getIdentityProvider(idpId, false);
        return optionalIdentityProvider.map(idp -> {
            String institutionId = idp.getInstitutionId();
            return hasText(institutionId) ? manage.getInstituteIdentityProviders(institutionId) : singletonList(idp);
        }).orElse(Collections.emptyList());
    }

    private Optional<String> getFirstShibHeaderValue(ShibbolethHeader headerName, HttpServletRequest request) {
        return getShibHeaderValues(headerName, request).stream().findFirst();
    }

    @SneakyThrows
    private List<String> getShibHeaderValues(ShibbolethHeader headerName, HttpServletRequest request) {
        String header = request.getHeader(headerName.getValue());
        String headerValue = StringUtils.hasText(header) ?
                new String(header.getBytes("ISO8859-1"), StandardCharsets.UTF_8) : null;
        return headerValue == null ? Collections.emptyList() : shibHeaderValueSplitter.splitToList(headerValue);
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        return "N/A";
    }

    private IdentityProvider getCurrentIdp(String idpId, List<IdentityProvider> institutionIdentityProviders) {
        return institutionIdentityProviders.stream()
                .filter(provider -> provider.getId().equals(idpId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(String.format("The Idp('%s') is not present in the list " +
                        "of Idp's returned by the CsaClient", idpId)));
    }

    private int convertLoaLevel(String shibAuthnContextClass) {
        if (!StringUtils.hasText(shibAuthnContextClass)) {
            return 1;
        }
        if (shibAuthnContextClass.trim().toLowerCase().endsWith("password")) {
            return 1;
        }
        try {
            int loa = Integer.parseInt(shibAuthnContextClass.substring(shibAuthnContextClass.length() - 1));
            return loa == 5 ? 1 : loa;
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    public void setManageConsentEnabled(boolean manageConsentEnabled) {
        isManageConsentEnabled = manageConsentEnabled;
    }
}
