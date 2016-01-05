package selfservice.selenium;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import selfservice.selenium.page.Pages;

public class CompoundSPDetailTestSelenium extends SeleniumSupport {

  @Test
  public void getLmngIdForIdpPageSuccess() throws InterruptedException {
    Pages.create(getCsaBaseUrl(), driver).loginPage().loginAsAdmin();

    clickOnPartialLink("Configure sources");
    clickOnPartialLink("URL of the app");
    clickOnPartialLink("CSA");

    List<WebElement> elements = driver.findElements(By.tagName("textarea"));
    for (WebElement element : elements) {
      if (element.isDisplayed()) {
        element.clear();
        element.sendKeys("http://example.org/this-is-an-example-url");
        clickOnButton("Save value");
      }
    }
  }

}
