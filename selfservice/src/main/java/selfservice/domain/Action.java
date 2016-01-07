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

package selfservice.domain;

import org.apache.commons.lang.builder.CompareToBuilder;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.Comparator;
import java.util.Date;

public class Action {

  private long id;
  private String jiraKey;
  private String userId;
  private String userName;
  private String userEmail;
  private String body;

  private String idpId;
  private String spId;
  private String idpName;
  private String spName;

  private Date requestDate = new Date();
  private JiraTask.Type type;
  private JiraTask.Status status = JiraTask.Status.OPEN;
  private String institutionId;

  private String subject;

  public Action() {
  }

  public Action(String jiraKey, String userId, String userName, String userEmail, JiraTask.Type type, JiraTask.Status status, String body, String idpId,
                String spId, String institutionId, Date requestDate) {
    this.userId = userId;
    this.jiraKey = jiraKey;
    this.userName = userName;
    this.userEmail = userEmail;
    this.body = body;
    this.idpId = idpId;
    this.spId = spId;
    this.requestDate = requestDate;
    this.type = type;
    this.status = status;
    this.institutionId = institutionId;
  }

  public String getJiraKey() {
    return jiraKey;
  }

  public String getUserName() {
    return userName;
  }

  public String getUserId() {
    return userId;
  }

  public String getBody() {
    return body;
  }

  public String getIdpId() {
    return idpId;
  }

  public String getSpId() {
    return spId;
  }

  public Date getRequestDate() {
    return requestDate;
  }

  public JiraTask.Type getType() {
    return type;
  }

  public JiraTask.Status getStatus() {
    return status;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getInstitutionId() {
    return institutionId;
  }

  public void setJiraKey(String jiraKey) {
    this.jiraKey = jiraKey;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public void setIdpId(String idpId) {
    this.idpId = idpId;
  }

  public void setSpId(String spId) {
    this.spId = spId;
  }

  public void setRequestDate(Date requestDate) {
    this.requestDate = requestDate;
  }

  public void setType(JiraTask.Type type) {
    this.type = type;
  }

  public void setStatus(JiraTask.Status status) {
    this.status = status;
  }

  public void setInstitutionId(String institutionId) {
    checkArgument(!isNullOrEmpty(institutionId));
    this.institutionId = institutionId;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public void setUserEmail(String userEmail) {
    this.userEmail = userEmail;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getIdpName() {
    return idpName;
  }

  public void setIdpName(String idpName) {
    this.idpName = idpName;
  }

  public String getSpName() {
    return spName;
  }

  public void setSpName(String spName) {
    this.spName = spName;
  }

  /**
   * get a Comparator that sorts by date ascending: newest first
   *
   * @return
   */
  public static Comparator<? super Action> sortByDateAsc() {
    return (a1, a2) -> new CompareToBuilder()
      .append(a1.getRequestDate(), a2.getRequestDate())
      .toComparison();
  }

}
