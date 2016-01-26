package selfservice.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.base.MoreObjects;

public class Policy {

  private Long id;

  private String name;
  private String description;
  private String serviceProviderId;
  private String serviceProviderName;

  private List<String> identityProviderIds = new ArrayList<>();
  private List<String> identityProviderNames = new ArrayList<>();

  private List<Attribute> attributes = new ArrayList<>();

  private String denyAdvice;
  private String denyAdviceNl;

  private boolean denyRule;
  private boolean allAttributesMustMatch;

  private String userDisplayName;
  private String authenticatingAuthorityName;

  private int numberOfRevisions;
  private int revisionNbr;

  private Date created;

  private boolean isActivatedSr;
  private boolean active;
  private boolean actionsAllowed;

  public Policy() {
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

  public int getNumberOfRevisions() {
    return numberOfRevisions;
  }

  public List<String> getIdentityProviderNames() {
    return identityProviderNames;
  }

  public boolean isActionsAllowed() {
    return actionsAllowed;
  }

  public String toString() {
    return MoreObjects.toStringHelper(Policy.class)
        .add("name", name)
        .add("description", description)
        .add("serviceProviderName", serviceProviderName).toString();
  };

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
  }
}
