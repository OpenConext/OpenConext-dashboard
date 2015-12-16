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

import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import selfservice.Application;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest
@DirtiesContext
@ActiveProfiles("dev")
public class SeleniumSupport {

  protected WebDriver driver;

  @Value("${local.server.port}")
  private int port;

  protected String getCsaBaseUrl() {
    return "http://localhost:" + port + "/";
  }

  @Before
  public void initializeDriver() {
    if (driver != null) {
      driver.quit();
    }

    driver = new PhantomJSDriver();
    driver.manage().timeouts().implicitlyWait(3, TimeUnit.SECONDS);
  }

  protected WebDriver getWebDriver() {
    return driver;
  }

  public void clickOnPartialLink(String linkText) {
    driver.findElement(By.partialLinkText(linkText)).click();
  }

  public void clickOnButton(String textOnButton) {
    String xpathExpression = String.format("//button[contains(text(),\"%s\")]", textOnButton);
    driver.findElement(By.xpath(xpathExpression)).click();
  }


  public void loginAtMockAsAdmin() {
    getWebDriver().findElement(By.name("login")).submit();
  }


}
