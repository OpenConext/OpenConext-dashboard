package nl.surfnet.coin.selfservice.selenium;

import java.util.List;

import nl.surfnet.coin.selfservice.util.OpenConextOAuthClientMock;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompoundSPDetailTestSelenium extends SeleniumSupport {

  private static final String bindingAdminUrl = "shopadmin/all-spslmng.shtml";
  private static final Logger LOG = LoggerFactory.getLogger(CompoundSPDetailTestSelenium.class);


  @Test
  public void getLmngIdForIdpPageSuccess() {
    WebDriver driver = getRestartedWebDriver();

    driver.get(getSelfserviceBaseUrl()); // get homepage
    loginAtMujinaAs(OpenConextOAuthClientMock.Users.ALL); // login
    driver.get(getSelfserviceBaseUrl() + bindingAdminUrl); // get lmng sp admin page
    clickOnPartialLink("Configure sources");
    clickOnPartialLink("URL of the app");
    clickOnPartialLink("Distribution Channel");

    List<WebElement> elements = driver.findElements(By.tagName("textarea"));
    for (WebElement element : elements) {
      if (element.isDisplayed()) {
        element.clear();
        element.sendKeys("http://example.org/this-is-an-example-url");
        clickOnButton("Save value");
      } else {
        // not visible...
      }
    }
  }

}
