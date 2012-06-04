package nl.surfnet.coin.selfservice.domain;

public class JiraTask {

    public enum Action {
        REOPEN, CLOSE
    }

    public enum Status {
        OPEN, CLOSED
    }

    public enum Type {
        REQUEST, QUESTION
    }

    private String serviceProvider;
    private String identityProvider;
    private String institution;
    private String body;
    private Status status;
    private Type issueType;

    private JiraTask() {}

    public static class Builder {

        private String serviceProvider = "";
        private String identityProvider = "";
        private String institution = "";
        private Status status = Status.OPEN;
        private Type issueType = Type.QUESTION;
        private String body = "";

        public Builder serviceProvider(final String name) {
            this.serviceProvider = name;
            return this;
        }

        public Builder identityProvider(final String name) {
            this.identityProvider = name;
            return this;
        }

        public Builder institution(final String name) {
            this.institution = name;
            return this;
        }

        public Builder status(final Status status) {
            this.status = status;
            return this;
        }

        public Builder issueType(final Type issueType) {
            this.issueType = issueType;
            return this;
        }

        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        public JiraTask build() {
            JiraTask task = new JiraTask();
            task.serviceProvider = serviceProvider;
            task.identityProvider = identityProvider;
            task.institution = institution;
            task.status = status;
            task.issueType = issueType;
            task.body = body;
            return task;
        }
    }

    public String getServiceProvider() {
        return serviceProvider;
    }

    public String getIdentityProvider() {
        return identityProvider;
    }

    public String getInstitution() {
        return institution;
    }

    public Status getStatus() {
        return status;
    }

    public Type getIssueType() {
        return issueType;
    }

    public String getBody() {
        return body;
    }
}
