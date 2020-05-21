package dashboard.domain;

public class UpdateInviteRequest {

    public enum Status {
        ACCEPTED, REJECTED
    }

    private String jiraKey;
    private Status status;
    private String comment;
    private String spEntityId;
    private String typeMetaData;
    private boolean connectWithoutInteraction;

    public String getJiraKey() {
        return jiraKey;
    }

    public void setJiraKey(String jiraKey) {
        this.jiraKey = jiraKey;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getSpEntityId() {
        return spEntityId;
    }

    public void setSpEntityId(String spEntityId) {
        this.spEntityId = spEntityId;
    }

    public String getTypeMetaData() {
        return typeMetaData;
    }

    public void setTypeMetaData(String typeMetaData) {
        this.typeMetaData = typeMetaData;
    }

    public boolean isConnectWithoutInteraction() {
        return connectWithoutInteraction;
    }

    public void setConnectWithoutInteraction(boolean connectWithoutInteraction) {
        this.connectWithoutInteraction = connectWithoutInteraction;
    }
}
