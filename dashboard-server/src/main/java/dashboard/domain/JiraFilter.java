package dashboard.domain;


import java.time.Instant;
import java.util.List;

public class JiraFilter {

    private int maxResults = 20;
    private int startAt = 0;
    private Instant from;
    private Instant to;
    private String spEntityId;
    private List<String> statuses;

    public Instant getFrom() {
        return from;
    }

    public void setFrom(Instant from) {
        this.from = from;
    }

    public Instant getTo() {
        return to;
    }

    public void setTo(Instant to) {
        this.to = to;
    }

    public String getSpEntityId() {
        return spEntityId;
    }

    public void setSpEntityId(String spEntityId) {
        this.spEntityId = spEntityId;
    }

    public List<String> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<String> statuses) {
        this.statuses = statuses;
    }
}
