package selfservice.shibboleth;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static selfservice.shibboleth.ShibbolethHeader.Name_Id;
import static selfservice.shibboleth.ShibbolethHeader.Shib_EduPersonEntitlement;

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.ImmutableList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import selfservice.domain.CoinUser;
import selfservice.domain.IdentityProvider;
import selfservice.service.IdentityProviderService;

@RunWith(MockitoJUnitRunner.class)
public class ShibbolethPreAuthenticatedProcessingFilterTest {

  @InjectMocks
  private ShibbolethPreAuthenticatedProcessingFilter subject;

  @Mock
  private IdentityProviderService idpServiceMock;

  @Test
  public void shouldCreateACoinUserBasedOnShibbolethHeaders() {
    HttpServletRequest requestMock = mock(HttpServletRequest.class);
    when(requestMock.getHeader(anyString())).then(invocation -> invocation.getArguments()[0] + "_value");
    when(idpServiceMock.getIdentityProvider("Shib-Authenticating-Authority_value")).thenReturn(Optional.of(new IdentityProvider()));

    CoinUser coinUser = (CoinUser) subject.getPreAuthenticatedPrincipal(requestMock);

    assertThat(coinUser.getUid(), is("name-id_value"));
    assertThat(coinUser.getEmail(), is("Shib-email_value"));
    assertThat(coinUser.getDisplayName(), is("Shib-displayName_value"));
  }

  @Test
  public void shouldSetTheInstitionIdOfTheUser() {
    IdentityProvider idp = new IdentityProvider();
    idp.setInstitutionId("my-institution-id");

    HttpServletRequest requestMock = mock(HttpServletRequest.class);
    when(requestMock.getHeader(anyString())).then(invocation -> invocation.getArguments()[0] + "_value");
    when(idpServiceMock.getIdentityProvider("Shib-Authenticating-Authority_value")).thenReturn(Optional.of(idp));
    when(idpServiceMock.getInstituteIdentityProviders("my-institution-id")).thenReturn(ImmutableList.of(idp));

    CoinUser coinUser = (CoinUser) subject.getPreAuthenticatedPrincipal(requestMock);

    assertThat(coinUser.getInstitutionId(), is("my-institution-id"));
  }

  @Test
  public void shouldSplitMultiValueAttribute() {
    HttpServletRequest requestMock = mock(HttpServletRequest.class);
    when(requestMock.getHeader(anyString())).then(invocation -> invocation.getArguments()[0] + "_value1;" + invocation.getArguments()[0] + "_value2");
    when(idpServiceMock.getIdentityProvider("Shib-Authenticating-Authority_value1")).thenReturn(Optional.of(new IdentityProvider()));

    CoinUser coinUser = (CoinUser) subject.getPreAuthenticatedPrincipal(requestMock);

    assertThat(coinUser.getUid(), is("name-id_value1"));
    assertThat(coinUser.getEmail(), is("Shib-email_value1"));

    assertThat(coinUser.getAttributeMap().get(Shib_EduPersonEntitlement), contains("Shib-eduPersonEntitlement_value1", "Shib-eduPersonEntitlement_value2"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldFailWhenTheNameIdHeaderIsNotSet() {
    HttpServletRequest requestMock = mock(HttpServletRequest.class);
    when(requestMock.getHeader(anyString())).thenReturn("headerValue");
    when(requestMock.getHeader(Name_Id.getValue())).thenReturn(null);

    subject.getPreAuthenticatedPrincipal(requestMock);
  }

}
