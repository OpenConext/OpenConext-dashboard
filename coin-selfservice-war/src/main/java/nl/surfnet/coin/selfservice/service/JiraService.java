package nl.surfnet.coin.selfservice.service;

import java.io.IOException;
import java.util.List;

import nl.surfnet.coin.selfservice.domain.JiraTask;

public interface JiraService {

    /**
     * Create a new task in Jira.
     *
     * @param task the task you want to create
     * @return the new task key
     * @throws IOException when communicating with jira fails
     */
    String create(final JiraTask task) throws IOException;

    /**
     * Delete a task from Jira.
     *
     * @param key the task key
     * @throws IOException when communicating with jira fails
     */
    void delete(String key) throws IOException;

    /**
     * Re-open or close a Jira task.
     *
     * @param key the task key
     * @param action what action to undertake
     * @throws IOException when communicating with jira fails
     */
    void doAction(String key, JiraTask.Action action) throws IOException;

    /**
     * Retrieve specific tasks from Jira.
     *
     * @param keys a list of the task keys you want to retrieve
     * @return a list of tasks
     * @throws IOException when communicating with jira fails
     */
    List<JiraTask> getTasks(final List<String> keys) throws IOException;
}
