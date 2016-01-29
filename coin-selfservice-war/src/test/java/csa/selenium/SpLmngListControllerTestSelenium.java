/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package csa.selenium;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class SpLmngListControllerTestSelenium extends SeleniumSupport {

  private static final String bindingAdminUrl = "shopadmin/all-spslmng.shtml";

  @Test
  public void getLmngIdForSpPageSuccess() {
    WebDriver driver = getWebDriver();

    driver.get(getCsaBaseUrl()); // get homepage
    loginAtMockAsAdmin(); // login
    driver.get(getCsaBaseUrl() + bindingAdminUrl); // get lmng sp admin page
    List<WebElement> element = driver.findElements(By.id("form-3"));
    Assert.assertNotNull("Element form-3 should exist (expected 4 rows/forms)", element.get(0));
  }

  @Test
  public void getLmngIdForSpAccessGrantedAdminDist() {
    WebDriver driver = getWebDriver();

    driver.get(getCsaBaseUrl()); // get homepage
    loginAtMockAsAdmin(); // login as normal user
    driver.get(getCsaBaseUrl() + bindingAdminUrl); // get lmng sp admin page

    WebElement element = driver.findElement(By.id("sp_overview_table"));
    Assert.assertNotNull("Expected 'access denied' text", element);
  }

  @Test
  public void getLmngIdForSpChangeValue() {
    WebDriver driver = getWebDriver();
    String currentLmngValue = "{26FF7404-970C-E211-B6B9-005056950050}";
    String newLmngValue = "{41D136D1-3819-E211-B687-005056950050}";

    driver.get(getCsaBaseUrl()); // get homepage
    loginAtMockAsAdmin(); // login as normal user
    driver.get(getCsaBaseUrl() + bindingAdminUrl); // get lmng sp admin page

    WebElement inputLmng = driver.findElement(By.id("lmngId-0"));

    inputLmng.clear();
    inputLmng.sendKeys(newLmngValue);

    WebElement form = driver.findElement(By.id("form-0"));
    form.findElement(By.name("submitbutton")).click();

    inputLmng = driver.findElement(By.id("lmngId-0"));
    Assert.assertEquals("Unexpected new LMNG id", newLmngValue, inputLmng.getAttribute("value"));

    // reset value to initial value
    inputLmng.clear();
    inputLmng.sendKeys(currentLmngValue);
    form = driver.findElement(By.id("form-0"));
    form.findElement(By.name("submitbutton")).click();

  }

  @Test
  public void getLmngIdForSpChangeIllegalValue() {
    WebDriver driver = getWebDriver();
    String currentLmngValue = "{26FF7404-970C-E211-B6B9-005056950050}";
    String newLmngValue = "illegal string value";

    driver.get(getCsaBaseUrl()); // get homepage
    loginAtMockAsAdmin(); // login as normal user
    driver.get(getCsaBaseUrl() + bindingAdminUrl); // get lmng sp admin page

    WebElement inputLmng = driver.findElement(By.id("lmngId-0"));
    Assert.assertEquals("Unexpected LMNG id", currentLmngValue, inputLmng.getAttribute("value"));
    inputLmng.clear();
    inputLmng.sendKeys(newLmngValue);

    WebElement form = driver.findElement(By.id("form-0"));
    form.findElement(By.name("submitbutton")).click();

    //Wrong format for LMNG ID
    WebElement element = driver.findElement(By.xpath("//*[contains(.,'Wrong format for LMNG ID')]"));
    Assert.assertNotNull("Expected 'Wrong format for LMNG ID' text", element);

  }

}
