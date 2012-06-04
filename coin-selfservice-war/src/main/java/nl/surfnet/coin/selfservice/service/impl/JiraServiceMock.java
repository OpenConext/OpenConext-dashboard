package nl.surfnet.coin.selfservice.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.surfnet.coin.selfservice.domain.JiraTask;
import nl.surfnet.coin.selfservice.service.JiraService;

public class JiraServiceMock implements JiraService {

    private Map<String, JiraTask> repository;

    private int counter = 0;

    public JiraServiceMock() {
        repository = new HashMap<String, JiraTask>();
    }

    @Override
    public String create(final JiraTask task) throws IOException {
        String key = generateKey();
        repository.put(key, task);
        return key;
    }

    private String generateKey() {
        return "TASK-" + counter++;
    }

    @Override
    public void delete(final String key) throws IOException {
        repository.remove(key);
    }

    @Override
    public void doAction(final String key, final JiraTask.Action action) throws IOException {
        JiraTask jiraTask = repository.get(key);
        JiraTask newTask;
        switch (action) {
            case CLOSE:
                newTask = new JiraTask.Builder()
                        .identityProvider(jiraTask.getIdentityProvider())
                        .serviceProvider(jiraTask.getServiceProvider())
                        .institution(jiraTask.getInstitution())
                        .issueType(jiraTask.getIssueType())
                        .body(jiraTask.getBody())
                        .status(JiraTask.Status.CLOSED)
                        .build();
                break;
            case REOPEN:
            default:
                newTask = new JiraTask.Builder()
                        .identityProvider(jiraTask.getIdentityProvider())
                        .serviceProvider(jiraTask.getServiceProvider())
                        .institution(jiraTask.getInstitution())
                        .issueType(jiraTask.getIssueType())
                        .body(jiraTask.getBody())
                        .status(JiraTask.Status.OPEN)
                        .build();
                break;
        }
        repository.put(key, newTask);
    }

    @Override
    public List<JiraTask> getTasks(final List<String> keys) throws IOException {
        List<JiraTask> tasks = new ArrayList<JiraTask>();
        for (String key : keys) {
            tasks.add(repository.get(key));
        }
        return tasks;
    }
}
