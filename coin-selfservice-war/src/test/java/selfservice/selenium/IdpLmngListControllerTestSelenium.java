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

import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.openqa.selenium.By.xpath;

import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import selfservice.selenium.page.Pages;

public class IdpLmngListControllerTestSelenium extends SeleniumSupport {

  @Test
  public void getLmngIdForIdpAccessGrantedAdminDist() {
    Pages pages = Pages.create(getCsaBaseUrl(), driver);
    pages.loginPage().loginAsAdmin();
    pages.allIdpsPage();

    assertNotNull(driver.findElement(By.id("idp_overview_table")));
  }

  @Test
  public void getLmngIdForIdpChangeValue() {
    String currentLmngValue = "{ED3207DC-1910-DC11-A6C7-0019B9DE3AA4}";
    String newLmngValue = "{AF1F54D8-1B10-DC11-A6C7-0019B9DE3AA4}";

    Pages pages = Pages.create(getCsaBaseUrl(), driver);
    pages.loginPage().loginAsAdmin();
    pages.allIdpsPage();

    WebElement form = driver.findElement(xpath("//form[@class='lmng-id-edit'][1]"));

    WebElement inputLmng = form.findElement(xpath("//input[@name='lmngIdentifier']"));

    inputLmng.clear();
    inputLmng.sendKeys(newLmngValue);

    /*
     * Alas we had some problems (sometimes) with this testcase, sometime the AJAX call was
     * not received and the old value was kept in the field. After multiple tries I've added
     * two sleeps and be done with it .... GRRR
     */
    sleepUninterruptibly(800, TimeUnit.MILLISECONDS);

    form.findElement(By.name("submitbutton")).click();

    sleepUninterruptibly(800, TimeUnit.MILLISECONDS);

    form = driver.findElement(xpath("//form[@class='lmng-id-edit'][1]"));
    inputLmng = form.findElement(xpath("//input[@name='lmngIdentifier']"));
    assertEquals("Unexpected new LMNG id", newLmngValue, inputLmng.getAttribute("value"));

    // reset value to initial value
    inputLmng.clear();
    inputLmng.sendKeys(currentLmngValue);
    form = driver.findElement(xpath("//form[@class='lmng-id-edit'][1]"));
    form.findElement(By.name("submitbutton")).click();
  }

  @Test
  public void getLmngIdForIdpChangeIllegalValue() {
    String currentLmngValue = "{ED3207DC-1910-DC11-A6C7-0019B9DE3AA4}";
    String newLmngValue = "illegal string value";

    Pages pages = Pages.create(getCsaBaseUrl(), driver);
    pages.loginPage().loginAsAdmin();
    pages.allIdpsPage();

    WebElement form = driver.findElement(xpath("//form[@class='lmng-id-edit'][1]"));
    WebElement inputLmng = form.findElement(xpath("//input[@name='lmngIdentifier']"));

    assertEquals("Unexpected LMNG id", currentLmngValue, inputLmng.getAttribute("value"));

    inputLmng.clear();
    inputLmng.sendKeys(newLmngValue);

    form.findElement(By.name("submitbutton")).click();

    assertNotNull(driver.findElement(xpath("//*[contains(.,'Wrong format for LMNG ID')]")));
  }

}
