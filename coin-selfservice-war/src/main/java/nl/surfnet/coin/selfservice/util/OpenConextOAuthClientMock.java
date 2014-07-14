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

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.api.client.OAuthVersion;
import nl.surfnet.coin.api.client.OpenConextOAuthClient;
import nl.surfnet.coin.api.client.domain.Email;
import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.selfservice.domain.CoinAuthority;

public class OpenConextOAuthClientMock implements OpenConextOAuthClient {

  public enum Users {

    dashboard_admin(CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN),
    dashboard_viewer(CoinAuthority.Authority.ROLE_DASHBOARD_VIEWER),
    dashboard_super_user(CoinAuthority.Authority.ROLE_DASHBOARD_SUPER_USER),
    showroom_admin(CoinAuthority.Authority.ROLE_SHOWROOM_ADMIN),
    showroom_user(CoinAuthority.Authority.ROLE_SHOWROOM_USER),
    showroom_super_user(CoinAuthority.Authority.ROLE_SHOWROOM_SUPER_USER),
    noroles(null);

    private CoinAuthority.Authority user;

    private Users(CoinAuthority.Authority user) {
      this.user = user;
    }

    public CoinAuthority.Authority getUser() {
      return user;
    }

    public static Users fromUser(String userName) {
      Users[] values = Users.values();
      for (Users user : values) {
        if (user.name().equalsIgnoreCase(userName)) {
          return user;
        }
      }
      throw new RuntimeException("User unknown")   ;
    }
  }

  // Used in showroom.properties


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
    List<Person> persons = new ArrayList<>();
    String group = groupId.substring(groupId.lastIndexOf(":") + 1);
    persons.add(createPerson("John Doe", "john.doe@" + group));
    persons.add(createPerson("Pitje Puck", "p.p@" + group));
    persons.add(createPerson("Yan Yoe", "yan@" + group));
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
      case dashboard_admin:
        return asList(createGroup20("dashboard.admin"));
      case dashboard_viewer:
        return asList(createGroup20("dashboard.viewer"));
      case dashboard_super_user:
        return asList(createGroup20("dashboard.super.user"));
      case showroom_admin:
        return asList(createGroup20("showroom.admin"));
      case showroom_user:
        return asList(createGroup20("showroom.user"));
      case showroom_super_user:
        return asList(createGroup20("showroom.super.user"));
      case noroles:
        return Collections.emptyList();
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
