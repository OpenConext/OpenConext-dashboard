package selfservice.shibboleth;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.StringUtils;
import selfservice.domain.CoinAuthority;
import selfservice.domain.CoinUser;
import selfservice.domain.IdentityProvider;
import selfservice.manage.Manage;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkState;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toMap;
import static org.springframework.util.CollectionUtils.isEmpty;
import static org.springframework.util.StringUtils.hasText;
import static selfservice.domain.CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN;
import static selfservice.domain.CoinAuthority.Authority.ROLE_DASHBOARD_SUPER_USER;
import static selfservice.domain.CoinAuthority.Authority.ROLE_DASHBOARD_VIEWER;
import static selfservice.shibboleth.ShibbolethHeader.*;

public class ShibbolethPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    public static final Map<String, ShibbolethHeader> shibHeaders;
    private static final Splitter shibHeaderValueSplitter = Splitter.on(';').omitEmptyStrings();
    private final static Logger LOG = LoggerFactory.getLogger(ShibbolethPreAuthenticatedProcessingFilter.class);


    static {
        shibHeaders = ImmutableMap.<String, ShibbolethHeader>builder()
            .put("urn:mace:dir:attribute-def:uid", Shib_Uid)
            .put("urn:mace:dir:attribute-def:sn", Shib_SurName)
            .put("urn:mace:dir:attribute-def:givenName", Shib_GivenName)
            .put("urn:mace:dir:attribute-def:cn", Shib_CommonName)
            .put("urn:mace:dir:attribute-def:displayName", Shib_DisplayName)
            .put("urn:mace:dir:attribute-def:mail", Shib_Email)
            .put("urn:mace:dir:attribute-def:eduPersonAffiliation", Shib_EduPersonAffiliation)
            .put("urn:mace:dir:attribute-def:eduPersonScopedAffiliation", Shib_EduPersonScopedAffiliation)
            .put("urn:mace:dir:attribute-def:eduPersonEntitlement", Shib_EduPersonEntitlement)
            .put("urn:mace:dir:attribute-def:isMemberOf", Shib_MemberOf)
            .put("urn:mace:dir:attribute-def:eduPersonPrincipalName", Shib_EduPersonPN)
            .put("urn:mace:dir:attribute-def:preferredLanguage", Shib_PreferredLanguage)
            .put("urn:mace:terena.org:attribute-def:schacHomeOrganization", Shib_HomeOrg)
            .put("urn:mace:terena.org:attribute-def:schacHomeOrganizationType", Shib_SchacHomeOrganizationType)
            .put("urn:mace:surffederatie.nl:attribute-def:nlEduPersonHomeOrganization",
                Shib_NlEduPersonHomeOrganization)
            .put("urn:mace:surffederatie.nl:attribute-def:nlEduPersonStudyBranch", Shib_NlEduPersonStudyBranch)
            .put("urn:mace:surffederatie.nl:attribute-def:nlStudielinkNummer", Shib_NlStudielinkNummer)
            .put("urn:mace:surffederatie.nl:attribute-def:nlDigitalAuthorIdentifier", Shib_NlDigitalAuthorIdentifier)
            .put("urn:mace:surffederatie_nl:attribute-def:nlEduPersonOrgUnit", Shib_NlEduPersonOrgUnit)
            .put("urn:schac:attribute-def:schacPersonalUniqueCode", Shib_SchacPersonalUniqueCode)
            .put("urn:oid:1.3.6.1.4.1.1076.20.100.10.10.1", Shib_UserStatus)
            .put("urn:oid:1.3.6.1.4.1.5923.1.1.1.1", Shib_Accountstatus)
            .put("urn:oid:1.3.6.1.4.1.1076.20.100.10.10.2", Shib_VoName)
            .build();
    }

    private Manage manage;
    private String dashboardAdmin;
    private String dashboardViewer;
    private String dashboardSuperUser;
    private String adminSurfConextIdpRole;
    private String viewerSurfConextIdpRole;

    public ShibbolethPreAuthenticatedProcessingFilter(AuthenticationManager authenticationManager,
                                                      Manage manage,
                                                      String dashboardAdmin,
                                                      String dashboardViewer,
                                                      String dashboardSuperUser,
                                                      String adminSufConextIdpRole,
                                                      String viewerSurfConextIdpRole) {
        setAuthenticationManager(authenticationManager);
        this.manage = manage;
        this.dashboardAdmin = dashboardAdmin;
        this.dashboardSuperUser = dashboardSuperUser;
        this.dashboardViewer = dashboardViewer;
        this.adminSurfConextIdpRole = adminSufConextIdpRole;
        this.viewerSurfConextIdpRole = viewerSurfConextIdpRole;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(final HttpServletRequest request) {
        String uid = getFirstShibHeaderValue(Name_Id, request)
            .orElseThrow(() -> new IllegalArgumentException(String.format("Missing name-id Shibboleth header (%s)",
                request.getRequestURL())));

        String idpId = getFirstShibHeaderValue(Shib_Authenticating_Authority, request)
            .orElseThrow(() -> new IllegalArgumentException("Missing Shib-Authenticating-Authority Shibboleth header"));

        if (LOG.isDebugEnabled()) {
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                ArrayList<String> list = Collections.list(headerNames);
                LOG.debug("Received headers for {}: {} ", uid, list.stream().collect(toMap(
                    name -> name,
                    name -> {
                        Enumeration<String> headers = request.getHeaders(name);
                        return headers != null ? Collections.list(headers) : Collections.emptyList();
                    })));
            }
        }

        CoinUser coinUser = new CoinUser();
        coinUser.setUid(uid);
        coinUser.setDisplayName(getFirstShibHeaderValue(Shib_DisplayName, request).orElse(null));
        coinUser.setEmail(getFirstShibHeaderValue(Shib_Email, request).orElse(null));
        coinUser.setSchacHomeOrganization(getFirstShibHeaderValue(Shib_HomeOrg, request).orElse(null));

        Map<ShibbolethHeader, List<String>> attributes = shibHeaders.values().stream()
            .filter(h -> StringUtils.hasText(request.getHeader(h.getValue())))
            .collect(toMap(h -> h, h -> getShibHeaderValues(h, request)));
        coinUser.setAttributeMap(attributes);

        List<String> groups = getShibHeaderValues(Shib_MemberOf, request);
        this.addDashboardRoleForMemberships(coinUser, groups);

        List<String> entitlements = getShibHeaderValues(Shib_EduPersonEntitlement, request);
        this.addDashboardRoleForEntitlements(coinUser, entitlements);

        List<IdentityProvider> institutionIdentityProviders = getInstitutionIdentityProviders(idpId);

        checkState(!isEmpty(institutionIdentityProviders), "no InstitutionIdentityProviders found for '" + idpId + "'");

        if (institutionIdentityProviders.size() == 1) {
            IdentityProvider idp = institutionIdentityProviders.get(0);
            coinUser.setIdp(idp);
            coinUser.addInstitutionIdp(idp);
        } else {
            coinUser.setIdp(getCurrentIdp(idpId, institutionIdentityProviders));
            coinUser.getInstitutionIdps().addAll(institutionIdentityProviders);
            Collections.sort(coinUser.getInstitutionIdps(), (lh, rh) -> lh.getName().compareTo(rh.getName()));
        }

        institutionIdentityProviders.stream()
            .filter(idp -> hasText(idp.getInstitutionId()))
            .findFirst()
            .ifPresent(idp -> coinUser.setInstitutionId(idp.getInstitutionId()));

        return coinUser;
    }

    private void addDashboardRoleForMemberships(CoinUser user, List<String> groups) {
        if (groups.contains(dashboardSuperUser)) {
            user.addAuthority(new CoinAuthority(ROLE_DASHBOARD_SUPER_USER));
        } else if (groups.contains(dashboardAdmin)) {
            user.addAuthority(new CoinAuthority(ROLE_DASHBOARD_ADMIN));
        } else if (groups.contains(dashboardViewer)) {
            user.addAuthority(new CoinAuthority(ROLE_DASHBOARD_VIEWER));
        }
    }

    private void addDashboardRoleForEntitlements(CoinUser user, List<String> entitlements) {
        if (entitlements.stream().anyMatch(entitlement -> entitlement.indexOf(adminSurfConextIdpRole) > -1)) {
            user.addAuthority(new CoinAuthority(ROLE_DASHBOARD_ADMIN));
        } else if (entitlements.stream().anyMatch(entitlement -> entitlement.indexOf(viewerSurfConextIdpRole) > -1)) {
            user.addAuthority(new CoinAuthority(ROLE_DASHBOARD_VIEWER));
        }
    }

    private List<IdentityProvider> getInstitutionIdentityProviders(String idpId) {
        return manage.getIdentityProvider(idpId).map(idp -> {
            String institutionId = idp.getInstitutionId();
            return hasText(institutionId) ? manage.getInstituteIdentityProviders(institutionId) : singletonList(idp);
        }).orElse(Collections.emptyList());
    }

    private Optional<String> getFirstShibHeaderValue(ShibbolethHeader headerName, HttpServletRequest request) {
        return getShibHeaderValues(headerName, request).stream().findFirst();
    }

    private List<String> getShibHeaderValues(ShibbolethHeader headerName, HttpServletRequest request) {
        String headerValue = request.getHeader(headerName.getValue());

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

    public void setDashboardAdmin(String dashboardAdmin) {
        this.dashboardAdmin = dashboardAdmin;
    }

    public void setDashboardViewer(String dashboardViewer) {
        this.dashboardViewer = dashboardViewer;
    }

    public void setDashboardSuperUser(String dashboardSuperUser) {
        this.dashboardSuperUser = dashboardSuperUser;
    }

    public void setAdminSurfConextIdpRole(String adminSurfConextIdpRole) {
        this.adminSurfConextIdpRole = adminSurfConextIdpRole;
    }

    public void setViewerSurfConextIdpRole(String viewerSurfConextIdpRole) {
        this.viewerSurfConextIdpRole = viewerSurfConextIdpRole;
    }
}
