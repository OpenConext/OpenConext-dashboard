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
package dashboard.filter;

import dashboard.domain.CoinAuthority;
import dashboard.domain.CoinUser;
import dashboard.util.SpringSecurity;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class SpringSecurityUtil {

    protected static void assertNoRoleIsGranted() {
        CoinUser user = SpringSecurity.getCurrentUser();
        assertEquals(0, user.getAuthorityEnums().size());
    }

    protected static void assertRoleIsGranted(CoinAuthority.Authority... expectedAuthorities) {
        CoinUser user = SpringSecurity.getCurrentUser();
        List<CoinAuthority.Authority> actualAuthorities = user.getAuthorityEnums();
        assertEquals("expected roles: " + Arrays.asList(expectedAuthorities) + ", actual roles: " + actualAuthorities, expectedAuthorities.length, actualAuthorities.size());
        assertTrue("expected roles: " + Arrays.asList(expectedAuthorities) + ", actual roles: " + actualAuthorities, actualAuthorities.containsAll(Arrays.asList(expectedAuthorities)));
    }

    protected static void assertRoleIsNotGranted(CoinAuthority.Authority... expectedAuthorities) {
        CoinUser user = SpringSecurity.getCurrentUser();
        List<CoinAuthority.Authority> actualAuthorities = user.getAuthorityEnums();
        for (CoinAuthority.Authority expectedAuthority : expectedAuthorities) {
            assertFalse("Role not to be expected: " + expectedAuthority, actualAuthorities.contains(expectedAuthorities));
        }
    }

    public static void setAuthentication(String theUsersUid) {
        CoinUser coinUser = new CoinUser();
        coinUser.setUid(theUsersUid);

        setAuthentication(coinUser);
    }

    public static void setAuthentication(CoinUser coinUser) {
        TestingAuthenticationToken token = new TestingAuthenticationToken(coinUser, "");
        token.setAuthenticated(true);

        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
