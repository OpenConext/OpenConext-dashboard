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

package selfservice.domain.csa;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.security.core.GrantedAuthority;

/**
 * Simple implementation of a {@link GrantedAuthority}
 */
@SuppressWarnings("serial")
public class CoinAuthority implements GrantedAuthority {

  private final Authority authority;

  public enum Authority {
    ROLE_DISTRIBUTION_CHANNEL_ADMIN, NA;
  }

  public CoinAuthority(Authority authority) {
    this.authority = authority;
  }

  @Override
  public String getAuthority() {
    return authority.name();
  }

  public Authority getEnumAuthority() {
    return authority;
  }

  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
        .append("authority", authority)
        .toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CoinAuthority that = (CoinAuthority) o;

    if (authority != that.authority) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return authority != null ? authority.hashCode() : 0;
  }
}
