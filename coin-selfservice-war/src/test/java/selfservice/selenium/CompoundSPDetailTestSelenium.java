package selfservice.selenium;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CompoundSPDetailTestSelenium extends SeleniumSupport {

  private static final String bindingAdminUrl = "shopadmin/all-spslmng.shtml";

  @Test
  public void getLmngIdForIdpPageSuccess() {
    driver.get(getCsaBaseUrl());
    loginAtMockAsAdmin();
    driver.get(getCsaBaseUrl() + bindingAdminUrl);

    clickOnPartialLink("Configure sources");
    clickOnPartialLink("URL of the app");
    clickOnPartialLink("CSA");

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
