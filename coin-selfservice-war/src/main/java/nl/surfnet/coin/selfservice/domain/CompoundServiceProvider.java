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

import static nl.surfnet.coin.selfservice.domain.Field.Key.APPSTORE_LOGO;
import static nl.surfnet.coin.selfservice.domain.Field.Key.APP_URL;
import static nl.surfnet.coin.selfservice.domain.Field.Key.DETAIL_LOGO;
import static nl.surfnet.coin.selfservice.domain.Field.Key.ENDUSER_DESCRIPTION_EN;
import static nl.surfnet.coin.selfservice.domain.Field.Key.ENDUSER_DESCRIPTION_NL;
import static nl.surfnet.coin.selfservice.domain.Field.Key.EULA_URL;
import static nl.surfnet.coin.selfservice.domain.Field.Key.INSTITUTION_DESCRIPTION_EN;
import static nl.surfnet.coin.selfservice.domain.Field.Key.INSTITUTION_DESCRIPTION_NL;
import static nl.surfnet.coin.selfservice.domain.Field.Key.SERVICE_DESCRIPTION_EN;
import static nl.surfnet.coin.selfservice.domain.Field.Key.SERVICE_DESCRIPTION_NL;
import static nl.surfnet.coin.selfservice.domain.Field.Key.SERVICE_URL;
import static nl.surfnet.coin.selfservice.domain.Field.Key.SUPPORT_MAIL;
import static nl.surfnet.coin.selfservice.domain.Field.Key.SUPPORT_URL;
import static nl.surfnet.coin.selfservice.domain.Field.Key.TECHNICAL_SUPPORTMAIL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import nl.surfnet.coin.selfservice.domain.Field.Key;
import nl.surfnet.coin.selfservice.domain.Provider.Language;
import nl.surfnet.coin.shared.domain.DomainObject;

import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
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
  private ServiceProvider serviceProvider;

  @Transient
  private License license;

  @Column
  private String serviceProviderEntityId;

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
  @Sort(type = SortType.NATURAL)
  private List<FieldImage> screenShotsImages = new ArrayList<FieldImage>();

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

  public List<FieldImage> getScreenShotsImages() {
    return screenShotsImages;
  }

  public void setScreenShotsImages(List<FieldImage> screenShotsImages) {
    this.screenShotsImages = screenShotsImages;
  }

  public ServiceProvider getSp() {
    return serviceProvider;
  }

  public License getLs() {
    return license;
  }

  public String getServiceProviderEntityId() {
    return serviceProviderEntityId;
  }

  public String getLmngId() {
    return lmngId;
  }

  public String getServiceDescriptionNl() {
    return (String) getFieldValue(SERVICE_DESCRIPTION_NL);
  }

  public String getServiceDescriptionEn() {
    return (String) getFieldValue(SERVICE_DESCRIPTION_EN);
  }

  
  public String getInstitutionDescriptionNl() {
    return (String) getFieldValue(INSTITUTION_DESCRIPTION_NL);
  }

  public String getInstitutionDescriptionEn() {
    return (String) getFieldValue(INSTITUTION_DESCRIPTION_EN);
  }

  public String getEnduserDescriptionNl() {
    return (String) getFieldValue(ENDUSER_DESCRIPTION_NL);
  }

  public String getEnduserDescriptionEn() {
    return (String) getFieldValue(ENDUSER_DESCRIPTION_EN);
  }

  public byte[] getAppStoreLogo() {
    return (byte[]) getFieldValue(APPSTORE_LOGO);
  }

  public byte[] getDetailLogo() {
    return (byte[]) getFieldValue(DETAIL_LOGO);
  }

  public String getAppUrl() {
    return (String) getFieldValue(APP_URL);
  }

  public List<byte[]> getScreenshots() {
    List<byte[]> result = new ArrayList<byte[]>();
    for (FieldImage fi : screenShotsImages) {
      result.add(fi.getImage());
    }
    return result;
  }

  public String getServiceUrl() {
    return (String) getFieldValue(SERVICE_URL);
  }

  public String getSupportUrl() {
    return (String) getFieldValue(SUPPORT_URL);
  }

  public String getEulaUrl() {
    return (String) getFieldValue(EULA_URL);
  }

  public String getSupportMail() {
    return (String) getFieldValue(SUPPORT_MAIL);
  }

  public String getTechnicalSupportMail() {
    return (String) getFieldValue(TECHNICAL_SUPPORTMAIL);
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
  private Object getFieldValue(Field.Key key) {
    Assert.notNull(key);
    for (FieldString f : this.fields) {
      if (key.equals(f.getKey())) {
        switch (f.getSource()) {
        case LMNG:
          return getLmngProperty(key);
        case SURFCONEXT:
          return getSurfConextProperty(key);
        case DISTRIBUTIONCHANNEL:
          return getDistributionChannelProperty(f);
        default:
          throw new RuntimeException("Unknow Source ('" + f.getSource() + "')");
        }
      }
    }
    for (FieldImage f : this.fieldImages) {
      if (key.equals(f.getKey())) {
        switch (f.getSource()) {
        case LMNG:
          return getLmngProperty(key);
        case SURFCONEXT:
          return getSurfConextProperty(key);
        case DISTRIBUTIONCHANNEL:
          return getDistributionChannelProperty(f);
        default:
          throw new RuntimeException("Unknow Source ('" + f.getSource() + "')");
        }
      }
    }
    throw new RuntimeException("Unset key for ('" + this + "'");
  }

  private Object getDistributionChannelProperty(Field field) {
    if (field instanceof FieldImage) {
      return ((FieldImage) field).getImage();
    }
    if (field instanceof FieldString) {
      return ((FieldString) field).getValue();
    }
    throw new RuntimeException("Unknown Field class: " + field.getClass());
  }

  private Object getSurfConextProperty(Key key) {
    switch (key) {
    case ENDUSER_DESCRIPTION_NL:
      return this.serviceProvider.getDescription(Language.NL);
    case ENDUSER_DESCRIPTION_EN:
      return this.serviceProvider.getDescription(Language.EN);
    case SERVICE_DESCRIPTION_NL:
      return this.serviceProvider.getName(Language.NL);
    case SERVICE_DESCRIPTION_EN:
      return this.serviceProvider.getName(Language.EN);
    case DETAIL_LOGO:
      return new FieldImage(this.serviceProvider.getLogoUrl()).getImageBytes();
    case APP_URL:
      return this.serviceProvider.getHomeUrl();
    case SERVICE_URL:
      return this.serviceProvider.getUrl();
    case SUPPORT_URL:
      return this.serviceProvider.getUrl();
    case SUPPORT_MAIL:
      ContactPerson helpCP = this.serviceProvider.getContactPerson(ContactPersonType.help);
      return helpCP != null ? helpCP.getEmailAddress() : null;
    case TECHNICAL_SUPPORTMAIL:
      ContactPerson cp = this.serviceProvider.getContactPerson(ContactPersonType.technical);
      return cp != null ? cp.getEmailAddress() : null;
    case EULA_URL:
      return this.serviceProvider.getEulaURL();
    default:
      throw new RuntimeException("SURFConext does not support property: " + key);
    }
  }

  private Object getLmngProperty(Key key) {
    switch (key) {
    case ENDUSER_DESCRIPTION_NL:
      return this.license.getEndUserDescription();
    case INSTITUTION_DESCRIPTION_NL:
      return this.license.getDescription();
    case SERVICE_DESCRIPTION_NL:
      return this.license.getProductName();
    case DETAIL_LOGO:
      return new FieldImage(this.license.getDetailLogo()).getImageBytes();
    default:
      throw new RuntimeException("LMNG does not support property: " + key);
    }
  }

  @Override
  public String toString() {
    return "CompoundServiceProvider [serviceProviderEntityId=" + serviceProviderEntityId + ", lmngId=" + lmngId + ", fields=" + fields
        + ", fieldImages=" + fieldImages + ", screenShotsImages=" + screenShotsImages + ", getId()=" + getId() + "]";
  }

}
