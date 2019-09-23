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

package dashboard.domain;

import static dashboard.domain.CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN;
import static dashboard.domain.CoinAuthority.Authority.ROLE_DASHBOARD_SUPER_USER;
import static dashboard.domain.CoinAuthority.Authority.ROLE_DASHBOARD_VIEWER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.util.StringUtils;
import dashboard.domain.CoinAuthority.Authority;
import dashboard.shibboleth.ShibbolethHeader;

@SuppressWarnings("serial")
public class CoinUser implements UserDetails {

    private String uid;
    private String displayName;
    private String schacHomeOrganization;
    private IdentityProvider currentIdp;
    private IdentityProvider switchedToIdp;
    private List<IdentityProvider> institutionIdps = new ArrayList<>();
    private String institutionId;
    private String email;
    private boolean manageConsentEnabled;
    private Set<CoinAuthority> grantedAuthorities = new HashSet<>();
    private Map<ShibbolethHeader, List<String>> attributeMap = new HashMap<>();
    private String hideTabs;
    private String supportedLanguages;
    private String organization;
    private boolean oidcEnabled;

    @Override
    @JsonIgnore
    public String getPassword() {
        return "";
    }

    /**
     * Same value as {@link #getUid()}
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

    public String getFriendlyName() {
        return StringUtils.hasText(this.displayName) ? this.displayName : this.uid;
    }

    /**
     * List of {@link InstitutionIdentityProvider}'s of the institution for this users.
     * Usually contains only the IdP the user logs in with.
     *
     * @return List of {@link InstitutionIdentityProvider}'s
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
    public IdentityProvider getIdp() {
        return currentIdp;
    }

    public void setIdp(IdentityProvider idp) {
        this.currentIdp = idp;
    }

    public Optional<IdentityProvider> getSwitchedToIdp() {
        return Optional.ofNullable(switchedToIdp);
    }

    public void setSwitchedToIdp(IdentityProvider switchedToIdp) {
        this.switchedToIdp = switchedToIdp;
    }

    /**
     * Map of user attributes, key as String, value Object
     *
     * @return Map of user attributes
     */
    public Map<ShibbolethHeader, List<String>> getAttributeMap() {
        return attributeMap;
    }

    public void setAttributeMap(Map<ShibbolethHeader, List<String>> attributeMap) {
        this.attributeMap = attributeMap;
    }

    public void addAttribute(ShibbolethHeader key, List<String> value) {
        this.attributeMap.put(key, value);
    }

    public List<Authority> getAuthorityEnums() {
        return grantedAuthorities.stream().map(CoinAuthority::getEnumAuthority).collect(Collectors.toList());
    }

    public Optional<IdentityProvider> getByEntityId(String entityId) {
        return getInstitutionIdps().stream().filter(iip -> iip.getId().equals(entityId)).findFirst();
    }

    public boolean isManageConsentEnabled() {
        return manageConsentEnabled;
    }

    public void setManageConsentEnabled(boolean manageConsentEnabled) {
        this.manageConsentEnabled = manageConsentEnabled;
    }

    public void setHideTabs(String hideTabs) {
        this.hideTabs = hideTabs;
    }

    public boolean isOidcEnabled() {
        return oidcEnabled;
    }

    public void setOidcEnabled(boolean isOidcEnabled) {
        this.oidcEnabled = isOidcEnabled;
    }

    public String getSupportedLanguages() {
        return supportedLanguages;
    }

    public void setSupportedLanguages(String supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }

    public String getHideTabs() {
        return hideTabs;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getOrganization() {
        return organization;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uid", uid)
                .add("displayName", displayName)
                .add("schacHomeOrganization", schacHomeOrganization)
                .add("idp", currentIdp)
                .add("institutionIdps", institutionIdps)
                .add("institutionId", institutionId)
                .add("email", email)
                .add("grantedAuthorities", grantedAuthorities)
                .add("attributeMap", attributeMap)
                .add("switchedToIdp", switchedToIdp).toString();
    }

}
