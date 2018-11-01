package dashboard.domain;

import java.io.Serializable;

import com.google.common.base.MoreObjects;

@SuppressWarnings("serial")
public class Group implements Serializable {

  private final String id;

  public Group(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(Group.class).add("id", id).toString();
  }

}
