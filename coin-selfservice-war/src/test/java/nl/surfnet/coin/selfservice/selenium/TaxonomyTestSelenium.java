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

import nl.surfnet.coin.selfservice.util.OpenConextOAuthClientMock;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.*;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.openqa.selenium.By.cssSelector;
import static org.openqa.selenium.By.xpath;

public class TaxonomyTestSelenium extends SeleniumSupport {

  private RestTemplate restTemplate = new RestTemplate();

  @Ignore
  @Test
  public void testCreateTranslationThroughRestInterface() {
    /*
     * We test through the GUI as well, but this is WIP for injecting state to ease up the tests
     */
    login("taxonomy-overview");
    String facet = "{\"name\":\"Pietje\"}";

    HttpHeaders headers = new HttpHeaders();
    headers.add("Cookie", "JSESSIONID=" + getJsessionId());
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> requestEntity = new HttpEntity<String>(facet, headers);
    ResponseEntity<Long> response = restTemplate.exchange(
            getSelfserviceBaseUrl() + "shopadmin/facet.shtml?tokencheck={tokencheck}",
            HttpMethod.POST,
            requestEntity,
            Long.class, getTokenCheck());

    HttpStatus statusCode = response.getStatusCode();
    assertEquals(200, statusCode.value());
    Long res = response.getBody();

  }
  /*
   http://sauceio.com/index.php/2010/01/selenium-totw-css-selectors-in-selenium-demystified/
   */

  @Test
  public void createFacetWithValue() throws InterruptedException {
    WebDriver driver = login("taxonomy-overview");

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
    Thread.sleep(1500);
    String text = ul.findElement(cssSelector("li span")).getText();
    assertEquals("NewFacetValue", text);


  }

  private WebDriver login(String location) {
    WebDriver driver = getRestartedWebDriver();
    driver.get(getSelfserviceBaseUrl() + "shopadmin/" + location + ".shtml");
    loginAtMujinaAs(OpenConextOAuthClientMock.Users.ADMIN_DISTRIBUTIE_CHANNEL); // login
    return driver;
  }

}
