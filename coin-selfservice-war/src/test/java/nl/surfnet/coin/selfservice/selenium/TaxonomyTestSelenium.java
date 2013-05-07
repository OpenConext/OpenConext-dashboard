/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package nl.surfnet.coin.selfservice.selenium;

import junit.framework.Assert;
import nl.surfnet.coin.selfservice.util.OpenConextOAuthClientMock;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

public class TaxonomyTestSelenium extends SeleniumSupport {

  @Test
  public void createFacetWithValue() throws InterruptedException {
    WebDriver driver = getWebDriver();

    driver.get(getSelfserviceBaseUrl()); // get homepage
    loginAtMujinaAs(OpenConextOAuthClientMock.Users.ADMIN_DISTRIBUTIE_CHANNEL); // login
    driver.get(getSelfserviceBaseUrl() + "shopadmin/taxonomy-overview.shtml");
    driver.findElement(By.id("add_facet")).click();

    WebElement newFacet = driver.findElement(xpath("//input[@class='inline-edit']"));

    newFacet.sendKeys("NewFacet");
    newFacet.sendKeys(Keys.TAB);

    driver.findElement(xpath("//a[@class='accordion-toggle with-options']")).click();

    driver.findElement(cssSelector(".accordion-inner a")).click();

    WebElement ul = driver.findElement(xpath("//ul[@class='nav facet-values']"));
    WebElement newFacetValue = ul.findElement(xpath("//input[@class='inline-edit']"));

    newFacetValue.sendKeys("NewFacetValue");
    newFacetValue.sendKeys(Keys.TAB);

    //ajax with fadein is asking for trouble
    Thread.sleep(500);
    String text = ul.findElement(cssSelector("li span")).getText();
    assertEquals("NewFacetValue", text);


  }

}
