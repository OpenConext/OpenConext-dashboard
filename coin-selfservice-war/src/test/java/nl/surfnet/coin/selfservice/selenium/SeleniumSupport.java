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

import java.util.concurrent.TimeUnit;

import nl.surfnet.coin.selfservice.util.OpenConextOAuthClientMock;

import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 */
public class SeleniumSupport {
  
  private final static Logger LOG = LoggerFactory.getLogger(SeleniumSupport.class);

  private static WebDriver driver;

  protected String getSelfserviceBaseUrl() {
    return System.getProperty("selenium.test.url", "http://localhost:8280/selfservice/");
  }

  @Before
  public void initializeOnce() {
    if (driver == null) {
      if ("firefox".equals(System.getProperty("selenium.webdriver", "firefox"))) {
        initFirefoxDriver();
      } else {
        initHtmlUnitDriver();
      }
    }
  }

  private void initHtmlUnitDriver() {
    SeleniumSupport.driver = new HtmlUnitDriver();
    SeleniumSupport.driver.manage().timeouts()
        .implicitlyWait(3, TimeUnit.SECONDS);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        if (driver != null) {
          driver.quit();
        }
      }
    });
  }

  private void initFirefoxDriver() {
    SeleniumSupport.driver = new FirefoxDriver();


    SeleniumSupport.driver.manage().timeouts()
        .implicitlyWait(3, TimeUnit.SECONDS);
    Runtime.getRuntime().addShutdownHook(new Thread() {
      @Override
      public void run() {
        if (driver != null) {
          driver.quit();
        }
      }
    });
  }

  /**
   * @return the webDriver
   */
  protected WebDriver getWebDriver() {
    return driver;
  }

  protected WebDriver getRestartedWebDriver() {
    driver.quit();
    driver = null;
    initializeOnce();
    return driver;
  }

  public void clickOnPartialLink(String linkText) {
    driver.findElement(By.partialLinkText(linkText)).click();
  }
  public void clickOnButton(String textOnButton) {
    String xpathExpression = String.format("//button[contains(text(),\"%s\")]", textOnButton);
    driver.findElement(By.xpath(xpathExpression)).click();
  }


  public void loginAtMujinaAs(OpenConextOAuthClientMock.Users user) {
    getWebDriver().findElement(By.name("j_username")).sendKeys(user.getUser());
    getWebDriver().findElement(By.name("j_password")).sendKeys("secret");
    getWebDriver().findElement(By.name("login")).submit();
  }
}
