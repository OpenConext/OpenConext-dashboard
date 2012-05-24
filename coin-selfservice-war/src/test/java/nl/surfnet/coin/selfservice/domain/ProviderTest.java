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

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;

/**
 * Tests for {@link Provider}
 */
public class ProviderTest {
  private Provider a;
  private Provider b;

  @Before
  public void setup() {
    a = new IdentityProvider();
    b = new IdentityProvider();
  }

  @Test
  public void testCompareToNulls() throws Exception {
    assertEquals(0, a.compareTo(b));
  }

  @Test
  public void testCompareByName() {
    a.setName("a");
    b.setName("B");
    assertEquals(-1, a.compareTo(b));
  }

  @Test
  public void testCompareBySameName() {
    a.setId("A");
    a.setName("A");
    b.setId("B");
    b.setName("A");
    assertFalse(a.equals(b));
    assertEquals(0, a.compareTo(b));
  }

  @Test
  public void testCompareById() {
    a.setId("A");
    b.setId("B");
    assertEquals(-1, a.compareTo(b));
  }

  @Test
  public void testCompareByNameOrId() {
    a.setName("A");
    b.setId("B");
    assertEquals(-1, a.compareTo(b));
  }

  @Test
  public void testCompareByIdOrName() {
    a.setId("A");
    b.setName("B");
    assertEquals(-1, a.compareTo(b));
  }

  @Test
  public void testEqualsById() {
    a.setId("A");
    b.setId("A");
    assertEquals(a,b);
    assertEquals(0, a.compareTo(b));
  }
}
