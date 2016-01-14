package selfservice.domain.csa;

import com.google.common.base.MoreObjects;

import org.springframework.util.Assert;

public class MappingEntry {

  private String key;
  private String value;

  public MappingEntry(String key, String value) {
    Assert.notNull(key);
    Assert.notNull(value);
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o == null || !(o instanceof MappingEntry)) {
      return false;
    }

    MappingEntry that = (MappingEntry) o;
    return key.equals(that.key) && value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return key.hashCode() ^ value.hashCode();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(MappingEntry.class)
        .add("key", key)
        .add("value", value).toString();
  }
}
