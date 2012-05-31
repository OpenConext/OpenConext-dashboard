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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Simple conext user
 */
public class CoinUser implements UserDetails{

  private String uid;
  private String displayName;
  private String schacHomeOrganization;
  private String idp;
  private List<String> institutionIdps = new ArrayList<String>();
  private String institutionId;
  private String email;


  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    GrantedAuthority ga = new GrantedAuthority() {
      @Override
      public String getAuthority() {
        return "ROLE_USER";
      }
    };
    List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
    grantedAuthorities.add(ga);
    return grantedAuthorities;
  }

  @Override
  public String getPassword() {
    throw new SecurityException("Self service interface does not contain passwords");
  }

  @Override
  public String getUsername() {
    return uid;
  }

  @Override
  public boolean isAccountNonExpired() {
    return false;
  }

  @Override
  public boolean isAccountNonLocked() {
    return false;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getSchacHomeOrganization() {
    return schacHomeOrganization;
  }

  public void setSchacHomeOrganization(String schacHomeOrganization) {
    this.schacHomeOrganization = schacHomeOrganization;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<String> getInstitutionIdps() {
    return institutionIdps;
  }

  public void addInstitutionIdp(String idp) {
    this.institutionIdps.add(idp);
  }

  public String getInstitutionId() {
    return institutionId;
  }

  public void setInstitutionId(String institutionId) {
    this.institutionId = institutionId;
  }

  public String getIdp() {
    return idp;
  }

  public void setIdp(String idp) {
    this.idp = idp;
  }
}
