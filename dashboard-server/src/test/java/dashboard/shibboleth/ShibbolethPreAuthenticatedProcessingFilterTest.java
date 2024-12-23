package dashboard.shibboleth;

import com.google.common.collect.ImmutableList;
import dashboard.domain.CoinAuthority;
import dashboard.domain.CoinUser;
import dashboard.domain.IdentityProvider;
import dashboard.manage.Manage;
import dashboard.service.impl.JiraClientMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static dashboard.domain.CoinAuthority.Authority.*;
import static dashboard.shibboleth.ShibbolethHeader.*;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ShibbolethPreAuthenticatedProcessingFilterTest {

    public static final String INSTITUTION_ID = "institution_id";
    @InjectMocks
    private ShibbolethPreAuthenticatedProcessingFilter subject =
            new ShibbolethPreAuthenticatedProcessingFilter(new JiraClientMock(null, null));

    @Mock
    private Manage manageMock;

    @Before
    public void before() {
        subject.setAdminSurfConextIdpRole("SURFconextverantwoordelijke");
        subject.setViewerSurfConextIdpRole("SURFconextbeheerder");

        subject.setDashboardAdmin("dashboard.admin");
        subject.setDashboardSuperUser(Arrays.asList("dashboard.super.user"));
        subject.setDashboardViewer("dashboard.viewer");
        subject.setManageConsentEnabled(true);

    }

    @Test
    public void shouldCreateACoinUserBasedOnShibbolethHeaders() {
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getHeader(anyString())).then(invocation -> invocation.getArguments()[0] + "_value");
        IdentityProvider identityProvider = new IdentityProvider();
        identityProvider.setState("prodaccepted");
        when(manageMock.getIdentityProvider("Shib-Authenticating-Authority_value", false)).thenReturn(Optional.of(identityProvider));

        CoinUser coinUser = (CoinUser) subject.getPreAuthenticatedPrincipal(requestMock);

        assertThat(coinUser.getUid(), is("name-id_value"));
        assertThat(coinUser.getEmail(), is("Shib-InetOrgPerson-mail_value"));
        assertThat(coinUser.getDisplayName(), is("displayName_value"));
    }

    @Test
    public void shouldSetTheInstitionIdOfTheUser() {
        IdentityProvider idp = new IdentityProvider();
        idp.setInstitutionId("my-institution-id");
        idp.setState("prodaccepted");

        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getHeader(anyString())).then(invocation -> invocation.getArguments()[0] + "_value");
        when(manageMock.getIdentityProvider("Shib-Authenticating-Authority_value", false)).thenReturn(Optional.of(idp));
        when(manageMock.getInstituteIdentityProviders("my-institution-id")).thenReturn(ImmutableList.of(idp));

        CoinUser coinUser = (CoinUser) subject.getPreAuthenticatedPrincipal(requestMock);

        assertThat(coinUser.getInstitutionId(), is("my-institution-id"));
    }

    @Test
    public void shouldSplitMultiValueAttribute() {
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getHeader(anyString())).then(invocation -> invocation.getArguments()[0] + "_value1;" +
                invocation.getArguments()[0] + "_value2");
        IdentityProvider identityProvider = new IdentityProvider();
        identityProvider.setState("prodaccepted");

        when(manageMock.getIdentityProvider("Shib-Authenticating-Authority_value1", false)).thenReturn(Optional.of(identityProvider));

        CoinUser coinUser = (CoinUser) subject.getPreAuthenticatedPrincipal(requestMock);

        assertThat(coinUser.getUid(), is("name-id_value1"));
        assertThat(coinUser.getEmail(), is("Shib-InetOrgPerson-mail_value1"));

        assertThat(coinUser.getAttributeMap().get(Shib_EduPersonEntitlement), contains
                ("eduPersonEntitlement_value1", "eduPersonEntitlement_value2"));
    }

    @Test
    public void testWithOrganisationSabMismatch() {
        Arrays.asList(null, "", "no_match").forEach(institutionId -> {
            MockHttpServletRequest request = httpRequest();
            request.addHeader(Name_Id.getValue(), "uid");
            IdentityProvider idp = new IdentityProvider("mock-idp", "SURFNET", "name", 1L);
            idp.setState("prodaccepted");
            when(manageMock.getIdentityProvider("mock-idp", false)).thenReturn(Optional.of(idp));
            when(manageMock.getInstituteIdentityProviders("SURFNET")).thenReturn(Collections.singletonList(idp));
            request.addHeader(Shib_Authenticating_Authority.getValue(), "mock-idp");
            CoinUser user = (CoinUser) subject.getPreAuthenticatedPrincipal(request);
            assertEquals(1, user.getAuthorityEnums().size());
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailWhenTheNameIdHeaderIsNotSet() {
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getHeader(anyString())).thenReturn("headerValue");
        when(requestMock.getHeader(Name_Id.getValue())).thenReturn(null);

        subject.getPreAuthenticatedPrincipal(requestMock);
    }

    @Test
    public void shouldAddSabEntitlements() {
        doAssertSabEntitlement(ROLE_DASHBOARD_ADMIN, Map.of(Shib_SURFautorisaties,
                List.of("urn:mace:surfnet.nl:surfnet.nl:sab:role:SURFconextverantwoordelijke",
                        "urn:mace:surfnet.nl:surfnet.nl:sab:organizationGUID:" + INSTITUTION_ID)));
        doAssertSabEntitlement(ROLE_DASHBOARD_VIEWER, Map.of(Shib_SURFautorisaties,
                List.of("urn:mace:surfnet.nl:surfnet.nl:sab:role:SURFconextbeheerder",
                        "urn:mace:surfnet.nl:surfnet.nl:sab:organizationGUID:" + INSTITUTION_ID)));
        doAssertSabEntitlement(ROLE_DASHBOARD_MEMBER, Map.of());

        MockHttpServletRequest request = new MockHttpServletRequest();
        CoinUser user = (CoinUser) subject.getPreAuthenticatedPrincipal(request);
        assertEquals(1, user.getAuthorityEnums().size());
        assertTrue(user.getAuthorityEnums().contains(ROLE_DASHBOARD_GUEST));


        doAssertSabEntitlement(ROLE_DASHBOARD_MEMBER, Map.of(Shib_SURFautorisaties,
                List.of("urn:mace:surfnet.nl:surfnet.nl:sab:role:SURFconextbeheerder",
                        "urn:mace:surfnet.nl:surfnet.nl:sab:organizationGUID:nope")));
    }

    @Test
    public void shouldAddTeamsEntitlements() {
        doAssertSabEntitlement(ROLE_DASHBOARD_ADMIN, Map.of(Shib_MemberOf, List.of("dashboard.admin")));
        doAssertSabEntitlement(ROLE_DASHBOARD_VIEWER, Map.of(Shib_MemberOf, List.of("dashboard.viewer")));
        doAssertSabEntitlement(ROLE_DASHBOARD_SUPER_USER, Map.of(Shib_MemberOf, List.of("dashboard.super.user")));
    }

    @Test
    public void noRoles() {
        MockHttpServletRequest request = httpRequest();
        request.addHeader(Name_Id.getValue(), "uid");
        IdentityProvider identityProvider = new IdentityProvider();
        identityProvider.setState("prodaccepted");
        when(manageMock.getIdentityProvider("mock-idp", false)).thenReturn(Optional.of(identityProvider));
        CoinUser user = (CoinUser) subject.getPreAuthenticatedPrincipal(request);
        assertEquals(1, user.getAuthorityEnums().size());
    }

    private void doAssertSabEntitlement(CoinAuthority.Authority role, Map<ShibbolethHeader, List<String>> headers) {
        MockHttpServletRequest request = httpRequest();
        headers.forEach((headerName, values) -> request.addHeader(headerName.getValue(), String.join(";", values)));
        IdentityProvider idp = new IdentityProvider("mock-idp", INSTITUTION_ID, "name", 1L);
        idp.setState("prodaccepted");
        when(manageMock.getIdentityProvider("mock-idp", false)).thenReturn(Optional.of(idp));
        when(manageMock.getInstituteIdentityProviders(INSTITUTION_ID)).thenReturn(Collections.singletonList(idp));
        request.addHeader(Shib_Authenticating_Authority.getValue(), "mock-idp");
        CoinUser user = (CoinUser) subject.getPreAuthenticatedPrincipal(request);
        assertEquals(1, user.getAuthorityEnums().size());
        assertTrue(user.getAuthorityEnums().contains(role));
    }

    private MockHttpServletRequest httpRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Name_Id.getValue(), "uid");
        request.addHeader(Shib_Authenticating_Authority.getValue(), "mock-idp");
        return request;
    }

}
