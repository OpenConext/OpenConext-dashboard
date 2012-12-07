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
package nl.surfnet.coin.selfservice.domain;

import static org.junit.Assert.*;

import java.util.Map;

import nl.surfnet.coin.selfservice.domain.Field.Key;
import nl.surfnet.coin.selfservice.domain.Field.Source;
import nl.surfnet.coin.selfservice.domain.Provider.Language;

import org.junit.Test;

/**
 * CompoundServiceProviderTest.java
 * 
 */
public class CompoundServiceProviderTest {

  /**
   * Test method for
   * {@link nl.surfnet.coin.selfservice.domain.CompoundServiceProvider#builder(nl.surfnet.coin.selfservice.domain.ServiceProvider, nl.surfnet.coin.selfservice.domain.License)}
   * .
   */
  @Test
  public void testBuilder() {
    ServiceProvider serviceProvider = new ServiceProvider("id");
    serviceProvider.addDescription(Language.EN.name().toLowerCase(), "EN description");
    serviceProvider.setLogoUrl("http://png");

    Article article = new Article();

    CompoundServiceProvider provider = CompoundServiceProvider.builder(serviceProvider, article);
    Map<Key, String> values = provider.getDistributionFieldValues();
    String des = values.get(Key.ENDUSER_DESCRIPTION_EN);
    assertNull(des);

    String detailLogo = provider.getDetailLogo();
    assertEquals("http://png", detailLogo);

    String appLogo = provider.getAppStoreLogo();
    // looks strange, but corect as we did not save the provider
    assertEquals("/fieldimages/null.img", appLogo);

    des = values.get(Key.ENDUSER_DESCRIPTION_NL);
    assertNull(des);
  }

  @Test
  public void testIsAllowed() {
    assertFalse(CompoundServiceProvider.isAllowedCombination(Key.INSTITUTION_DESCRIPTION_EN, Source.SURFCONEXT));
    assertTrue(CompoundServiceProvider.isAllowedCombination(Key.ENDUSER_DESCRIPTION_NL, Source.SURFCONEXT));

    assertFalse(CompoundServiceProvider.isAllowedCombination(Key.SERVICE_DESCRIPTION_EN, Source.LMNG));
    assertFalse(CompoundServiceProvider.isAllowedCombination(Key.APPSTORE_LOGO, Source.LMNG));
    assertTrue(CompoundServiceProvider.isAllowedCombination(Key.SERVICE_DESCRIPTION_NL, Source.LMNG));
    assertTrue(CompoundServiceProvider.isAllowedCombination(Key.INSTITUTION_DESCRIPTION_NL, Source.LMNG));

    assertTrue(CompoundServiceProvider.isAllowedCombination(Key.SERVICE_DESCRIPTION_NL, Source.DISTRIBUTIONCHANNEL));
    assertTrue(CompoundServiceProvider.isAllowedCombination(Key.SERVICE_DESCRIPTION_NL, Source.DISTRIBUTIONCHANNEL));

  }

}
