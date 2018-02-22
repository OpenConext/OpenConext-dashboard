package selfservice.domain;

public class Change {

  private String entityId;
  private String attribute;
  private String oldValue;
  private String newValue;

  public Change(String entityId, String attribute, String oldValue, String newValue) {
    this.entityId = entityId;
    this.attribute = attribute;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  @Override
  public String toString() {
    return "Change the attribute '" + attribute + "' for '" + entityId + "'from old value '" + oldValue
      + "' to new value '" + newValue + "'";
  }
}
