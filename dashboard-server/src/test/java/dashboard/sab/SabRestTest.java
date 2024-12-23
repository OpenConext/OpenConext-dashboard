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
package dashboard.sab;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import dashboard.manage.ClassPathResourceManage;
import org.apache.commons.io.IOUtils;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

public class SabRestTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8891);

    private final Sab sab = new SabRest(new ClassPathResourceManage(),
            "user",
            "secret",
            "http://localhost:8891/api");


    @Test
    public void getRoles() throws IOException {
        String sabResponse = IOUtils.toString(new ClassPathResource("sab-json/profile.json").getInputStream(), Charset.defaultCharset());
        String guid = UUID.randomUUID().toString();
        String role = "SURFconextverantwoordelijke";

        stubFor(get(urlPathEqualTo("/api/profile"))
                .withQueryParam("guid", new EqualToPattern(guid))
                .withQueryParam("role", new EqualToPattern(role))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json").withBody(sabResponse)));
        List<SabPerson> sabPersons = sab.getPersonsInRoleForOrganization(guid, role);
        assertEquals(6, sabPersons.size());
    }

    @Test
    public void getEmptyRoles() throws IOException {
        String sabResponse = IOUtils.toString(new ClassPathResource("sab-json/empty-roles.json").getInputStream(), Charset.defaultCharset());
        String guid = UUID.randomUUID().toString();
        String role = "SURFconextverantwoordelijke";

        stubFor(get(urlPathEqualTo("/api/profile"))
                .withQueryParam("guid", new EqualToPattern(guid))
                .withQueryParam("role", new EqualToPattern(role))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json").withBody(sabResponse)));
        List<SabPerson> sabPersons = sab.getPersonsInRoleForOrganization(guid, role);
        assertEquals(0, sabPersons.size());
    }
}
