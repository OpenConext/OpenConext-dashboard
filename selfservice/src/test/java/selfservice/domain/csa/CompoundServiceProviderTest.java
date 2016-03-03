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
package selfservice.domain.csa;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;

import selfservice.domain.ServiceProvider;
import selfservice.domain.csa.Field.Key;
import selfservice.domain.csa.Field.Source;

public class CompoundServiceProviderTest {

  @Test
  public void testBuilder() {
    ServiceProvider serviceProvider = new ServiceProvider(
      metaData(new String[]{
        "entityid", "id"

      }));
    Article article = new Article();

    CompoundServiceProvider provider = CompoundServiceProvider.builder(serviceProvider, Optional.of(article));
    Map<Key, String> values = provider.getDistributionFieldValues();
    String des = values.get(Key.ENDUSER_DESCRIPTION_EN);
    assertNull(des);

    String detailLogo = provider.getDetailLogo();
    // looks strange, but corect as we did not save the provider
    assertEquals("/fieldimages/null.img", detailLogo);

    String appLogo = provider.getAppStoreLogo();
    assertEquals("/fieldimages/null.img", appLogo);

    serviceProvider = new ServiceProvider(
      metaData(new String[]{
        "entityid", "id"}, new String[]{"logo:0:url", "https://static.surfconext.nl/media/idp/windesheim.png"}));
    provider = CompoundServiceProvider.builder(serviceProvider, Optional.of(article));
    appLogo = provider.getAppStoreLogo();
    assertEquals("https://static.surfconext.nl/media/idp/windesheim.png", appLogo);

    des = values.get(Key.ENDUSER_DESCRIPTION_NL);
    assertNull(des);
  }

  private Map<String, Object> metaData(String[]... args) {
    Map<String, Object> metaData = new HashMap<>();
    asList(args).forEach((arg) -> metaData.put(arg[0], arg[1]));
    return metaData;
  }

  @Test
  public void testIsAllowed() {
    assertFalse(CompoundServiceProvider.isAllowedCombination(Key.INSTITUTION_DESCRIPTION_EN, Source.SURFCONEXT));
    assertTrue(CompoundServiceProvider.isAllowedCombination(Key.APPSTORE_LOGO, Source.SURFCONEXT));

    assertFalse(CompoundServiceProvider.isAllowedCombination(Key.SERVICE_DESCRIPTION_EN, Source.LMNG));
    assertFalse(CompoundServiceProvider.isAllowedCombination(Key.APPSTORE_LOGO, Source.LMNG));
    assertTrue(CompoundServiceProvider.isAllowedCombination(Key.SERVICE_DESCRIPTION_NL, Source.LMNG));
    assertTrue(CompoundServiceProvider.isAllowedCombination(Key.INSTITUTION_DESCRIPTION_NL, Source.LMNG));

    assertTrue(CompoundServiceProvider.isAllowedCombination(Key.SERVICE_DESCRIPTION_NL, Source.DISTRIBUTIONCHANNEL));
    assertTrue(CompoundServiceProvider.isAllowedCombination(Key.SERVICE_DESCRIPTION_NL, Source.DISTRIBUTIONCHANNEL));

    assertFalse(CompoundServiceProvider.isAllowedCombination(Key.ENDUSER_DESCRIPTION_EN, Source.SURFCONEXT));
    assertFalse(CompoundServiceProvider.isAllowedCombination(Key.ENDUSER_DESCRIPTION_NL, Source.SURFCONEXT));
  }

}
