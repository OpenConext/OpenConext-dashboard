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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.*;

import static java.lang.String.format;

/**
 * Client implementation for SAB.
 * Depends on an actual 'transport' for communication, most probably HttpClientTransport.
 *
 */
@Component
public class SabClient implements Sab {

  private static final Logger LOG = LoggerFactory.getLogger(SabClient.class);
  private static final String REQUEST_TEMPLATE_LOCATION = "/sab-request.xml";
  protected static final DateTimeFormatter XML_DATE_TIME_FORMAT = ISODateTimeFormat.dateTimeNoMillis().withZone(DateTimeZone.UTC);
  private final SabTransport sabTransport;
  private SabResponseParser sabResponseParser;

  @Autowired
  public SabClient(SabTransport sabTransport) {
    this.sabTransport = sabTransport;
    sabResponseParser = new SabResponseParser();
  }

  @Override
  public boolean hasRoleForOrganisation(String userId, String role, String organisation) {
    try {
      SabRoleHolder sabRoleHolder = getRoles(userId);
      return sabRoleHolder.getOrganisation().equals(organisation) && sabRoleHolder.getRoles().contains(role);
    } catch (IOException e) {
      LOG.error("IOException while doing request to SAB. Will return false.", e);
      return false;
    }
  }

  @Override
  public SabRoleHolder getRoles(String userId) throws IOException {

    String messageId = UUID.randomUUID().toString();
    String request = createRequest(userId, messageId);
    return sabResponseParser.parse(sabTransport.getResponse(request));
  }

  @Override
  public SabPersonsInRole getPersonsInRoleForOrganization(final String organisationAbbreviation, final String role) {
    try {
      InputStream responseAsStream = sabTransport.getRestResponse(format("/profile?abbrev=%s&role=%s", organisationAbbreviation, role));
      HashMap<String, Object> result =
              new ObjectMapper().readValue(responseAsStream, HashMap.class);
      List<SabPerson> allSabPersons = Lists.transform((List<Map>) result.get("profiles"), new Function<Map, SabPerson>() {
        public SabPerson apply(Map person) {
          List<SabRole> sabRoles = Lists.transform((List<Map>) person.get("authorisations"), new Function<Map, SabRole>() {
            public SabRole apply(Map role) {
              return new SabRole((String) role.get("short"), (String) role.get("role"));
            }
          });
          return new SabPerson((String) person.get("firstname"), (String) person.get("surname"), (String) person.get("uid"), sabRoles);
        }
      });
      Collection<SabPerson> sabPersons = Collections2.filter(allSabPersons, new Predicate<SabPerson>() {
        @Override
        public boolean apply(SabPerson person) {
          return person.hasRole(role);
        }
      });
      return new SabPersonsInRole(sabPersons, role);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Create request string from template
   *
   * @param userId the userId to use
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
