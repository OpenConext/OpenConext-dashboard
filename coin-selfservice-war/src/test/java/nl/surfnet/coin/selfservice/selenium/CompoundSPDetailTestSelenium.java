package nl.surfnet.coin.selfservice.selenium;

import nl.surfnet.coin.selfservice.util.OpenConextOAuthClientMock;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class CompoundSPDetailTestSelenium extends SeleniumSupport {

  private static final String bindingAdminUrl = "shopadmin/all-spslmng.shtml";


  @Test
  public void getLmngIdForIdpPageSuccess() {
    WebDriver driver = getRestartedWebDriver();

    driver.get(getSelfserviceBaseUrl()); // get homepage
    loginAtMujinaAs(OpenConextOAuthClientMock.Users.ALL); // login
    driver.get(getSelfserviceBaseUrl() + bindingAdminUrl); // get lmng sp admin page
    clickOnPartialLink("Configure sources");
    clickOnPartialLink("URL EULA");
    clickOnPartialLink("Distribution Channel");
    driver.findElement(By.tagName("textarea")).clear();
    driver.findElement(By.tagName("textarea")).sendKeys("http://example.org/this-is-an-example-url");
    clickOnButton("Save value");
  }

}
