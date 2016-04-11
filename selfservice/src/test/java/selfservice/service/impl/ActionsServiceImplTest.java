package selfservice.service.impl;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import selfservice.domain.Action;
import selfservice.domain.IdentityProvider;
import selfservice.domain.ServiceProvider;
import selfservice.serviceregistry.ServiceRegistry;

@RunWith(MockitoJUnitRunner.class)
public class ActionsServiceImplTest {

  @InjectMocks
  private ActionsServiceImpl service;

  @Mock
  private JiraClient jiraClientMock;

  @Mock
  private ServiceRegistry serviceRegistryMock;

  @Test
  public void forBackwardCompatibilityShouldFillUserFromBody() {

    when(serviceRegistryMock.getIdentityProvider("idp")).thenReturn(Optional.of(new IdentityProvider("idp", "idp-institution", "idp-name")));
    when(serviceRegistryMock.getServiceProvider("sp")).thenReturn(new ServiceProvider("sp"));
    when(jiraClientMock.getTasks("idp")).thenReturn(ImmutableList.of(Action.builder()
        .idpId("idp")
        .spId("sp")
        .body("Request: Create a new connection\n" +
        "Applicant name: Teun Fransen\n" +
        "Applicant email: Teun.Fransen@surfnet.nl\n" +
        "Identity Provider: https://idp.surfnet.nl\n" +
        "Service Provider: https://bod.acc.dlp.surfnet.nl/shibboleth\n" +
        "Time: 14:43 18-03-13\n" +
        "Service Provider: https://bod.acc.dlp.surfnet.nl/shibboleth\n" +
        "Remark from user: Teun.Fransen@surfnet.nl\n" +
        "\n" +
        "test").build()));

    List<Action> actions = service.getActions("idp");

    assertThat(actions, hasSize(1));
    assertThat(actions.get(0).getUserName(), is("Teun Fransen"));
    assertThat(actions.get(0).getUserEmail(), is("Teun.Fransen@surfnet.nl"));
  }

}
