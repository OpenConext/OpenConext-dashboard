package dashboard.shibboleth;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static dashboard.shibboleth.ShibbolethHeader.Name_Id;
import static dashboard.shibboleth.ShibbolethHeader.findByValue;

import java.util.Arrays;

import org.junit.Test;

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
