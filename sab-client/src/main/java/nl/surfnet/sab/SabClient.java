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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Client implementation for SAB.
 * Depends on an actual 'transport' for communication, most probably HttpClientTransport.
 *
 */
@Component
public class SabClient implements Sab {

  private static final Logger LOG = LoggerFactory.getLogger(SabClient.class);
  private static final String REQUEST_TEMPLATE_LOCATION = "/sab-request.xml";
  private static final DateTimeFormatter XML_DATE_TIME_FORMAT = ISODateTimeFormat.dateTimeNoMillis();

  private SabTransport transport;
  private SabResponseParser sabResponseParser = new SabResponseParser();

  @Override
  public boolean hasRoleForOrganisation(String userId, String role, String organisation) {
    try {

      SabRoleHolder sabRoleHolder = getRoles(userId);
      return sabRoleHolder.getOrganization().equals(organisation) && sabRoleHolder.getRoles().contains(role);
    } catch (IOException e) {
      LOG.error("IOException while doing request to SAB. Will return false.", e);
      return false;
    }
  }

  @Override
  public SabRoleHolder getRoles(String userId) throws IOException {

    String messageId = UUID.randomUUID().toString();
    String request = createRequest(userId, messageId);
    return sabResponseParser.parse(transport.getResponse(request));
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

  public void setTransport(SabTransport transport) {
    this.transport = transport;
  }


}
