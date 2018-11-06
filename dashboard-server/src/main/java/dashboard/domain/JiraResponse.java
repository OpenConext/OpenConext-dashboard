package dashboard.domain;

import java.util.List;

public class JiraResponse {

    private List<Action> issues;
    private int total = 0;
    private int startAt;
    private int maxResults;

    public JiraResponse(List<Action> issues, int total, int startAt, int maxResults) {
        this.issues = issues;
        this.total = total;
        this.startAt = startAt;
        this.maxResults = maxResults;
    }

    public List<Action> getIssues() {
        return issues;
    }

    public void setIssues(List<Action> issues) {
        this.issues = issues;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getStartAt() {
        return startAt;
    }

    public void setStartAt(int startAt) {
        this.startAt = startAt;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
}
