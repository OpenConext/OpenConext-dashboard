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
package selfservice.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import selfservice.domain.ARP;
import selfservice.janus.domain.EntityMetadata;
import selfservice.util.JanusRestClientMock;

/**
 * JanusRestClientMockTest.java
 */
public class JanusRestClientMockTest {

  private JanusRestClientMock mock = new JanusRestClientMock();
  private final static String SP_ENTITY_ID = "http://mock-sp";
  private final static String IDP_ENTITY_ID = "http://mock-idp";

  /**
   * Test method for {@link JanusRestClientMock#getMetadataByEntityId(java.lang.String)}.
   */
  @Test
  public void testGetMetadataByEntityId() {
    EntityMetadata metaData = mock.getMetadataByEntityId(SP_ENTITY_ID);
    assertEquals(SP_ENTITY_ID, metaData.getAppEntityId());

    metaData = mock.getMetadataByEntityId(IDP_ENTITY_ID);
    assertEquals(IDP_ENTITY_ID, metaData.getAppEntityId());
    assertEquals("mock-institution-id", metaData.getInstutionId());
  }

  @Test
  public void getArp() {
    ARP arp = mock.getArp(SP_ENTITY_ID);
    assertNotNull(arp);
  }

}
