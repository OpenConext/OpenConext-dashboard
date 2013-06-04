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
package nl.surfnet.coin.selfservice.util;

import nl.surfnet.coin.api.client.OAuthVersion;
import nl.surfnet.coin.api.client.OpenConextOAuthClient;
import nl.surfnet.coin.api.client.domain.Email;
import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * OpenConextOAuthClientMock.java
 * 
 */
public class OpenConextOAuthClientMock implements OpenConextOAuthClient {

  public enum Users {
    /*
     * ROLE_IDP_SURFCONEXT_ADMIN=IdP Account Administrator
     */
    ADMIN_IDP_SURFCONEXT("adminidpsc"), // admin from institution for dashboard
    /*
     * ROLE_IDP_LICENSE_ADMIN=IdP License Administrator
     */
    ADMIN_IDP_LICENSE("adminidpli"), // admin from institution for showroom
    /*
     * ROLE_DISTRIBUTION_CHANNEL_ADMIN=Distribution Channel Administrator
     */
    ADMIN_DISTRIBUTIE_CHANNEL("admindk"), // admin from surfmarket for showroom
    /*
     * ROLE_USER=Distribution Channel User
     */
    USER("user"),  //mere mortal end-user
    /*
     * Both IdP admins
     */
    ADMIN_IDP_ADMIN("adminidp"),

    ALL("NA");

    private String user;

    private Users(String user) {
      this.user = user;
    }

    public String getUser() {
      return user;
    }

    public static Users fromUser(String userName) {
      Users[] values = Users.values();
      for (Users user : values) {
        if (user.getUser().equalsIgnoreCase(userName)) {
          return user;
        }
      }
      return ALL;
    }
  }

  // Used in showroom.properties
  private static final String LICENSE_TEAM = "license-team";
  private static final String SC_TEAM = "sc-team";
  private static final String ADMIN_TEAM = "admin-team";

  @Override
  public boolean isAccessTokenGranted(String userId) {
    return true;
  }

  @Override
  public String getAuthorizationUrl() {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public void oauthCallback(HttpServletRequest request, String onBehalfOf) {
  }

  @Override
  public Person getPerson(String userId, String onBehalfOf) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public List<Person> getGroupMembers(String groupId, String onBehalfOf) {
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
    }
    List<Person> persons = new ArrayList<Person>();
    String group = groupId.substring(groupId.lastIndexOf(":") + 1);
    persons.add(createPerson("John Doe", "john.doe@"+group));
    persons.add(createPerson("Pitje Puck", "p.p@"+group));
    persons.add(createPerson("Yan Yoe", "yan@"+group));
    return persons;
  }

  private Person createPerson(String displayName, String email) {
    Person p = new Person();
    p.setDisplayName(displayName);
    p.setEmails(Collections.singleton(new Email(email)));
    return p;
  }

  @Override
  public List<Group> getGroups(String userId, String onBehalfOf) {
    throw new RuntimeException("Not implemented");
  }

  @Override
  public List<Group20> getGroups20(String userId, String onBehalfOf) {
    final Users user = Users.fromUser(userId);
    switch (user) {
    case ADMIN_DISTRIBUTIE_CHANNEL:
      return asList(createGroup20(ADMIN_TEAM));
    case ADMIN_IDP_LICENSE:
      return asList(createGroup20(LICENSE_TEAM));
    case ADMIN_IDP_SURFCONEXT:
      return asList(createGroup20(SC_TEAM));
    case ADMIN_IDP_ADMIN:
      return asList(createGroup20(LICENSE_TEAM), createGroup20(SC_TEAM));
    case USER:
      return new ArrayList<Group20>();
    case ALL:
      return asList(createGroup20(LICENSE_TEAM), createGroup20(SC_TEAM), createGroup20(ADMIN_TEAM));
    default:
      throw new RuntimeException("Unknown");
    }

  }

  private Group20 createGroup20(String id) {
    return new Group20(id, id, id);
  }

  @Override
  public Group20 getGroup20(String userId, String groupId, String onBehalfOf) {
    throw new RuntimeException("Not implemented");
  }

  /*
   * The following is needed to be conform the contract of the real
   * OpenConextOAuthClient. For the same reason we get the values our selves
   * from the properties files, as we can't inject them
   */

  public void setCallbackUrl(String url) {
  }

  public void setConsumerSecret(String secret) {
  }

  public void setConsumerKey(String key) {
  }

  public void setEndpointBaseUrl(String url) {
  }

  public void setVersion(OAuthVersion v) {
  }

}
