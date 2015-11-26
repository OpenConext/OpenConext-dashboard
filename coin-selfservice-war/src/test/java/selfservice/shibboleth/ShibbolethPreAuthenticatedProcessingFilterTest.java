package selfservice.shibboleth;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.ImmutableList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import selfservice.domain.CoinUser;
import selfservice.domain.InstitutionIdentityProvider;
import selfservice.service.Csa;

@RunWith(MockitoJUnitRunner.class)
public class ShibbolethPreAuthenticatedProcessingFilterTest {

  @InjectMocks
  private ShibbolethPreAuthenticatedProcessingFilter subject;

  @Mock
  private Csa csaMock;

  @Test
  public void shouldCreateACoinUserBasedOnShibbolethHeaders() {
    HttpServletRequest requestMock = mock(HttpServletRequest.class);
    when(requestMock.getHeader(anyString())).then(invocation -> invocation.getArguments()[0] + "_value");
    when(csaMock.getInstitutionIdentityProviders("Shib-Authenticating-Authority_value")).thenReturn(ImmutableList.of(new InstitutionIdentityProvider()));

    CoinUser coinUser = (CoinUser) subject.getPreAuthenticatedPrincipal(requestMock);

    assertThat(coinUser.getUid(), is("shib-user_value"));
    assertThat(coinUser.getEmail(), is("Shib-email_value"));
    assertThat(coinUser.getDisplayName(), is("Shib-displayName_value"));
  }
}
