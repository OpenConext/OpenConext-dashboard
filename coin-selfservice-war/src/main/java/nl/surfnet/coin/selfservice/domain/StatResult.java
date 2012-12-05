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
  private String idpEntityIdp;

  public StatResult(String spEntityId, String spName, long millis, Integer logins, String idpEntityIdp) {
    this.spEntityId = spEntityId;
    this.spName = spName;
    this.millis = millis;
    this.logins = logins;
    this.idpEntityIdp = idpEntityIdp;
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

  public String getIdpEntityIdp() {
    return idpEntityIdp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    StatResult that = (StatResult) o;

    if (millis != that.millis) {
      return false;
    }
    if (logins != null ? !logins.equals(that.logins) : that.logins != null) {
      return false;
    }
    if (spEntityId != null ? !spEntityId.equals(that.spEntityId) : that.spEntityId != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = spEntityId != null ? spEntityId.hashCode() : 0;
    result = 31 * result + (Long.valueOf(millis).hashCode());
    result = 31 * result + (logins != null ? logins.hashCode() : 0);
    return result;
  }

  @Override
  public int compareTo(StatResult that) {
    if (this == that) {
      return 0;
    }

    final String thisSP = this.getSpEntityId();
    final String thatSP = that.getSpEntityId();

    if (thisSP.equals(thatSP)) {
      return millis < that.millis ? -1 : (millis==that.millis ? 0 : 1) ;
    } else {
      return thisSP.compareTo(thatSP);
    }
  }

}
