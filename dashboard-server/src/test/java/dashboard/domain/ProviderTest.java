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
package dashboard.domain;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
    public void testIgnoreId() {
        a.setId("A");
        b.setId("B");
        assertEquals(0, a.compareTo(b));
    }

    @Test
    public void testFromMap() {
        Map<String, Object> metaData = new HashMap<>();
        metaData.put("eid", 1L);

        metaData.put("contacts:0:contactType", "support");
        metaData.put("contacts:0:emailAddress", "support@example.com");

        metaData.put("contacts:1:emailAddress", "technical@example.com");

        metaData.put("contacts:2:contactType", "administrative");
        metaData.put("contacts:2:emailAddress", "administrative@example.com");

        metaData.put("contacts:3:contactType", "bogus");
        metaData.put("contacts:3:emailAddress", "bogus@example.com");

        Provider provider = new ServiceProvider(metaData);
        List<ContactPerson> contactPersons = provider.getContactPersons();

        assertEquals(3, contactPersons.size());
        List<ContactPersonType> types = contactPersons.stream().map(cp -> cp.getContactPersonType()).collect(Collectors.toList());

        assertEquals(Arrays.asList(ContactPersonType.support, ContactPersonType.administrative, ContactPersonType.other), types);
    }
}
