package dashboard.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.base.MoreObjects;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Policy {

    private Long id;

    private String name;
    private String description;
    private String serviceProviderId;
    private String serviceProviderName;
    private String serviceProviderNameNl;
    private String serviceProviderNamePt;

    private List<String> identityProviderIds = new ArrayList<>();
    private List<String> identityProviderNames = new ArrayList<>();
    private List<String> identityProviderNamesNl = new ArrayList<>();
    private List<String> identityProviderNamesPt = new ArrayList<>();

    private List<Attribute> attributes = new ArrayList<>();

    private String denyAdvice;
    private String denyAdviceNl;
    private String denyAdvicePt;

    private boolean denyRule;
    private boolean allAttributesMustMatch;

    private String userDisplayName;
    private String authenticatingAuthorityName;

    private int numberOfRevisions;
    private int revisionNbr;

    private String created;

    private boolean isActivatedSr;
    private boolean active = true;
    private boolean actionsAllowed;
    private String type = "reg";

    public Policy() {
    }

    private Policy(PolicyBuilder builder) {
        this.id = builder.id;
        this.created = builder.created;
        this.userDisplayName = builder.userDisplayName;
        this.actionsAllowed = builder.actionsAllowed;
        this.revisionNbr = builder.revisionNbr;
        this.numberOfRevisions = builder.numberOfRevisions;
        this.serviceProviderName = builder.serviceProviderName;

        this.name = builder.policy.name;
        this.description = builder.policy.description;
        this.serviceProviderId = builder.policy.serviceProviderId;
        this.identityProviderIds = builder.policy.identityProviderIds;
        this.identityProviderNames = builder.policy.identityProviderNames;
        this.attributes = builder.policy.attributes;
        this.denyAdvice = builder.policy.denyAdvice;
        this.denyAdviceNl = builder.policy.denyAdviceNl;
        this.denyAdvicePt = builder.policy.denyAdvicePt;
        this.denyRule = builder.policy.denyRule;
        this.allAttributesMustMatch = builder.policy.allAttributesMustMatch;
        this.authenticatingAuthorityName = builder.policy.authenticatingAuthorityName;
        this.isActivatedSr = builder.policy.isActivatedSr;
        this.active = builder.policy.active;
        this.type = builder.policy.type;
    }

    public Policy(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getServiceProviderName() {
        return serviceProviderName;
    }

    public String getServiceProviderNameNl() {
        return serviceProviderNameNl;
    }

    public String getServiceProviderNamePt() {
        return serviceProviderNamePt;
    }

    public int getNumberOfRevisions() {
        return numberOfRevisions;
    }

    public List<String> getIdentityProviderIds() {
        return identityProviderIds;
    }

    public List<String> getIdentityProviderNames() {
        return identityProviderNames;
    }

    public List<String> getIdentityProviderNamesNl() {
        return identityProviderNamesNl;
    }

    public List<String> getIdentityProviderNamesPt() {
        return identityProviderNamesPt;
    }

    public boolean isActionsAllowed() {
        return actionsAllowed;
    }

    public List<Attribute> getAttributes() {
        return attributes;
    }

    public String getCreated() {
        return created;
    }

    public String getDenyAdvice() {
        return denyAdvice;
    }

    public String getDenyAdviceNl() {
        return denyAdviceNl;
    }

    public String getDenyAdvicePt() {
        return denyAdvicePt;
    }

    public boolean isDenyRule() {
        return denyRule;
    }

    public boolean isAllAttributesMustMatch() {
        return allAttributesMustMatch;
    }

    public String getServiceProviderId() {
        return serviceProviderId;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public int getRevisionNbr() {
        return revisionNbr;
    }

    public boolean isActivatedSr() {
        return isActivatedSr;
    }

    public boolean isActive() {
        return active;
    }

    public String getAuthenticatingAuthorityName() {
        return authenticatingAuthorityName;
    }

    public String getType() {
        return type;
    }

    public String toString() {
        return MoreObjects.toStringHelper(Policy.class)
                .add("name", name)
                .add("serviceProviderId", serviceProviderId)
                .add("identityProviderIds", identityProviderIds)
                .add("denyRule", denyRule)
                .add("denyAdvice", denyAdvice)
                .add("denyAdviceNl", denyAdviceNl)
                .add("denyAdvicePt", denyAdvicePt)
                .add("attributes", attributes)
                .add("created", created)
                .add("description", description)
                .add("type", type)
                .add("serviceProviderName", serviceProviderName).toString();
    }

    ;

    public static class Attribute {
        private String name;
        private String value;

        public Attribute() {
        }

        public Attribute(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(Attribute.class)
                    .add("name", name)
                    .add("value", value).toString();
        }
    }

    public static class PolicyBuilder {
        private final Policy policy;
        private Long id;
        private String created;
        private String userDisplayName;
        private boolean actionsAllowed;
        private int revisionNbr = 0;
        private int numberOfRevisions = 0;
        private String serviceProviderName;

        private PolicyBuilder(Policy policy) {
            this.policy = policy;
        }

        public static PolicyBuilder of(Policy policy) {
            return new PolicyBuilder(policy);
        }

        public PolicyBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public PolicyBuilder withCreated(String created) {
            this.created = created;
            return this;
        }

        public PolicyBuilder withUserDisplayName(String userDisplayName) {
            this.userDisplayName = userDisplayName;
            return this;
        }

        public PolicyBuilder withActionsAllowed(boolean actionsAllowed) {
            this.actionsAllowed = actionsAllowed;
            return this;
        }

        public PolicyBuilder withRevisionNbr(int revisionNbr) {
            this.revisionNbr = revisionNbr;
            return this;
        }

        public PolicyBuilder withNumberOfRevisions(int numberOfRevisions) {
            this.numberOfRevisions = numberOfRevisions;
            return this;
        }

        public PolicyBuilder withServiceProviderName(String serviceProviderName) {
            this.serviceProviderName = serviceProviderName;
            return this;
        }

        public Policy build() {
            return new Policy(this);
        }
    }

}
