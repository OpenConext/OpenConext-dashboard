package selfservice.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class LicenseContactPersonTest {

  @Test
  public void is_reachable() {
    LicenseContactPerson person = new LicenseContactPerson("", "", "", "idp");

    assertFalse(person.isReachable());
  }
}
