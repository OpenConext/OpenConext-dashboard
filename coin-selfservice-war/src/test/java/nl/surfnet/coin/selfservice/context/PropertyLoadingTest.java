package nl.surfnet.coin.selfservice.context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:coin-selfservice-properties-context.xml", "classpath:coin-selfservice-context.xml", "classpath:coin-shared-context.xml"})
public class PropertyLoadingTest {

  @Value("${testProp1}")
  private String testProp1;

  @Value("${testProp2}")
  private String testProp2;

  // FIXME these dummy properties should be replaced with something that is actually in application.properties (and overridden in another file. But for now, no real properties are in place.
  @Test
  public void test1() {
    assertEquals("application.properties", testProp1);
    assertEquals("overridden_in_showroom.properties(src-test-resources-showroom.properties)", testProp2);
  }

}
