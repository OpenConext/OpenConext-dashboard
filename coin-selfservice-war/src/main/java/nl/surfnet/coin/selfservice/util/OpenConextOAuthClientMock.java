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

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.api.client.OpenConextOAuthClient;
import nl.surfnet.coin.api.client.domain.Group;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;

/**
 * OpenConextOAuthClientMock.java
 * 
 */
public class OpenConextOAuthClientMock implements OpenConextOAuthClient {
  private String adminLicentieIdPTeam;
  private String adminSurfConextIdPTeam;
  private String adminDistributionTeam;

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#isAccessTokenGranted(java
   * .lang.String)
   */
  @Override
  public boolean isAccessTokenGranted(String userId) {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.api.client.OpenConextOAuthClient#getAuthorizationUrl()
   */
  @Override
  public String getAuthorizationUrl() {
    throw new RuntimeException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#oauthCallback(javax.servlet
   * .http.HttpServletRequest, java.lang.String)
   */
  @Override
  public void oauthCallback(HttpServletRequest request, String onBehalfOf) {
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#getPerson(java.lang.String
   * , java.lang.String)
   */
  @Override
  public Person getPerson(String userId, String onBehalfOf) {
    throw new RuntimeException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#getGroupMembers(java.lang
   * .String, java.lang.String)
   */
  @Override
  public List<Person> getGroupMembers(String groupId, String onBehalfOf) {
    throw new RuntimeException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#getGroups(java.lang.String
   * , java.lang.String)
   */
  @Override
  public List<Group> getGroups(String userId, String onBehalfOf) {
    throw new RuntimeException("Not implemented");
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#getGroups20(java.lang.
   * String, java.lang.String)
   */
  @Override
  public List<Group20> getGroups20(String userId, String onBehalfOf) {
    return Arrays.asList(createGroup20(adminLicentieIdPTeam), createGroup20(adminSurfConextIdPTeam), createGroup20(adminDistributionTeam));
  }
  
  private Group20 createGroup20(String id) {
    return new Group20(id, null, null);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.api.client.OpenConextOAuthClient#getGroup20(java.lang.String
   * , java.lang.String, java.lang.String)
   */
  @Override
  public Group20 getGroup20(String userId, String groupId, String onBehalfOf) {
    throw new RuntimeException("Not implemented");
  }

  public void setAdminLicentieIdPTeam(String adminLicentieIdPTeam) {
    this.adminLicentieIdPTeam = adminLicentieIdPTeam;
  }

  public void setAdminSurfConextIdPTeam(String adminSurfConextIdPTeam) {
    this.adminSurfConextIdPTeam = adminSurfConextIdPTeam;
  }

  public void setAdminDistributionTeam(String adminDistributionTeam) {
    this.adminDistributionTeam = adminDistributionTeam;
  }


}
