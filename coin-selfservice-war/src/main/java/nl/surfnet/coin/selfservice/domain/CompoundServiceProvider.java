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

import java.util.HashSet;
import java.util.Set;
import static nl.surfnet.coin.selfservice.domain.Field.Key.*;
import static nl.surfnet.coin.selfservice.domain.Field.Source.*;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import nl.surfnet.coin.selfservice.domain.Field.Key;
import nl.surfnet.coin.shared.domain.DomainObject;

import org.hibernate.annotations.Proxy;
import org.springframework.beans.BeanUtils;
import org.springframework.util.Assert;

/**
 * CompoundServiceProvider.java
 * 
 */
@SuppressWarnings("serial")
@Entity
@Proxy(lazy = false)
public class CompoundServiceProvider extends DomainObject {

  @Transient
  private ServiceProvider sp;

  @Transient
  private License ls;

  @Column
  private String serviceProviverEntityId;

  @Column
  private String lmngId;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "compoundServiceProvider")
  @JoinTable
  private Set<FieldString> fields = new HashSet<FieldString>();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "compoundServiceProvider")
  @JoinTable
  private Set<FieldImage> fieldImages = new HashSet<FieldImage>();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "compoundServiceProvider")
  @JoinTable
  private Set<FieldImage> screenShotsImages = new HashSet<FieldImage>();

  public Set<FieldString> getFields() {
    return fields;
  }

  public void setFields(Set<FieldString> fields) {
    this.fields = fields;
  }

  public Set<FieldImage> getFieldImages() {
    return fieldImages;
  }

  public void setFieldImages(Set<FieldImage> fieldImages) {
    this.fieldImages = fieldImages;
  }

  public Set<FieldImage> getScreenShotsImages() {
    return screenShotsImages;
  }

  public void setScreenShotsImages(Set<FieldImage> screenShotsImages) {
    this.screenShotsImages = screenShotsImages;
  }

  public ServiceProvider getSp() {
    return sp;
  }

  public License getLs() {
    return ls;
  }

  public String getServiceProviverEntityId() {
    return serviceProviverEntityId;
  }

  public String getLmngId() {
    return lmngId;
  }

  public String getInstitutionDescriptionNl() {
    return getFieldStringValue(INSTITUTION_DESCRIPTION_NL);
  }

  public String getInstitutionDescriptionEn() {
    return getFieldStringValue(INSTITUTION_DESCRIPTION_EN);
  }

  public String getEnduserDescriptionNl() {
    return getFieldStringValue(ENDUSER_DESCRIPTION_NL);
  }

  public String getEnduserDescriptionEn() {
    return getFieldStringValue(ENDUSER_DESCRIPTION_EN);
  }

  public byte[] getAppStoreLogo() {
    return null;
  }

  public byte[] getDetailLogo() {
    return null;
  }

  public String getAppUrl() {
    return null;
  }

  public Set<byte[]> getScreenshots() {
    return null;
  }

  public String getServiceUrl() {
    return null;
  }

  public String getSupportUrl() {
    return null;
  }

  public String getEulaUrl() {
    return null;
  }

  public String getSupportMail() {
    return null;
  }

  public String getTechnicalSupportMail() {
    return null;
  }

  public boolean addFieldString(FieldString f) {
    Assert.notNull(f);
    f.setCompoundServiceProvider(this);
    return this.fields.add(f);
  }

  public boolean addFieldImage(FieldImage f) {
    Assert.notNull(f);
    f.setCompoundServiceProvider(this);
    return this.fieldImages.add(f);
  }

  public boolean addScreenShot(FieldImage f) {
    Assert.notNull(f);
    f.setCompoundServiceProvider(this);
    return this.screenShotsImages.add(f);
  }

  /*
   * Note that we could use reflection and create a very generic framework.
   * However this puts to many constraints on the naming convention of unrelated
   * Objects (e.g. Service Provider and License). Therefore we have chosen to
   * explicitly retrieve values.
   */
  private String getFieldStringValue(Field.Key key) {
    Assert.notNull(key);
    for (FieldString f : this.fields) {
      if (key.equals(f.getKey())) {
        switch (f.getSource()) {
        case LMNG:
          return getLmngProperty(key);
         
        case SURFCONEXT:
          
          break;
        case DISTRIBUTIONCHANNEL:

          break;

        default:
          throw new RuntimeException("Unknow Source ('" + f.getSource() + "')");
        }
      }
    }
    return null;
    //throw new RuntimeException("Unset key for ('" + this + "'");
  }

  private String getLmngProperty(Key key) {
    /*
     *     APPSTORE_LOGO,

    APP_URL,

    DETAIL_LOGO,

    ENDUSER_DESCRIPTION_EN,

    ENDUSER_DESCRIPTION_NL,

    EULA_URL,

    INSTITUTION_DESCRIPTION_EN,

    SCREENSHOT,

    SERVICE_URL,

    SUPPORT_MAIL,

    SUPPORT_URL,

    TECHNICAL_SUPPORTMAIL,

    INSTITUTION_DESCRIPTION_NL;

     */
//    switch (key) {
//    case value:
//      
//      break;
//
//    default:
//      break;
//    }
    return null;
  }

  @Override
  public String toString() {
    return "CompoundServiceProvider [serviceProviverEntityId=" + serviceProviverEntityId + ", lmngId=" + lmngId + ", fields=" + fields
        + ", fieldImages=" + fieldImages + ", screenShotsImages=" + screenShotsImages + ", getId()=" + getId() + "]";
  }

}
