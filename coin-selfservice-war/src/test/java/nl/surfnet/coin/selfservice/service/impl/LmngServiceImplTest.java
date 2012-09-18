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

package nl.surfnet.coin.selfservice.service.impl;

import static junit.framework.Assert.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.junit.Ignore;

import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.License;

/**
 * Test class for {@code LmngServiceImpl}
 *
 */
public class LmngServiceImplTest {

  @Ignore
  public void testParseJsonToAttributeLabels() throws IOException {
    LmngServiceImpl lmngServiceImpl = new LmngServiceImpl();
    //TODO get properties set into service
    Date date = new Date();
    IdentityProvider identityProvider = new IdentityProvider("testid", "testinstitutionId", "testName");
    List<License> licenses = lmngServiceImpl.getLicensesForIdentityProvider(identityProvider, date);
   
    assertEquals("Incorrect number of results", 1 ,licenses.size());
    assertEquals("Incorrect name for IDP", "Hogeschool Aanbesteding" ,licenses.get(0).getIdentityName());

  }

}
