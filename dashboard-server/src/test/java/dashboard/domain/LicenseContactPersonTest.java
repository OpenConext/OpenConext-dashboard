package dashboard.domain;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class LicenseContactPersonTest {

    @Test
    public void is_reachable() {
        LicenseContactPerson person = new LicenseContactPerson("", "", "", "idp");

        assertFalse(person.isReachable());
    }
}
