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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Client implementation for SAB.
 * Depends on an actual 'transport' for communication, most probably HttpClientTransport.
 */
public class SabClient implements Sab {

    private static final Logger LOG = LoggerFactory.getLogger(SabClient.class);

    private static final String REQUEST_TEMPLATE_LOCATION = "/sab-request.xml";

    protected static final DateTimeFormatter XML_DATE_TIME_FORMAT = ISODateTimeFormat.dateTimeNoMillis().withZone(DateTimeZone.UTC);

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SabTransport sabTransport;
    private final SabResponseParser sabResponseParser = new SabResponseParser();

    public SabClient(SabTransport sabTransport) {
        this.sabTransport = sabTransport;
    }

    @Override
    public Optional<SabRoleHolder> getRoles(String userId) {
        String messageId = UUID.randomUUID().toString();
        String requestBody = createRequest(userId, messageId);

        try (InputStream is = sabTransport.getResponse(requestBody)) {
            SabRoleHolder sabRoleHolder = sabResponseParser.parse(is);
            return CollectionUtils.isEmpty(sabRoleHolder.getRoles()) && StringUtils.isEmpty(sabRoleHolder.getOrganisation()) ? Optional.empty() : Optional.of(sabRoleHolder);
        } catch (IOException e) {
            LOG.warn("Skipping SAB entitlement, SAB request got IOException: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<SabPerson> getPersonsInRoleForOrganization(String organisationAbbreviation, String role) {
        try (InputStream inputStream = sabTransport.getRestResponse(organisationAbbreviation, role)) {
            String json = IOUtils.toString(inputStream);

            LOG.debug("SAB results 'getPersonsInRoleForOrganization' for {} {} is {}", organisationAbbreviation, role, json);

            List<Map<String, Object>> profiles = (List<Map<String, Object>>) objectMapper.readValue(json, HashMap.class).get("profiles");

            return profiles.stream()
                    .map(profile -> {
                        List<SabRole> sabRoles = ((List<Map<String, String>>) profile.get("authorisations")).stream()
                                .map(authorisation -> new SabRole(authorisation.get("short"), authorisation.get("role")))
                                .collect(toList());
                        return new SabPerson(
                                (String) profile.get("firstname"),
                                (String) profile.get("surname"),
                                (String) profile.get("uid"),
                                (String) profile.get("email"),
                                sabRoles);
                    })
                    .filter(p -> p.hasRole(role))
                    .collect(toList());
        } catch (IOException e) {
            LOG.warn("Could not retrieve SAB info", e);
            return Collections.emptyList();
        }
    }

    @Override
    public String getSabEmailsForOrganization(String entityId, String role) {
        return getPersonsInRoleForOrganization(entityId, role)
                .stream()
                .map(SabPerson::getEmail)
                .collect(Collectors.joining(", "));
    }

    /**
     * Create request string from template
     *
     * @param userId    the userId to use
     * @param messageId the messageId to use
     * @return Serialized XML
     */
    public String createRequest(String userId, String messageId) {
        try (InputStream is = this.getClass().getResourceAsStream(REQUEST_TEMPLATE_LOCATION)) {
            String template = IOUtils.toString(is, "UTF-8");
            String issueInstant = XML_DATE_TIME_FORMAT.print(new Date().getTime());
            return MessageFormat.format(template, messageId, issueInstant, userId);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

}
