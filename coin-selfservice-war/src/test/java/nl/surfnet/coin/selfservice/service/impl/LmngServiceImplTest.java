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

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import nl.surfnet.coin.selfservice.dao.LmngIdentifierDao;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.License;

import org.junit.Before;
import org.junit.Ignore;

/**
 * Test class for {@code LmngServiceImpl}
 *
 */
public class LmngServiceImplTest {
  
  private Properties properties;
  private LmngServiceImpl lmngServiceImpl;
  private LmngIdentifierDao mockLmngIdentifierDao;
  
  @Before
  public void init() throws FileNotFoundException, IOException{
    properties = new Properties();
    properties.load(this.getClass().getResourceAsStream ("/coin-selfservice.properties"));
    lmngServiceImpl = new LmngServiceImpl();
    lmngServiceImpl.setEndpoint(properties.getProperty("coin-lmng-endpoint"));
    lmngServiceImpl.setUser(properties.getProperty("coin-lmng-user"));
    lmngServiceImpl.setPassword(properties.getProperty("coin-lmng-password"));
    
    mockLmngIdentifierDao = mock(LmngIdentifierDao.class);
    lmngServiceImpl.setLmngIdentifierDao(mockLmngIdentifierDao);
  }
  
  @Ignore // we us this for a local integration test only
  public void testRetrieveLmngData() throws IOException {
    when(mockLmngIdentifierDao.getLmngIdForIdentityProviderId("testId")).thenReturn("lmngId");
    
    Date date = new Date();
    IdentityProvider identityProvider = new IdentityProvider("testId", "testinstitutionId", "testName");
    List<License> licenses = lmngServiceImpl.getLicensesForIdentityProvider(identityProvider, date);
   
    assertEquals("Incorrect number of results", 1 ,licenses.size());
    assertEquals("Incorrect name for IDP", "Hogeschool Aanbesteding" ,licenses.get(0).getIdentityName());

  }

}
