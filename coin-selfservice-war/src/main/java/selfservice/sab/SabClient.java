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

package selfservice.sab;

import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private SabResponseParser sabResponseParser;

  public SabClient(SabTransport sabTransport) {
    this.sabTransport = sabTransport;
    sabResponseParser = new SabResponseParser();
  }

  @Override
  public SabRoleHolder getRoles(String userId) throws IOException {
    String messageId = UUID.randomUUID().toString();
    String request = createRequest(userId, messageId);
    return sabResponseParser.parse(sabTransport.getResponse(request));
  }

  @Override
  @SuppressWarnings("unchecked")
  public Collection<SabPerson> getPersonsInRoleForOrganization(final String organisationAbbreviation, final String role) {
    try (InputStream inputStream = sabTransport.getRestResponse(organisationAbbreviation, role)) {
      List<Map<String, Object>> profiles = (List<Map<String, Object>>) objectMapper.readValue(inputStream, HashMap.class).get("profiles");

      return profiles.stream()
          .map(profile -> {
            List<SabRole> sabRoles = ((List<Map<String, String>>) profile.get("authorisations")).stream()
                .map(authorisation -> new SabRole(authorisation.get("short"), authorisation.get("role")))
                .collect(toList());
            return new SabPerson((String) profile.get("firstname"), (String) profile.get("surname"), (String) profile.get("uid"), sabRoles);
          })
          .filter(p -> p.hasRole(role))
          .collect(toList());
    } catch (IOException | RuntimeException e) {
      LOG.warn("Could not retrieve SAB info");
      return Collections.emptyList();
    }
  }

  /**
   * Create request string from template
   *
   * @param userId    the userId to use
   * @param messageId the messageId to use
   * @return Serialized XML
   */
  public String createRequest(String userId, String messageId) {
    String template;
    try {
      template = IOUtils.toString(this.getClass().getResourceAsStream(REQUEST_TEMPLATE_LOCATION), "UTF-8");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    String issueInstant = XML_DATE_TIME_FORMAT.print(new Date().getTime());
    return MessageFormat.format(template, messageId, issueInstant, userId);
  }

}
