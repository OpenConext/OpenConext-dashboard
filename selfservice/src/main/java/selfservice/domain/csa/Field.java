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


import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import selfservice.util.DomainObject;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Field.java
 * 
 */
@SuppressWarnings("serial")
@MappedSuperclass
public abstract class Field extends DomainObject implements Comparable<Field> {

  @Column(name = "field_source")
  private Source source;

  @Column(name = "field_key")
  private Key key;

  @ManyToOne
  @JoinColumn(name = "compound_service_provider_id", nullable = false)
  private CompoundServiceProvider compoundServiceProvider;
  
  @Transient
  private Boolean availableInSurfMarket;
  
  @Transient
  private Boolean availableInSurfConext;

  @Transient
  private Map<Key, String> technicalOriginsLMNG = new HashMap<Key, String>() {
    {
      // CRM specific values
      put(Key.SERVICE_DESCRIPTION_NL,       "In SurfMarket CRM: artikel.lmng_description");
      put(Key.DETAIL_LOGO,                  "In SurfMarket CRM: image.lmng_url");
      put(Key.INSTITUTION_DESCRIPTION_NL,   "In SurfMarket CRM: lmng_descriptionlong");
      put(Key.ENDUSER_DESCRIPTION_NL,       "In SurfMarket CRM: lmng_surfspotdescriptionlong");
    }
  };
      
  @Transient
  private Map<Key, String> technicalOriginsSurfConext = new HashMap<Key, String>() {
    {
      // SURFCONEXT specific values
      put(Key.SERVICE_DESCRIPTION_NL,       "In Service Registry: descriptions:nl");
      put(Key.SERVICE_DESCRIPTION_EN,       "In Service Registry: descriptions:en");
      put(Key.APP_URL,                      "In Service Registry: applicationUrl");
      put(Key.DETAIL_LOGO,                  "In Service Registry: appLogoUrl");
      put(Key.SERVICE_URL,                  "In Service Registry: OrganizationURL");
      put(Key.EULA_URL,                     "In Service Registry: coin:eula");
      put(Key.SUPPORT_URL_NL,               "In Service Registry: url:nl");
      put(Key.SUPPORT_URL_EN,               "In Service Registry: url:en");
      put(Key.SUPPORT_MAIL,                 "In Service Registry: contact type support");
      put(Key.TECHNICAL_SUPPORTMAIL,        "In Service Registry: contact type technical");
      put(Key.APPSTORE_LOGO,                "In Serivce Registry: logo:0:url");
      put(Key.TITLE_EN,                     "In Service Registry: names:en");
      put(Key.TITLE_NL,                     "In Service Registry: names:nl");
      put(Key.INTERFED_SOURCE,              "In Service Registry: coin:interfed_source");
      put(Key.PRIVACY_STATEMENT_URL_EN,     "In Service Registry: mdui:PrivacyStatementURL:en");
      put(Key.PRIVACY_STATEMENT_URL_NL,     "In Service Registry: mdui:PrivacyStatementURL:nl");
      put(Key.REGISTRATION_INFO_URL,            "In Service Registry: mdrpi:RegistrationInfo");
      put(Key.REGISTRATION_POLICY_URL_EN,   "In Service Registry: mdrpi:RegistrationPolicy:en");
      put(Key.REGISTRATION_POLICY_URL_NL,   "In Service Registry: mdrpi:RegistrationPolicy:nl");
    }
  };

  public Field() {
    super();
  }

  public Field(Source source, Key key, CompoundServiceProvider compoundServiceProvider) {
    super();
    this.source = source;
    this.key = key;
    this.compoundServiceProvider = compoundServiceProvider;
  }

  public enum Source {
    LMNG, SURFCONEXT, DISTRIBUTIONCHANNEL
  }

  /**
   * These enum values are stored in the database by their ordinal. So be careful not to touch the order of the declaration.
   */
  public enum Key {
    APPSTORE_LOGO,

    APP_URL,

    DETAIL_LOGO,

    ENDUSER_DESCRIPTION_EN,

    ENDUSER_DESCRIPTION_NL,

    EULA_URL,

    INSTITUTION_DESCRIPTION_EN,

    INSTITUTION_DESCRIPTION_NL,

    SERVICE_DESCRIPTION_EN,

    SERVICE_DESCRIPTION_NL,

    SCREENSHOT,

    SERVICE_URL,

    SUPPORT_MAIL,

    SUPPORT_URL_NL,

    SUPPORT_URL_EN,

    TECHNICAL_SUPPORTMAIL,
    
    TITLE_EN,
    
    TITLE_NL,

    WIKI_URL_EN,

    WIKI_URL_NL,
    
    INTERFED_SOURCE,
    
    PRIVACY_STATEMENT_URL_EN,
    
    PRIVACY_STATEMENT_URL_NL,
    
    REGISTRATION_INFO_URL,
    
    REGISTRATION_POLICY_URL_EN,
    
    REGISTRATION_POLICY_URL_NL;
  }

  public Source getSource() {
    return source;
  }

  public void setSource(Source source) {
    this.source = source;
  }

  public Key getKey() {
    return key;
  }

  public void setKey(Key key) {
    this.key = key;
  }

  public CompoundServiceProvider getCompoundServiceProvider() {
    return compoundServiceProvider;
  }

  public void setCompoundServiceProvider(CompoundServiceProvider compoundServiceProvider) {
    this.compoundServiceProvider = compoundServiceProvider;
  }
  
  public String getTechnicalOriginLmng() {
    String result = "";
    if (null != technicalOriginsLMNG.get(this.key)) {
      result = technicalOriginsLMNG.get(this.key);
    }
    return result;
  }
  
  public String getTechnicalOriginSurfConext() {
    String result = "";
    if (null != technicalOriginsSurfConext.get(this.key)) {
      result = technicalOriginsSurfConext.get(this.key);
    }
    return result;
  }
  
  public abstract boolean isUnset();

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .appendSuper(super.toString())
      .append("source", source)
      .append("key", key)
      .toString();
  }

  @Override
  public int compareTo(Field that) {
    return new CompareToBuilder()
      .append(this.key, that.key)
      .toComparison();
  }

  /**
   * @return the availableInSurfMarket
   */
  public Boolean getAvailableInSurfMarket() {
    return availableInSurfMarket;
  }

  /**
   * @param availableInSurfMarket the availableInSurfMarket to set
   */
  public void setAvailableInSurfMarket(Boolean availableInSurfMarket) {
    this.availableInSurfMarket = availableInSurfMarket;
  }

  /**
   * @return the availableInSurfConext
   */
  public Boolean getAvailableInSurfConext() {
    return availableInSurfConext;
  }

  /**
   * @param availableInSurfConext the availableInSurfConext to set
   */
  public void setAvailableInSurfConext(Boolean availableInSurfConext) {
    this.availableInSurfConext = availableInSurfConext;
  }
}
