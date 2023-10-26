package dashboard.service.impl;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import dashboard.domain.Action;
import dashboard.domain.JiraFilter;
import dashboard.domain.JiraResponse;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class JiraClientImplTest {

    private JiraClientImpl jiraClient = new JiraClientImpl("http://localhost:8891", "user", "password", "CTX", 5, Environment.test);

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8891);

    public JiraClientImplTest() throws IOException {
    }

    @Test
    public void getTasks() throws IOException {
        String jiraResponse = IOUtils.toString(new ClassPathResource("jira-json/tasks.json").getInputStream(), Charset.defaultCharset());

        stubFor(post(urlPathEqualTo("/search")).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json").withBody(jiraResponse)));

        JiraFilter jiraFilter = new JiraFilter();
        JiraResponse response = jiraClient.searchTasks("https://mock-idp", jiraFilter);
        assertEquals(2, response.getIssues().size());
    }

    @Test
    public void getTasksRejected() throws IOException {
        String jiraResponse = IOUtils.toString(new ClassPathResource("jira-json/rejected_task.json").getInputStream(), Charset.defaultCharset());

        stubFor(post(urlPathEqualTo("/search")).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json").withBody(jiraResponse)));

        JiraFilter jiraFilter = new JiraFilter();
        JiraResponse response = jiraClient.searchTasks("https://mock-idp", jiraFilter);
        long count = response.getIssues().stream().filter(action -> action.isRejected()).count();
        assertEquals(1l, count);
    }

    @Test
    public void actionToIssueIdentifier() {
        List<String> identifiers = Arrays.asList(Action.Type.values()).stream().map(type -> jiraClient.actionToIssueIdentifier(type)).collect(toList());

        assertEquals("11104,11105,11106,11401,12201", String.join(",", identifiers));
        assertEquals(5l, identifiers.stream().map(identifier -> jiraClient.findType(identifier)).count());
    }

}