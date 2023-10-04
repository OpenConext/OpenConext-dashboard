package dashboard.shibboleth;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import dashboard.domain.*;
import dashboard.manage.Manage;
import dashboard.sab.Sab;
import dashboard.sab.SabRoleHolder;
import dashboard.service.impl.JiraClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkState;
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
                .build();
    }


    private String organization;

    private Manage manage;
    private Sab sab;
    private JiraClient jiraClient;
    private String dashboardAdmin;
    private String dashboardViewer;
    private List<String> dashboardSuperUser;
    private String adminSurfConextIdpRole;
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
        if (headerNames != null && LOG.isTraceEnabled()) {
            ArrayList<String> list = Collections.list(headerNames);
            LOG.trace("Received headers {}", list.stream().collect(toMap(
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
        }

        Optional<SabRoleHolder> roles = sab.getRoles(uid);
        LOG.debug("SAB: received roles {} and organization {}",
                roles.isPresent() ? roles.get().getRoles() : "None",
                roles.isPresent() ? roles.get().getOrganisation() : "None");


        List<String> institutionIds = institutionIdentityProviders.stream()
                .filter(provider -> StringUtils.hasText(provider.getInstitutionId()))
                .map(provider -> provider.getInstitutionId().toUpperCase()).collect(toList());

        LOG.debug("Manage: received institution ID's {} ", institutionIds);

        this.addDashboardRoleForEntitlements(coinUser, roles,
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
            if (!idp.isDisplayAdminEmailsInDashboard()) {
                idp.getContactPersons().clear();
            }
            coinUser.getInstitutionIdps().forEach(anIdp -> {
                if (!anIdp.isDisplayAdminEmailsInDashboard()) {
                    anIdp.getContactPersons().clear();
                }
            });
        }

        String idpEntityId = coinUser.getIdp().getId();
        JiraFilter jiraFilter = new JiraFilter();
        jiraFilter.setTypes(Arrays.asList(Action.Type.LINKINVITE, Action.Type.LINKREQUEST, Action.Type.UNLINKINVITE, Action.Type.UNLINKREQUEST));
        jiraFilter.setStatuses(Arrays.asList("To Do", "Awaiting Input"));

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

    private void addDashboardRoleForEntitlements(CoinUser user, Optional<SabRoleHolder> roles, List<String> institutionIds) {
        roles.ifPresent(sabRoleHolder -> {
            List<String> entitlements = sabRoleHolder.getRoles();
            String organisation = StringUtils.hasText(sabRoleHolder.getOrganisation()) ? sabRoleHolder.getOrganisation().toUpperCase() : null;
            boolean institutionIdMatch = StringUtils.hasText(sabRoleHolder.getOrganisation()) &&
                    institutionIds.contains(organisation);
            if (institutionIdMatch) {
                if (entitlements.stream().anyMatch(entitlement -> entitlement.indexOf(adminSurfConextIdpRole) > -1)) {
                    user.addAuthority(new CoinAuthority(ROLE_DASHBOARD_ADMIN));
                } else if (entitlements.stream().anyMatch(entitlement -> entitlement.indexOf(viewerSurfConextIdpRole) > -1)) {
                    user.addAuthority(new CoinAuthority(ROLE_DASHBOARD_VIEWER));
                }
            } else {
                LOG.info("SAB: received entitlements, but the institutionIds from Manage {} do not match the organization name from SAB {}",
                        institutionIds, organisation);
            }
        });
    }

    private List<IdentityProvider> getInstitutionIdentityProviders(String idpId) {
        Optional<IdentityProvider> optionalIdentityProvider = manage.getIdentityProvider(idpId, false);
        List<IdentityProvider> identityProviders = optionalIdentityProvider.map(idp -> {
            String institutionId = idp.getInstitutionId();
            return hasText(institutionId) ? manage.getInstituteIdentityProviders(institutionId) : singletonList(idp);
        }).orElse(Collections.emptyList());
        return identityProviders;
    }

    private Optional<String> getFirstShibHeaderValue(ShibbolethHeader headerName, HttpServletRequest request) {
        return getShibHeaderValues(headerName, request).stream().findFirst();
    }

    private List<String> getShibHeaderValues(ShibbolethHeader headerName, HttpServletRequest request) {
        String header = request.getHeader(headerName.getValue());
        try {
            String headerValue = StringUtils.hasText(header) ?
                    new String(header.getBytes("ISO8859-1"), "UTF-8") : null;
            return headerValue == null ? Collections.emptyList() : shibHeaderValueSplitter.splitToList(headerValue);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
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
            return Integer.parseInt(shibAuthnContextClass.substring(shibAuthnContextClass.length() - 1));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    public void setDashboardAdmin(String dashboardAdmin) {
        this.dashboardAdmin = dashboardAdmin;
    }

    public void setDashboardViewer(String dashboardViewer) {
        this.dashboardViewer = dashboardViewer;
    }

    public void setDashboardSuperUser(List<String> dashboardSuperUsers) {
        this.dashboardSuperUser = dashboardSuperUsers;
    }

    public void setAdminSurfConextIdpRole(String adminSurfConextIdpRole) {
        this.adminSurfConextIdpRole = adminSurfConextIdpRole;
    }

    public void setViewerSurfConextIdpRole(String viewerSurfConextIdpRole) {
        this.viewerSurfConextIdpRole = viewerSurfConextIdpRole;
    }

    public void setManageConsentEnabled(boolean manageConsentEnabled) {
        isManageConsentEnabled = manageConsentEnabled;
    }
}
