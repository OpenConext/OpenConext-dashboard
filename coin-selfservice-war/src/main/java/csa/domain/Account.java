package csa.domain;

public class Account {

  private String name;
  private String status;
  private String guid;

  public Account() {
  }

  public Account(String name, String status, String guid) {
    this.name = name;
    this.status = status;
    this.guid = guid;
  }

  public String getName() {
    return name;
  }

  public String getStatus() {
    return status;
  }

  public String getGuid() {
    return guid;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public void setGuid(String guid) {
    this.guid = guid;
  }
}
