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

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import selfservice.domain.Provider;
import selfservice.domain.ServiceProvider;
import selfservice.domain.csa.Article;
import selfservice.domain.csa.CompoundServiceProvider;
import selfservice.domain.csa.Field;

public class CompoundServiceProviderTest {

  @Test
  public void testBuilder() {
    ServiceProvider serviceProvider = new ServiceProvider("id");
    serviceProvider.addDescription(Provider.Language.EN.name().toLowerCase(), "EN description");
    serviceProvider.setLogoUrl(CompoundServiceProvider.SR_DEFAULT_LOGO_VALUE);

    Article article = new Article();

    CompoundServiceProvider provider = CompoundServiceProvider.builder(serviceProvider, article);
    Map<Field.Key, String> values = provider.getDistributionFieldValues();
    String des = values.get(Field.Key.ENDUSER_DESCRIPTION_EN);
    assertNull(des);

    String detailLogo = provider.getDetailLogo();
    // looks strange, but corect as we did not save the provider
    assertEquals("/fieldimages/null.img", detailLogo);

    String appLogo = provider.getAppStoreLogo();
    assertEquals("/fieldimages/null.img", appLogo);

    serviceProvider.setLogoUrl("https://static.surfconext.nl/media/idp/windesheim.png");
    provider = CompoundServiceProvider.builder(serviceProvider, article);
    appLogo = provider.getAppStoreLogo();
    assertEquals("https://static.surfconext.nl/media/idp/windesheim.png", appLogo);

    des = values.get(Field.Key.ENDUSER_DESCRIPTION_NL);
    assertNull(des);
  }

  @Test
  public void testIsAllowed() {
    assertFalse(CompoundServiceProvider.isAllowedCombination(Field.Key.INSTITUTION_DESCRIPTION_EN, Field.Source.SURFCONEXT));
    assertTrue(CompoundServiceProvider.isAllowedCombination(Field.Key.APPSTORE_LOGO, Field.Source.SURFCONEXT));

    assertFalse(CompoundServiceProvider.isAllowedCombination(Field.Key.SERVICE_DESCRIPTION_EN, Field.Source.LMNG));
    assertFalse(CompoundServiceProvider.isAllowedCombination(Field.Key.APPSTORE_LOGO, Field.Source.LMNG));
    assertTrue(CompoundServiceProvider.isAllowedCombination(Field.Key.SERVICE_DESCRIPTION_NL, Field.Source.LMNG));
    assertTrue(CompoundServiceProvider.isAllowedCombination(Field.Key.INSTITUTION_DESCRIPTION_NL, Field.Source.LMNG));

    assertTrue(CompoundServiceProvider.isAllowedCombination(Field.Key.SERVICE_DESCRIPTION_NL, Field.Source.DISTRIBUTIONCHANNEL));
    assertTrue(CompoundServiceProvider.isAllowedCombination(Field.Key.SERVICE_DESCRIPTION_NL, Field.Source.DISTRIBUTIONCHANNEL));

    assertFalse(CompoundServiceProvider.isAllowedCombination(Field.Key.ENDUSER_DESCRIPTION_EN, Field.Source.SURFCONEXT));
    assertFalse(CompoundServiceProvider.isAllowedCombination(Field.Key.ENDUSER_DESCRIPTION_NL, Field.Source.SURFCONEXT));
  }

}
