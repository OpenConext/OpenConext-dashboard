package selfservice.util;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@SuppressWarnings("serial")
@MappedSuperclass
public abstract class DomainObject implements Serializable {

  @Override
  public int hashCode() {
    return (id == null) ? super.hashCode() : id.hashCode();
  }

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || !getClass().equals(other.getClass())
      || !(other instanceof DomainObject)) {
      return false;
    }
    DomainObject domainObject = (DomainObject) other;
    if (id == null && domainObject.id == null) {
      return super.equals(domainObject);
    }
    if ((id != null && domainObject.id == null)
      || (id == null && domainObject.id != null)) {
      return false;
    }
    return id.equals(domainObject.id);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return getClass() + "(id='" + id + "')";
  }
}
