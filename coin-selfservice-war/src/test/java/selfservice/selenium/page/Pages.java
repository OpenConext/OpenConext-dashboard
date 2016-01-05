package selfservice.selenium.page;

import org.openqa.selenium.WebDriver;

public class Pages {

  private final String baseUrl;
  private final WebDriver driver;

  private Pages(String baseUrl, WebDriver driver) {
    this.baseUrl = baseUrl;
    this.driver = driver;
  }

  public static Pages create(String baseUrl, WebDriver driver) {
    return new Pages(baseUrl, driver);
  }

  public LoginPage loginPage() {
    driver.get(baseUrl);
    return new LoginPage(driver);
  }

  public void allIdpsPage() {
    driver.get(baseUrl + "shopadmin/all-idpslmng.shtml");
  }

}
