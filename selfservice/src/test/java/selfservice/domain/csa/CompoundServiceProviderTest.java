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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static selfservice.domain.csa.CompoundServiceProvider.isAllowedCombination;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import org.junit.Test;

import selfservice.domain.ServiceProvider;
import selfservice.domain.csa.Field.Key;
import selfservice.domain.csa.Field.Source;

public class CompoundServiceProviderTest {

  @Test
  public void testBuilder() {
    ServiceProvider serviceProvider = new ServiceProvider(ImmutableMap.of("entityid", "id"));
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

    serviceProvider = new ServiceProvider(ImmutableMap.of("entityid", "id", "logo:0:url", "https://static.surfconext.nl/media/idp/windesheim.png"));
    provider = CompoundServiceProvider.builder(serviceProvider, Optional.of(article));
    appLogo = provider.getAppStoreLogo();
    assertEquals("https://static.surfconext.nl/media/idp/windesheim.png", appLogo);

    des = values.get(Key.ENDUSER_DESCRIPTION_NL);

    assertNull(des);
  }

  @Test
  public void testIsAllowed() {
    assertTrue(isAllowedCombination(Key.APPSTORE_LOGO, Source.SURFCONEXT));
    assertTrue(isAllowedCombination(Key.APP_URL, Source.SURFCONEXT));
    assertFalse(isAllowedCombination(Key.INSTITUTION_DESCRIPTION_EN, Source.SURFCONEXT));
    assertFalse(isAllowedCombination(Key.ENDUSER_DESCRIPTION_EN, Source.SURFCONEXT));
    assertFalse(isAllowedCombination(Key.ENDUSER_DESCRIPTION_NL, Source.SURFCONEXT));

    assertFalse(isAllowedCombination(Key.SERVICE_DESCRIPTION_EN, Source.LMNG));
    assertFalse(isAllowedCombination(Key.APPSTORE_LOGO, Source.LMNG));
    assertTrue(isAllowedCombination(Key.SERVICE_DESCRIPTION_NL, Source.LMNG));
    assertTrue(isAllowedCombination(Key.INSTITUTION_DESCRIPTION_NL, Source.LMNG));

    assertTrue(isAllowedCombination(Key.SERVICE_DESCRIPTION_NL, Source.DISTRIBUTIONCHANNEL));
    assertTrue(isAllowedCombination(Key.SERVICE_DESCRIPTION_NL, Source.DISTRIBUTIONCHANNEL));
  }

}
