package nl.surfnet.coin.selfservice.domain;

import java.io.Serializable;

public class Group implements Serializable {

  private final String id;

  public Group(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }


}
