package nl.surfnet.sab;

import java.util.Collection;

public class SabPersonsInRole {
  private final Collection<SabPerson> sabPersons;
  private final String role;

  public SabPersonsInRole(Collection<SabPerson> sabPersons, String role) {
    this.sabPersons = sabPersons;
    this.role = role;
  }

  public Collection<SabPerson> getSabPersons() {
    return sabPersons;
  }

  public String getRole() {
    return role;
  }
}
