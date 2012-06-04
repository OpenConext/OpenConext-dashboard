package nl.surfnet.coin.selfservice.domain;

import java.util.Date;

public class Action {

    public enum Type {
        QUESTION, REQUEST
    }

    public enum Status {
        OPEN, CLOSED
    }

    private String jiraKey;
    private String userId;
    private String userName;
    private String body;
    private String idp;
    private String sp;
    private Date requestDate;
    private Type type;
    private Status status;

    public Action(String jiraKey, String userId, String userName, Type type, Status status, String body, String idp, String sp, Date requestDate) {
        this.userId = userId;
        this.jiraKey = jiraKey;
        this.userName = userName;
        this.body = body;
        this.idp = idp;
        this.sp = sp;
        this.requestDate = requestDate;
        this.type = type;
        this.status = status;
    }

    public String getJiraKey() {
        return jiraKey;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserId() {
        return userId;
    }

    public String getBody() {
        return body;
    }

    public String getIdp() {
        return idp;
    }

    public String getSp() {
        return sp;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public Type getType() {
        return type;
    }

    public Status getStatus() {
        return status;
    }
}
