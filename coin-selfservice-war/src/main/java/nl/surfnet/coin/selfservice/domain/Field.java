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

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import nl.surfnet.coin.shared.domain.DomainObject;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

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
  private Map<String,String> technicalOrigins = new HashMap<String, String>(){
    {
      // CRM specific values
      put("LMNG_SERVICE_DESCRIPTION_NL", "artikel.lmng_description");
      put("LMNG_DETAIL_LOGO", "image.lmng_url");
      put("LMNG_INSTITUTION_DESCRIPTION", "lmng_descriptionlong");
      
      // SURFCONEXT specific values
      put("SURFCONEXT_SERVICE_DESCRIPTION_NL", "names:nl");
      put("SURFCONEXT_SERVICE_DESCRIPTION_EN", "names:en");
      put("SURFCONEXT_APP_URL", "applicationUrl");
      put("SURFCONEXT_DETAIL_LOGO", "appLogoUrl");
      put("SURFCONEXT_SERVICE_URL","OrganizationURL");

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

    TECHNICAL_SUPPORTMAIL;
    
    
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
  
  public String getTechnicalOrigin(final String source) {
    String result = "";
    if (null != technicalOrigins.get(source+"_"+this.key)) {
      result = technicalOrigins.get(source+"_"+this.key);
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
