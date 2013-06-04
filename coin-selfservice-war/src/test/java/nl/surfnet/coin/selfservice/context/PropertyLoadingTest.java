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

  @Value("${feature.development.mode}")
  private boolean developmentMode;

  @Value("${feature.showOauthTokens}")
  private boolean showOauthTokens;

  /**
   * This tests whether properties from the default props file are overridden correctly by an external properties file.
   * In this case, the 'local development' properties file, from src/test/resources.
   *
   */
  @Test
  public void overrideProperties() {
    assertEquals("false by default, but overridden to true in local development", true, developmentMode);
    assertEquals("true by default, not defined in local development", true, showOauthTokens);
  }

}
