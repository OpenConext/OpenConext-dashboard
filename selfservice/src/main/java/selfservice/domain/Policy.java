package selfservice.domain;

import com.google.common.base.MoreObjects;

public class Policy {

  private final String name;
  private final String description;

  public Policy(String name, String description) {
    this.name = name;
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String toString() {
    return MoreObjects.toStringHelper(Policy.class)
        .add("name", name)
        .add("description", description).toString();
  };
}
