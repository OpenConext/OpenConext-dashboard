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

import selfservice.domain.CoinAuthority.Authority;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

import static selfservice.domain.CoinAuthority.Authority.*;

/**
 * Simple conext user
 */
@SuppressWarnings("serial")
public class CoinUser implements UserDetails {

  private String uid;
  private String displayName;
  private String schacHomeOrganization;
  private InstitutionIdentityProvider currentIdp;
  private InstitutionIdentityProvider switchedToIdp;
  private List<InstitutionIdentityProvider> institutionIdps = new ArrayList<>();
  private String institutionId;
  private String email;
  private Set<CoinAuthority> grantedAuthorities = new HashSet<>();
  private Map<String, List<String>> attributeMap = new HashMap<>();

  /**
   * It is not allowed to call this method {@inheritDoc}
   */
  @Override
  @JsonIgnore
  public String getPassword() {
    return "";
  }

  /**
   * Same value as {@link #getUid()}
   * <p/>
   * {@inheritDoc}
   */
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

  public boolean isSuperUser() {
    return hasAuthority(new CoinAuthority(ROLE_DASHBOARD_SUPER_USER));
  }

  public boolean isDashboardAdmin() {
    return hasAuthority(new CoinAuthority(ROLE_DASHBOARD_ADMIN));
  }

  public boolean isDashboardViewer() {
    return hasAuthority(new CoinAuthority(ROLE_DASHBOARD_VIEWER));
  }

  /**
   * Unique identifier of the user, e.g. urn:collab:person:org.example:john.doe
   *
   * @return unique identifier of the user
   */
  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  /**
   * Display name, e.g. 'John S. Doe Jr'
   *
   * @return display name of the user
   */
  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  /**
   * @return schac home organization of the user
   */
  public String getSchacHomeOrganization() {
    return schacHomeOrganization;
  }

  public void setSchacHomeOrganization(String schacHomeOrganization) {
    this.schacHomeOrganization = schacHomeOrganization;
  }

  /**
   * @return email address of the user
   */
  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * Returns a collection that will contain {@link CoinAuthority}'s
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return grantedAuthorities;
  }

  public void setAuthorities(Set<CoinAuthority> grantedAuthorities) {
    this.grantedAuthorities = grantedAuthorities;
  }

  public void addAuthority(CoinAuthority grantedAuthority) {
    this.grantedAuthorities.add(grantedAuthority);
  }

  public void removeAuthority(CoinAuthority grantedAuthority) {
    this.grantedAuthorities.remove(grantedAuthority);
  }

  public boolean hasAuthority(CoinAuthority grantedAuthority) {
    return this.grantedAuthorities.contains(grantedAuthority);
  }

  /**
   * List of {@link InstitutionIdentityProvider}'s of the institution for this users.
   * Usually contains only the IdP the user logs in with.
   *
   * @return List of {@link InstitutionIdentityProvider}'s
   */
  public List<InstitutionIdentityProvider> getInstitutionIdps() {
    return institutionIdps;
  }

  public void addInstitutionIdp(InstitutionIdentityProvider idp) {
    this.institutionIdps.add(idp);
  }

  /**
   * Identifier of the institution the IdentityProvider of the user belongs to.
   * Can be empty.
   *
   * @return Identifier of the institution the IdentityProvider of the user
   * belongs to
   */
  public String getInstitutionId() {
    return institutionId;
  }

  public void setInstitutionId(String institutionId) {
    this.institutionId = institutionId;
  }

  /**
   * Identifier of the IdentityProvider the user has logged in with
   *
   * @return Identifier of the IdentityProvider the user has logged in with
   */
  public InstitutionIdentityProvider getIdp() {
    return currentIdp;
  }

  public void setIdp(InstitutionIdentityProvider idp) {
    this.currentIdp = idp;
  }


  public InstitutionIdentityProvider getSwitchedToIdp() {
    return switchedToIdp;
  }

  public void setSwitchedToIdp(InstitutionIdentityProvider switchedToIdp) {
    this.switchedToIdp = switchedToIdp;
  }

  /**
   * Map of user attributes, key as String, value Object
   *
   * @return Map of user attributes
   */
  public Map<String, List<String>> getAttributeMap() {
    return attributeMap;
  }

  public void setAttributeMap(Map<String, List<String>> attributeMap) {
    this.attributeMap = attributeMap;
  }

  public void addAttribute(String key, List<String> value) {
    this.attributeMap.put(key, value);
  }

  public List<Authority> getAuthorityEnums() {
    List<Authority> result = new ArrayList<Authority>();
    for (CoinAuthority authority : this.grantedAuthorities) {
      result.add(authority.getEnumAuthority());
    }
    return result;
  }

  public Optional<InstitutionIdentityProvider> getByEntityId(String entityId) {
    for (InstitutionIdentityProvider institutionIdentityProvider : getInstitutionIdps()) {
      if (institutionIdentityProvider.getId().equals(entityId)) {
        return Optional.of(institutionIdentityProvider);
      }
    }
    return Optional.empty();
  }

  @Override
  public String toString() {
    return "CoinUser [uid=" + uid + ", displayName=" + displayName + ", schacHomeOrganization=" + schacHomeOrganization + ", idp=" + currentIdp
      + ", institutionIdps=" + institutionIdps + ", institutionId=" + institutionId + ", email=" + email + ", grantedAuthorities="
      + new ArrayList<CoinAuthority>(grantedAuthorities) + ", attributeMap=" + attributeMap + "]";
  }

}