package nl.surfnet.coin.selfservice.selenium;
import static junit.framework.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.WebDriver;

public class ApiTestSelenium extends SeleniumSupport {

  private static final String apiUrl = "api/public/services.json";


  @Test
  public void getServices() {
    WebDriver driver = getRestartedWebDriver();

    driver.get(getSelfserviceBaseUrl() + apiUrl); // get services json
    String jsonResponse = driver.getPageSource();
    assertTrue(jsonResponse.contains("8833CEAE-960C-E211-B6B9-005056950050"));
  }

}
