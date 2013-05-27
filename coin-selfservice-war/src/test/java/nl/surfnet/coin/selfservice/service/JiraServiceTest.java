package nl.surfnet.coin.selfservice.service;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.JiraTask;

import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/coin-selfservice-properties-context.xml",
        "/coin-selfservice-context.xml",
        "classpath:coin-shared-context.xml"})
public class JiraServiceTest {

    @Autowired
    private JiraService jiraService;

    @Test
    public void testCreateTask() throws IOException {
        JiraTask task = new JiraTask.Builder()
                .serviceProvider("https://mock-sp")
                .identityProvider("https://mock-idp")
                .institution("Mujina")
                .issueType(JiraTask.Type.LINKREQUEST)
                .body("thebody")
                .build();
        assertThat(jiraService, NotNull.NOT_NULL);
        String key = jiraService.create(task, new CoinUser());
        assertThat(key, NotNull.NOT_NULL);
        assertThat(key.length(), IsNot.not(0));

        final List<JiraTask> tasks = jiraService.getTasks(Collections.singletonList(key));
        assertThat(tasks, NotNull.NOT_NULL);
        assertThat(tasks.size(), IsEqual.equalTo(1));
        final JiraTask jiraTask = tasks.get(0);
        assertThat(jiraTask, NotNull.NOT_NULL);
        assertThat(jiraTask.getStatus(), IsEqual.equalTo(JiraTask.Status.OPEN));
        assertThat(jiraTask.getIdentityProvider(), IsEqual.equalTo("https://mock-idp"));
        assertThat(jiraTask.getServiceProvider(), IsEqual.equalTo("https://mock-sp"));
        assertThat(jiraTask.getBody(), is("thebody"));

        jiraService.delete(key);
    }

    @Test
    public void testCompleteTask() throws IOException {
        JiraTask task = new JiraTask.Builder()
                .serviceProvider("https://mock-sp")
                .identityProvider("https://mock-idp")
                .institution("Mujina")
                .issueType(JiraTask.Type.LINKREQUEST)
                .build();
        assertThat(jiraService, NotNull.NOT_NULL);
        String key = jiraService.create(task, new CoinUser());
        assertThat(key, NotNull.NOT_NULL);
        assertThat(key.length(), IsNot.not(0));

        jiraService.doAction(key, JiraTask.Action.CLOSE);

        final List<JiraTask> tasks = jiraService.getTasks(Collections.singletonList(key));
        assertThat(tasks, NotNull.NOT_NULL);
        assertThat(tasks.size(), IsEqual.equalTo(1));
        final JiraTask jiraTask = tasks.get(0);
        assertThat(jiraTask, NotNull.NOT_NULL);
        assertThat(jiraTask.getStatus(), IsEqual.equalTo(JiraTask.Status.CLOSED));

        jiraService.delete(key);
    }
}
