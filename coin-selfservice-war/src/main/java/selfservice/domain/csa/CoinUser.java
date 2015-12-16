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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;

@SuppressWarnings("serial")
public class CoinUser implements UserDetails {

  private String uid;
  private String displayName;
  private IdentityProvider idp;
  private List<IdentityProvider> institutionIdps = new ArrayList<>();
  private String institutionId;
  private String email;
  private List<CoinAuthority> grantedAuthorities = new ArrayList<>();

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
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  /**
   * Unique identifier of the user, e.g. urn:collab:person:org.example:john.doe
   */
  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  /**
   * Display name, e.g. 'John S. Doe Jr'
   */
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public Collection<CoinAuthority> getAuthorities() {
    return grantedAuthorities;
  }

  public void setAuthorities(List<CoinAuthority> grantedAuthorities) {
    this.grantedAuthorities = grantedAuthorities;
  }

  public void addAuthority(CoinAuthority grantedAuthority) {
    this.grantedAuthorities.add(grantedAuthority);
  }

  /**
   * List of {@link IdentityProvider}'s of the institution for this users.
   * Usually contains only the IdP the user logs in with.
   */
  public List<IdentityProvider> getInstitutionIdps() {
    return institutionIdps;
  }

  public void addInstitutionIdp(IdentityProvider idp) {
    this.institutionIdps.add(idp);
  }

  /**
   * Identifier of the institution the IdentityProvider of the user belongs to.
   * Can be empty.
   */
  public String getInstitutionId() {
    return institutionId;
  }

  public void setInstitutionId(String institutionId) {
    this.institutionId = institutionId;
  }

  /**
   * Identifier of the IdentityProvider the user has logged in with
   */
  public IdentityProvider getIdp() {
    return idp;
  }

  public void setIdp(IdentityProvider idp) {
    this.idp = idp;
  }

  public List<CoinAuthority.Authority> getAuthorityEnums() {
    return this.grantedAuthorities.stream().map(CoinAuthority::getEnumAuthority).collect(Collectors.toList());
  }

  @Override
  public String toString() {
    return "CoinUser [uid=" + uid + ", displayName=" + displayName + ", idp=" + idp
      + ", institutionIdps=" + institutionIdps + ", institutionId=" + institutionId + ", email=" + email + ", grantedAuthorities="
      + new ArrayList<>(grantedAuthorities) + "]";
  }

}
