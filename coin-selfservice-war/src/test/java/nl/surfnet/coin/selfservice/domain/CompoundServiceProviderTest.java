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
import nl.surfnet.coin.selfservice.domain.Provider.Language;

import org.junit.Test;

/**
 * CompoundServiceProviderTest.java
 *
 */
public class CompoundServiceProviderTest {


  /**
   * Test method for {@link nl.surfnet.coin.selfservice.domain.CompoundServiceProvider#builder(nl.surfnet.coin.selfservice.domain.ServiceProvider, nl.surfnet.coin.selfservice.domain.License)}.
   */
  @Test
  public void testBuilder() {
    ServiceProvider serviceProvider = new ServiceProvider("id");
    serviceProvider.addDescription(Language.EN.name().toLowerCase(),"EN description");
    
    License license = new License();
    
    CompoundServiceProvider provider = CompoundServiceProvider.builder(serviceProvider, license);
    Map<Key, String> values = provider.getDistributionFieldValues();
    String des = values.get(Key.ENDUSER_DESCRIPTION_EN);
    assertNull(des);
  
    des = values.get(Key.ENDUSER_DESCRIPTION_NL);
    assertEquals("TODO" ,des);
}

}
