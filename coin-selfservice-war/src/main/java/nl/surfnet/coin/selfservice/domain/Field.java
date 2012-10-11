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

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

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
public abstract class Field extends DomainObject implements Comparable {

  @Column(name = "field_source")
  private Source source;

  @Column(name = "field_key")
  private Key key;

  @ManyToOne
  @JoinColumn(name = "compound_service_provider_id", nullable = false)
  private CompoundServiceProvider compoundServiceProvider;

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
    APPSTORE_LOGO(true),

    APP_URL(false),

    DETAIL_LOGO(true),

    ENDUSER_DESCRIPTION_EN(false),

    ENDUSER_DESCRIPTION_NL(false),

    EULA_URL(false),

    INSTITUTION_DESCRIPTION_EN(false),

    INSTITUTION_DESCRIPTION_NL(false),
 
    SERVICE_DESCRIPTION_EN(false),

    SERVICE_DESCRIPTION_NL(false),

    SCREENSHOT(true),

    SERVICE_URL(false),

    SUPPORT_MAIL(false),

    SUPPORT_URL(false),

    TECHNICAL_SUPPORTMAIL(false);
    
    private boolean image;
    
    Key(boolean image) {
      this.image = image;
    }
    
    public boolean isImage() {
      return image;
    }
    
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

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .appendSuper(super.toString())
      .append("source", source)
      .append("key", key)
      .toString();
  }

  @Override
  public int compareTo(Object o) {
    Field that = (Field) o;
    return new CompareToBuilder()
      .append(this.key, that.key)
      .toComparison();
  }
}
