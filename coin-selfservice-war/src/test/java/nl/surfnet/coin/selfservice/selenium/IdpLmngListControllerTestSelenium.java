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

package nl.surfnet.coin.selfservice.selenium;

import nl.surfnet.coin.selfservice.util.OpenConextOAuthClientMock;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import junit.framework.Assert;
import static org.openqa.selenium.By.xpath;

public class IdpLmngListControllerTestSelenium extends SeleniumSupport {

  private static final String bindingAdminUrl = "shopadmin/all-idpslmng.shtml";
 
  @Test
  public void getLmngIdForIdpPageSuccess() {
    WebDriver driver = getRestartedWebDriver();
    
    driver.get(getSelfserviceBaseUrl()); // get homepage
    loginAtMujinaAs(OpenConextOAuthClientMock.Users.ALL); // login
    driver.get(getSelfserviceBaseUrl()+bindingAdminUrl); // get lmng sp admin page
    WebElement element = driver.findElement(By.id("form-14"));
    Assert.assertNotNull("Element form-14 should exist (expected 15 visable or invisable rows/forms)", element);
  }
  
  @Test
  public void getLmngIdForIdpAccessDeniedUser() {
    WebDriver driver = getRestartedWebDriver();

    driver.get(getSelfserviceBaseUrl()); // get homepage
    loginAtMujinaAs(OpenConextOAuthClientMock.Users.USER); // login as normal user
    driver.get(getSelfserviceBaseUrl()+bindingAdminUrl); // get lmng sp admin page
    
    WebElement element = driver.findElement(xpath("//*[contains(.,'Access denied')]"));
    Assert.assertNotNull("Expected 'access denied' text", element);
  }
  
  @Test
  public void getLmngIdForIdpAccessDeniedAdminIdpLicense() {
    WebDriver driver = getRestartedWebDriver();

    driver.get(getSelfserviceBaseUrl()); // get homepage
    loginAtMujinaAs(OpenConextOAuthClientMock.Users.ADMIN_IDP_LICENSE); // login as normal user
    driver.get(getSelfserviceBaseUrl()+bindingAdminUrl); // get lmng sp admin page
    
    WebElement element = driver.findElement(xpath("//*[contains(.,'Access denied')]"));
    Assert.assertNotNull("Expected 'access denied' text", element);
  }
  
  @Test
  public void getLmngIdForIdpAccessDeniedAdminIdpSurfconext() {
    WebDriver driver = getRestartedWebDriver();

    driver.get(getSelfserviceBaseUrl()); // get homepage
    loginAtMujinaAs(OpenConextOAuthClientMock.Users.ADMIN_IDP_SURFCONEXT); // login as normal user
    driver.get(getSelfserviceBaseUrl()+bindingAdminUrl); // get lmng sp admin page
    
    WebElement element = driver.findElement(xpath("//*[contains(.,'Access denied')]"));
    Assert.assertNotNull("Expected 'access denied' text", element);
  }
  
  @Test
  public void getLmngIdForIdpAccessGrantedAdminDist() {
    WebDriver driver = getRestartedWebDriver();

    driver.get(getSelfserviceBaseUrl()); // get homepage
    loginAtMujinaAs(OpenConextOAuthClientMock.Users.ADMIN_DISTRIBUTIE_CHANNEL); // login as normal user
    driver.get(getSelfserviceBaseUrl()+bindingAdminUrl); // get lmng sp admin page
    
    WebElement element = driver.findElement(By.id("idp_overview_table")); 
    Assert.assertNotNull("Expected idp_table", element);
  }

  @Test
  public void getLmngIdForIdpChangeValue() {
    WebDriver driver = getRestartedWebDriver();
    String currentLmngValue = "{ED3207DC-1910-DC11-A6C7-0019B9DE3AA4}";
    String newLmngValue = "{AF1F54D8-1B10-DC11-A6C7-0019B9DE3AA4}";

    driver.get(getSelfserviceBaseUrl()); // get homepage
    loginAtMujinaAs(OpenConextOAuthClientMock.Users.ALL); // login as normal user
    driver.get(getSelfserviceBaseUrl()+bindingAdminUrl); // get lmng sp admin page

    WebElement form = driver.findElement(xpath("//form[@class='lmng-id-edit'][1]"));

    
    WebElement inputLmng = form.findElement(xpath("//input[@name='lmngIdentifier']"));

    inputLmng.clear();
    inputLmng.sendKeys(newLmngValue);

    form.findElement(By.name("submitbutton")).click();

    // page gets refreshed..

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
    WebDriver driver = getRestartedWebDriver();
    String currentLmngValue = "{ED3207DC-1910-DC11-A6C7-0019B9DE3AA4}";
    String newLmngValue = "illegal string value";

    driver.get(getSelfserviceBaseUrl()); // get homepage
    loginAtMujinaAs(OpenConextOAuthClientMock.Users.ALL); // login as normal user
    driver.get(getSelfserviceBaseUrl()+bindingAdminUrl); // get lmng sp admin page

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
