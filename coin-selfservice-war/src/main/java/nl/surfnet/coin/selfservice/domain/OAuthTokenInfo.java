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

import java.io.Serializable;

/**
 * Basic information about an OAuth token
 */
public class OAuthTokenInfo implements Serializable {

  private static final long serialVersionUID = -2844848947914282227L;

  private String id;
  private String userId;
  private String consumerKey;
  private String source;


  public OAuthTokenInfo(String id, String source) {
    this.id = id;
    this.source = source;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getConsumerKey() {
    return consumerKey;
  }

  public void setConsumerKey(String consumerKey) {
    this.consumerKey = consumerKey;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append("OAuthTokenInfo");
    sb.append("{id='").append(id).append('\'');
    sb.append(", userId='").append(userId).append('\'');
    sb.append(", consumerKey='").append(consumerKey).append('\'');
    sb.append(", source='").append(source).append('\'');
    sb.append('}');
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    OAuthTokenInfo that = (OAuthTokenInfo) o;

    if (id != null ? !id.equals(that.id) : that.id != null) {
      return false;
    }
    if (source != null ? !source.equals(that.source) : that.source != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (source != null ? source.hashCode() : 0);
    return result;
  }
}
