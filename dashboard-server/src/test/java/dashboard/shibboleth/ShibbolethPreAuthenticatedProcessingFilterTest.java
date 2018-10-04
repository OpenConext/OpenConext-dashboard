package dashboard.shibboleth;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import dashboard.domain.CoinAuthority;
import dashboard.domain.CoinUser;
import dashboard.domain.IdentityProvider;
import dashboard.manage.Manage;
import dashboard.sab.Sab;
import dashboard.sab.SabRoleHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static dashboard.domain.CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN;
import static dashboard.domain.CoinAuthority.Authority.ROLE_DASHBOARD_SUPER_USER;
import static dashboard.domain.CoinAuthority.Authority.ROLE_DASHBOARD_VIEWER;
import static dashboard.shibboleth.ShibbolethHeader.Name_Id;
import static dashboard.shibboleth.ShibbolethHeader.Shib_Authenticating_Authority;
import static dashboard.shibboleth.ShibbolethHeader.Shib_EduPersonEntitlement;
import static dashboard.shibboleth.ShibbolethHeader.Shib_MemberOf;

@RunWith(MockitoJUnitRunner.class)
public class ShibbolethPreAuthenticatedProcessingFilterTest {

    @InjectMocks
    private ShibbolethPreAuthenticatedProcessingFilter subject;

    @Mock
    private Manage manageMock;

    @Mock
    private Sab sab;

    @Before
    public void before() {
        subject.setAdminSurfConextIdpRole("SURFconextverantwoordelijke");
        subject.setViewerSurfConextIdpRole("SURFconextbeheerder");

        subject.setDashboardAdmin("dashboard.admin");
        subject.setDashboardSuperUser("dashboard.super.user");
        subject.setDashboardViewer("dashboard.viewer");

        when(sab.getRoles(anyString())).thenReturn(Optional.empty());
    }

    @Test
    public void shouldCreateACoinUserBasedOnShibbolethHeaders() {
        HttpServletRequest requestMock = mock(HttpServletRequest.class);
        when(requestMock.getHeader(anyString())).then(invocation -> invocation.getArguments()[0] + "_value");
        when(manageMock.getIdentityProvider("Shib-Authenticating-Authority_value", false)).thenReturn(Optional.of(new
            IdentityProvider()));

        CoinUser coinUser = (CoinUser) subject.getPreAuthenticatedPrincipal(requestMock);

        assertThat(coinUser.getUid(), is("name-id_value"));
        assertThat(coinUser.getEmail(), is("Shib-InetOrgPerson-mail_value"));
        assertThat(coinUser.getDisplayName(), is("displayName_value"));
    }

    @Test
    public void shouldSetTheInstitionIdOfTheUser() {
        IdentityProvider idp = new IdentityProvider();
        idp.setInstitutionId("my-institution-id");

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
        when(manageMock.getIdentityProvider("Shib-Authenticating-Authority_value1", false)).thenReturn(Optional.of(new
            IdentityProvider()));

        CoinUser coinUser = (CoinUser) subject.getPreAuthenticatedPrincipal(requestMock);

        assertThat(coinUser.getUid(), is("name-id_value1"));
        assertThat(coinUser.getEmail(), is("Shib-InetOrgPerson-mail_value1"));

        assertThat(coinUser.getAttributeMap().get(Shib_EduPersonEntitlement), contains
            ("eduPersonEntitlement_value1", "eduPersonEntitlement_value2"));
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
        doAssertSabEntitlement("urn:mace:surfnet.nl:surfnet.nl:sab:SURFconextverantwoordelijke",
            ROLE_DASHBOARD_ADMIN, null);
        doAssertSabEntitlement("urn:mace:surfnet.nl:surfnet.nl:sab:SURFconextbeheerder",
            ROLE_DASHBOARD_VIEWER, null);
    }

    @Test
    public void shouldAddTeamsEntitlements() {
        doAssertSabEntitlement("dashboard.admin",
            ROLE_DASHBOARD_ADMIN, Shib_MemberOf);
        doAssertSabEntitlement("dashboard.viewer",
            ROLE_DASHBOARD_VIEWER, Shib_MemberOf);
        doAssertSabEntitlement("dashboard.super.user",
            ROLE_DASHBOARD_SUPER_USER, Shib_MemberOf);
    }

    @Test
    public void noRoles() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(Name_Id.getValue(), "uid");
        when(manageMock.getIdentityProvider("mock-idp", false)).thenReturn(Optional.of(new IdentityProvider()));
        request.addHeader(Shib_Authenticating_Authority.getValue(), "mock-idp");
        CoinUser user = (CoinUser) subject.getPreAuthenticatedPrincipal(request);
        assertEquals(0, user.getAuthorityEnums().size());
    }

    private void doAssertSabEntitlement(String entitlement, CoinAuthority.Authority role, ShibbolethHeader headerName) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        if (headerName != null) {
            request.addHeader(headerName.getValue(), entitlement);
        } else {
            when(sab.getRoles(anyString())).thenReturn(Optional.of(new SabRoleHolder("Org", Arrays.asList
                (entitlement))));
        }
        request.addHeader(Name_Id.getValue(), "uid");
        when(manageMock.getIdentityProvider("mock-idp", false)).thenReturn(Optional.of(new IdentityProvider()));
        request.addHeader(Shib_Authenticating_Authority.getValue(), "mock-idp");
        CoinUser user = (CoinUser) subject.getPreAuthenticatedPrincipal(request);
        assertEquals(1, user.getAuthorityEnums().size());
        assertTrue(user.getAuthorityEnums().contains(role));
    }

}
