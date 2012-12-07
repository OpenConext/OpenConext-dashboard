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

package nl.surfnet.coin.selfservice.domain;

/**
 * Represents the result row of the mysql query to get login statistics
 */
public class StatResult implements Comparable<StatResult> {

  private String spEntityId;
  private String spName;
  private long millis;
  private Integer logins;
  private String idpEntityId;

  public StatResult(String spEntityId, String spName, long millis, Integer logins, String idpEntityId) {
    this.spEntityId = spEntityId;
    this.spName = spName;
    this.millis = millis;
    this.logins = logins;
    this.idpEntityId = idpEntityId;
  }

  public String getSpEntityId() {
    return spEntityId;
  }

  public String getSpName() {
    return spName;
  }

  public long getMillis() {
    return millis;
  }

  public Integer getLogins() {
    return logins;
  }

  public String getIdpEntityId() {
    return idpEntityId;
  }

  @Override
  public int compareTo(StatResult that) {
    if (this == that) {
      return 0;
    }

    final String thisIdpEntityId = this.getIdpEntityId();
    final String thatIdpEntityId = that.getIdpEntityId();

    if (thisIdpEntityId.equals(thatIdpEntityId)) {

      final String thisSpEntityId = this.getSpEntityId();
      final String thatSpEntityId = that.getSpEntityId();

      if (thisSpEntityId.equals(thatSpEntityId)) {
        return millis < that.millis ? -1 : (millis == that.millis ? 0 : 1);
      } else {
        return thisSpEntityId.compareTo(thatSpEntityId);
      }
    }
    return thisIdpEntityId.compareTo(thatIdpEntityId);

  }

}
