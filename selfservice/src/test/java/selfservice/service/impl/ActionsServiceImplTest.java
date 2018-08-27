package selfservice.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import selfservice.domain.Action;
import selfservice.domain.IdentityProvider;
import selfservice.domain.ServiceProvider;
import selfservice.manage.EntityType;
import selfservice.manage.Manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ActionsServiceImplTest {

    @InjectMocks
    private ActionsServiceImpl service;

    @Mock
    private JiraClient jiraClientMock;

    @Mock
    private Manage manageMock;

    @Test
    public void forBackwardCompatibilityShouldFillUserFromBody() {
        when(manageMock.getIdentityProvider("idp", true)).thenReturn(Optional.of(new IdentityProvider("idp",
            "idp-institution", "idp-name", 1L)));
        Map<String, Object> result = new HashMap<>();
        result.put("issues", ImmutableList.of
            (Action
                .builder()
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
        when(manageMock.getServiceProvider("sp", EntityType.saml20_sp, true)).thenReturn(Optional.of(new ServiceProvider
            (ImmutableMap.of("entityid", "sp", "eid", 1L))));
        when(jiraClientMock.getTasks("idp", 0, 20)).thenReturn(result);

        Map<String, Object> actionResult = service.getActions("idp", 0, 20);
        List<Action> actions = (List<Action>) actionResult.get("issues");

        assertEquals(1, actions.size());
        assertEquals("Teun Fransen", actions.get(0).getUserName());
        assertEquals("Teun.Fransen@surfnet.nl", actions.get(0).getUserEmail());
    }

}
