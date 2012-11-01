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

import junit.framework.Assert;
import nl.surfnet.coin.selfservice.util.OpenConextOAuthClientMock;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class IdpLmngListControllerTestSelenium extends SeleniumSupport {

  private static final String bindingAdminUrl = "shopadmin/all-idpslmng.shtml";
 
  @Test
  public void getLmngIdForSpPageSuccess() {
    WebDriver driver = getRestartedWebDriver();
    
    driver.get(getSelfserviceBaseUrl()); // get homepage
    loginAtMujinaAs(OpenConextOAuthClientMock.Users.ALL); // login
    driver.get(getSelfserviceBaseUrl()+bindingAdminUrl); // get lmng sp admin page
    WebElement element = driver.findElement(By.id("form-14"));
    Assert.assertNotNull("Element form-14 should exist (expected 15 visable or invisable rows/forms)", element);
  }
  
  @Test
  public void getLmngIdForSpAccessDeniedUser() {
    WebDriver driver = getRestartedWebDriver();

    driver.get(getSelfserviceBaseUrl()); // get homepage
    loginAtMujinaAs(OpenConextOAuthClientMock.Users.USER); // login as normal user
    driver.get(getSelfserviceBaseUrl()+bindingAdminUrl); // get lmng sp admin page
    
    WebElement element = driver.findElement(By.xpath("//*[contains(.,'Access denied')]")); 
    Assert.assertNotNull("Expected 'access denied' text", element);
  }
  
  @Test
  public void getLmngIdForSpAccessDeniedAdminIdpLicense() {
    WebDriver driver = getRestartedWebDriver();

    driver.get(getSelfserviceBaseUrl()); // get homepage
    loginAtMujinaAs(OpenConextOAuthClientMock.Users.ADMIN_IDP_LICENSE); // login as normal user
    driver.get(getSelfserviceBaseUrl()+bindingAdminUrl); // get lmng sp admin page
    
    WebElement element = driver.findElement(By.xpath("//*[contains(.,'Access denied')]")); 
    Assert.assertNotNull("Expected 'access denied' text", element);
  }
  
  @Test
  public void getLmngIdForSpAccessDeniedAdminIdpSurfconext() {
    WebDriver driver = getRestartedWebDriver();

    driver.get(getSelfserviceBaseUrl()); // get homepage
    loginAtMujinaAs(OpenConextOAuthClientMock.Users.ADMIN_IDP_SURFCONEXT); // login as normal user
    driver.get(getSelfserviceBaseUrl()+bindingAdminUrl); // get lmng sp admin page
    
    WebElement element = driver.findElement(By.xpath("//*[contains(.,'Access denied')]")); 
    Assert.assertNotNull("Expected 'access denied' text", element);
  }
  
  @Test
  public void getLmngIdForSpAccessGrantedAdminDist() {
    WebDriver driver = getRestartedWebDriver();

    driver.get(getSelfserviceBaseUrl()); // get homepage
    loginAtMujinaAs(OpenConextOAuthClientMock.Users.ADMIN_DISTRIBUTIE_CHANNEL); // login as normal user
    driver.get(getSelfserviceBaseUrl()+bindingAdminUrl); // get lmng sp admin page
    
    WebElement element = driver.findElement(By.id("idp_overview_table")); 
    Assert.assertNotNull("Expected 'access denied' text", element);
  }
  
  @Test
  public void getLmngIdForSpChangeValue() {
    WebDriver driver = getRestartedWebDriver();
    String currentLmngValue = "{ED3207DC-1910-DC11-A6C7-0019B9DE3AA4}";
    String newLmngValue = "{AF1F54D8-1B10-DC11-A6C7-0019B9DE3AA4}";

    driver.get(getSelfserviceBaseUrl()); // get homepage
    loginAtMujinaAs(OpenConextOAuthClientMock.Users.ALL); // login as normal user
    driver.get(getSelfserviceBaseUrl()+bindingAdminUrl); // get lmng sp admin page

    WebElement inputSp = driver.findElement(By.id("idpId-1"));
    Assert.assertEquals("Unexpected IDP id", "mock-institution-id", inputSp.getAttribute("value"));
    
    WebElement inputLmng = driver.findElement(By.id("lmngId-1"));
    Assert.assertEquals("Unexpected LMNG id", currentLmngValue, inputLmng.getAttribute("value"));
    inputLmng.clear();
    inputLmng.sendKeys(newLmngValue);
    
    WebElement form = driver.findElement(By.id("form-1"));
    form.findElement(By.name("submitbutton")).click();

    inputLmng = driver.findElement(By.id("lmngId-1"));
    Assert.assertEquals("Unexpected new LMNG id", newLmngValue, inputLmng.getAttribute("value"));

    // reset value to initial value
    inputLmng.clear();
    inputLmng.sendKeys(currentLmngValue);
    form = driver.findElement(By.id("form-1"));
    form.findElement(By.name("submitbutton")).click();
    
  }
 
}
