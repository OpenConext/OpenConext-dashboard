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
package selfservice.selenium;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;

import selfservice.selenium.page.Pages;

public class SpLmngListControllerTestSelenium extends SeleniumSupport {

  @Test
  public void getLmngIdForSpPageSuccess() {
    Pages.create(getCsaBaseUrl(), driver).loginPage().loginAsAdmin();

    String screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BASE64);

    System.out.println("data:image/png;base64," + screenshot);

    List<WebElement> element = driver.findElements(By.id("form-lmng-id-edit-3"));

    assertThat(element, hasSize(1));
  }

  @Test
  public void getLmngIdForSpAccessGrantedAdminDist() {
    Pages.create(getCsaBaseUrl(), driver).loginPage().loginAsAdmin();

    WebElement element = driver.findElement(By.id("sp_overview_table"));

    assertNotNull("Expected 'access denied' text", element);
  }

  @Test
  public void getLmngIdForSpChangeValue() {
    String currentLmngValue = "{26FF7404-970C-E211-B6B9-005056950050}";
    String newLmngValue = "{41D136D1-3819-E211-B687-005056950050}";

    Pages.create(getCsaBaseUrl(), driver).loginPage().loginAsAdmin();

    WebElement inputLmng = driver.findElement(By.id("lmngId-0"));

    inputLmng.clear();
    inputLmng.sendKeys(newLmngValue);

    WebElement form = driver.findElement(By.id("form-lmng-id-edit-0"));
    form.findElement(By.name("submitbutton")).click();

    inputLmng = driver.findElement(By.id("lmngId-0"));

    assertEquals("Unexpected new LMNG id", newLmngValue, inputLmng.getAttribute("value"));

    // reset value to initial value
    inputLmng.clear();
    inputLmng.sendKeys(currentLmngValue);
    form = driver.findElement(By.id("form-lmng-id-edit-0"));
    form.findElement(By.name("submitbutton")).click();
  }

  @Test
  public void getLmngIdForSpChangeIllegalValue() {
    String currentLmngValue = "{26FF7404-970C-E211-B6B9-005056950050}";
    String newLmngValue = "illegal string value";

    Pages.create(getCsaBaseUrl(), driver).loginPage().loginAsAdmin();

    WebElement inputLmng = driver.findElement(By.id("lmngId-0"));
    assertEquals("Unexpected LMNG id", currentLmngValue, inputLmng.getAttribute("value"));
    inputLmng.clear();
    inputLmng.sendKeys(newLmngValue);

    WebElement form = driver.findElement(By.id("form-lmng-id-edit-0"));
    form.findElement(By.name("submitbutton")).click();

    WebElement element = driver.findElement(By.xpath("//*[contains(.,'Wrong format for LMNG ID')]"));
    assertNotNull("Expected 'Wrong format for LMNG ID' text", element);
  }

}
