/*
 * Copyright 2013 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.surfnet.sab;


import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class SabResponseParserTest {

  @Test
  public void testParse() throws IOException {
    InputStream stream = this.getClass().getResourceAsStream("/response.xml");

    SabRoleHolder srh = new SabResponseParser().parse(stream);

    assertEquals("SURFNET", srh.getOrganisation());
    assertTrue("roles should contain Infraverantwoordelijke", srh.getRoles().contains("Infraverantwoordelijke"));
    assertEquals("roles should count 9", 9, srh.getRoles().size());
  }

  @Test
  public void nameIdNotFoundShouldNotThrowException() throws IOException {
    InputStream stream = this.getClass().getResourceAsStream("/response-nameidnotfound.xml");

    SabRoleHolder srh = new SabResponseParser().parse(stream);
    assertNotNull(srh);
    assertEquals(0, srh.getRoles().size());
  }

  @Test(expected = IOException.class)
  public void blockedByAclShouldThrowException() throws IOException {
    InputStream stream = this.getClass().getResourceAsStream("/response-aclblocked.xml");
    new SabResponseParser().parse(stream);
  }
}
