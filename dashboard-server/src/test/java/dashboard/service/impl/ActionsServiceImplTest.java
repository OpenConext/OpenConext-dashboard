package dashboard.service.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import dashboard.domain.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import dashboard.manage.EntityType;
import dashboard.manage.Manage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
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
        List<Action> issues = ImmutableList.of(Action.builder()
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
                        "test").build());
        JiraResponse result = new JiraResponse(issues, 15, 0, 20);

        when(manageMock.getServiceProvider("sp", EntityType.saml20_sp, true)).thenReturn(Optional.of(new ServiceProvider
                (ImmutableMap.of("entityid", "sp", "eid", 1L))));
        when(jiraClientMock.searchTasks(anyString(), Matchers.any(JiraFilter.class))).thenReturn(result);

        JiraResponse response = service.searchTasks("idp", new JiraFilter());
        List<Action> actions = response.getIssues();

        assertEquals(1, actions.size());
        assertEquals("Teun Fransen", actions.get(0).getUserName());
        assertEquals("Teun.Fransen@surfnet.nl", actions.get(0).getUserEmail());
    }

}
