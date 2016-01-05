package selfservice.selenium.page;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {

  private final WebDriver driver;

  public LoginPage(WebDriver driver) {
    this.driver = driver;
  }

  public AllSpsPage loginAsAdmin() {
    driver.findElement(By.name("login")).submit();
    return new AllSpsPage();
  }

}
