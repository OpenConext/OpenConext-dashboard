package csa.domain;

public class Group {

  private final String id;
  private final String name;

  public Group(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }
}
