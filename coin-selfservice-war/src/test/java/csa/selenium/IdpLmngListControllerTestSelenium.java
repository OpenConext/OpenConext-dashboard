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

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.openqa.selenium.By.xpath;

public class IdpLmngListControllerTestSelenium extends SeleniumSupport {

  private static final String bindingAdminUrl = "shopadmin/all-idpslmng.shtml";

  @Test
  public void getLmngIdForIdpPageSuccess() {
    WebDriver driver = getWebDriver();

    driver.get(getCsaBaseUrl()); // get homepage
    loginAtMockAsAdmin(); // login
    driver.get(getCsaBaseUrl() + bindingAdminUrl); // get lmng sp admin page
    WebElement element = driver.findElement(By.id("form-1"));
    Assert.assertNotNull("Element form-1 should exist (expected 2 visable or invisable rows/forms)", element);
  }

  @Test
  public void getLmngIdForIdpAccessGrantedAdminDist() {
    WebDriver driver = getWebDriver();

    driver.get(getCsaBaseUrl()); // get homepage
    loginAtMockAsAdmin(); // login as normal user
    driver.get(getCsaBaseUrl() + bindingAdminUrl); // get lmng sp admin page

    WebElement element = driver.findElement(By.id("idp_overview_table"));
    Assert.assertNotNull("Expected idp_table", element);
  }

  @Test
  public void getLmngIdForIdpChangeValue() {
    WebDriver driver = getWebDriver();
    String currentLmngValue = "{ED3207DC-1910-DC11-A6C7-0019B9DE3AA4}";
    String newLmngValue = "{AF1F54D8-1B10-DC11-A6C7-0019B9DE3AA4}";

    driver.get(getCsaBaseUrl()); // get homepage
    loginAtMockAsAdmin(); // login as normal user
    driver.get(getCsaBaseUrl() + bindingAdminUrl); // get lmng sp admin page

    WebElement form = driver.findElement(xpath("//form[@class='lmng-id-edit'][1]"));


    WebElement inputLmng = form.findElement(xpath("//input[@name='lmngIdentifier']"));

    inputLmng.clear();
    inputLmng.sendKeys(newLmngValue);
    
    /*
     * Alas we had some problems (sometimes) with this testcase, sometime the AJAX call was
     * not received and the old value was kept in the field. After multiple tries I've added
     * two sleeps and be done with it .... GRRR
     */
    try {
      Thread.sleep(800);
    } catch (InterruptedException e) {
      //ignored
    }

    form.findElement(By.name("submitbutton")).click();

    try {
      Thread.sleep(800);
    } catch (InterruptedException e) {
      //ignored
    }

    form = driver.findElement(xpath("//form[@class='lmng-id-edit'][1]"));
    inputLmng = form.findElement(xpath("//input[@name='lmngIdentifier']"));
    Assert.assertEquals("Unexpected new LMNG id", newLmngValue, inputLmng.getAttribute("value"));

    // reset value to initial value
    inputLmng.clear();
    inputLmng.sendKeys(currentLmngValue);
    form = driver.findElement(xpath("//form[@class='lmng-id-edit'][1]"));
    form.findElement(By.name("submitbutton")).click();
    // page gets refreshed..

  }

  @Test
  public void getLmngIdForIdpChangeIllegalValue() {
    WebDriver driver = getWebDriver();
    String currentLmngValue = "{ED3207DC-1910-DC11-A6C7-0019B9DE3AA4}";
    String newLmngValue = "illegal string value";

    driver.get(getCsaBaseUrl()); // get homepage
    loginAtMockAsAdmin(); // login as normal user
    driver.get(getCsaBaseUrl() + bindingAdminUrl); // get lmng sp admin page

    WebElement form = driver.findElement(xpath("//form[@class='lmng-id-edit'][1]"));
    WebElement inputLmng = form.findElement(xpath("//input[@name='lmngIdentifier']"));
    Assert.assertEquals("Unexpected LMNG id", currentLmngValue, inputLmng.getAttribute("value"));
    inputLmng.clear();
    inputLmng.sendKeys(newLmngValue);


    form.findElement(By.name("submitbutton")).click();

    //Wrong format for LMNG ID
    WebElement element = driver.findElement(xpath("//*[contains(.,'Wrong format for LMNG ID')]"));
    Assert.assertNotNull("Expected 'Wrong format for LMNG ID' text", element);

  }

}
