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

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Optional;

import com.google.common.base.MoreObjects;

import org.apache.commons.lang.builder.CompareToBuilder;

public class Action {

  public enum Type { LINKREQUEST, UNLINKREQUEST, QUESTION }

  private String jiraKey;
  private String userName;
  private String userEmail;
  private String body;

  private String idpId;
  private String spId;
  private String idpName;
  private String spName;

  private ZonedDateTime requestDate;
  private Type type;
  private String status;

  private String subject;

  private Action(Builder builder) {
    this.jiraKey = builder.jiraKey;
    this.userName = builder.userName;
    this.userEmail = builder.userEmail;
    this.body = builder.body;
    this.idpId = builder.idpId;
    this.spId = builder.spId;
    this.spName = builder.spName;
    this.idpName = builder.idpName;
    this.requestDate = builder.requestDate;
    this.type = builder.type;
    this.status = builder.status;
  }

  public Optional<String> getJiraKey() {
    return Optional.ofNullable(jiraKey);
  }

  public String getUserName() {
    return userName;
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

  public String getIdpName() {
    return idpName;
  }

  public ZonedDateTime getRequestDate() {
    return requestDate;
  }

  public Type getType() {
    return type;
  }

  public String getStatus() {
    return status;
  }

  public String getUserEmail() {
    return userEmail;
  }

  public String getSubject() {
    return subject;
  }

//  public String getIdpName() {
//    return idpName;
//  }

  public String getSpName() {
    return spName;
  }

  /**
   * get a Comparator that sorts by date ascending: newest first
   */
  public static Comparator<? super Action> sortByDateAsc() {
    return (a1, a2) -> new CompareToBuilder()
      .append(a1.getRequestDate(), a2.getRequestDate())
      .toComparison();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(Action.class)
        .add("type", type)
        .add("status", status)
        .add("jiraKey", jiraKey)
        .add("userName", userName)
        .add("userEmail", userEmail)
        .add("requestDate", requestDate)
        .add("idpId", idpId)
        .add("spId", spId)
        .add("spName", spName)
        .add("body", body).toString();
  }

  public Builder unbuild() {
    return new Builder(this);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {

    private String jiraKey;
    private String userName;
    private String userEmail;
    private Type type;
    private String status;
    private String body;
    private String idpId;
    private String spId;
    private String spName;
    private String idpName;
    private ZonedDateTime requestDate;

    private Builder() {
    }

    private Builder(Action action) {
      this.jiraKey = action.jiraKey;
      this.userName = action.userName;
      this.userEmail = action.userEmail;
      this.type = action.type;
      this.status = action.status;
      this.body = action.body;
      this.idpId = action.idpId;
      this.spId = action.spId;
      this.spName = action.spName;
      this.idpName = action.idpName;
      this.requestDate = action.requestDate;
    }

    public Builder requestDate(ZonedDateTime requestDate) {
      this.requestDate = requestDate;
      return this;
    }

    public Builder userEmail(String userEmail) {
      this.userEmail = userEmail;
      return this;
    }

    public Builder userName(String userName) {
      this.userName = userName;
      return this;
    }

    public Builder jiraKey(String jiraKey) {
      this.jiraKey = jiraKey;
      return this;
    }

    public Builder type(Type type) {
      this.type = type;
      return this;
    }

    public Builder body(String body) {
      this.body = body;
      return this;
    }

    public Builder idpId(String idpId) {
      this.idpId = idpId;
      return this;
    }

    public Builder spId(String spId) {
      this.spId = spId;
      return this;
    }

    public Builder spName(String spName) {
      this.spName = spName;
      return this;
    }

    public Builder idpName(String idpName) {
      this.idpName = idpName;
      return this;
    }

    public Builder status(String status) {
      this.status = status;
      return this;
    }

    public Action build() {
      return new Action(this);
    }
  }

}
