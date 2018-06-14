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

import java.util.EnumSet;

import com.google.common.base.MoreObjects;

import org.springframework.security.core.GrantedAuthority;

/**
 * Simple implementation of a {@link GrantedAuthority}
 */
@SuppressWarnings("serial")
public class CoinAuthority implements GrantedAuthority {

  private final Authority authority;

  public enum Authority {
    ROLE_DASHBOARD_ADMIN,
    ROLE_DASHBOARD_VIEWER,
    ROLE_DASHBOARD_SUPER_USER,

    ROLE_DISTRIBUTION_CHANNEL_ADMIN;

    private static final EnumSet<Authority> dashboardAuthorities = EnumSet.of(ROLE_DASHBOARD_ADMIN, ROLE_DASHBOARD_SUPER_USER, ROLE_DASHBOARD_VIEWER);

    public boolean isDashboardAuthority() {
      return dashboardAuthorities.contains(this);
    }

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
    return MoreObjects.toStringHelper(this)
      .add("authority", authority)
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
