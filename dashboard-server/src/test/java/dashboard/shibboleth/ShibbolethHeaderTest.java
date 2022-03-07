package dashboard.shibboleth;

import org.junit.Test;

import java.util.Arrays;

import static dashboard.shibboleth.ShibbolethHeader.Name_Id;
import static dashboard.shibboleth.ShibbolethHeader.findByValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.MatcherAssert.*;

public class ShibbolethHeaderTest {

    @Test
    public void findShibbolethHeaderBasedOnItsValue() {
        Arrays.stream(ShibbolethHeader.values()).forEach(header ->
                assertThat(findByValue(header.getValue()), is(header))
        );
    }

    @Test(expected = IllegalArgumentException.class)
    public void findShibbolethHeaderBasedOnNonExistingValue() {
        assertThat(ShibbolethHeader.findByValue("fake-header"), is(Name_Id));
    }
}
