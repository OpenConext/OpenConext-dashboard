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

package selfservice.janus.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import selfservice.janus.Janus;

/**
 * Representation of an entity's metadata in Janus.
 */
public class EntityMetadata implements Serializable {

  private static final long serialVersionUID = -7846074224939621423L;
  private static final String LANG_EN = "en";
  private static final String LANG_NL = "nl";

  private Map<String, String> names = new HashMap<String, String>();
  private String oauthConsumerKey;
  private String oauthConsumerSecret;
  private String appTitle;
  private String appIcon;
  private String appDescription;
  private String appThumbNail;
  private String appEntityId;
  private String oauthCallbackUrl;
  private boolean twoLeggedOauthAllowed;
  private boolean consentNotRequired;
  private String appLogoUrl;
  private Map<String, String> appHomeUrls = new HashMap<String, String>();
  private String eula;
  private Map<String, String> descriptions = new HashMap<String, String>();
  private Map<String, String> urls = new HashMap<String, String>();
  private boolean isIdpVisibleOnly;
  private String workflowState;
  private List<Contact> contacts = new ArrayList<Contact>();
  private String instutionId;
  private String applicationUrl;
  private boolean publishedInEduGain;

  public static EntityMetadata fromMetadataMap(Map<String, Object> metadata) {
    EntityMetadata em = new EntityMetadata();

    em.addName(LANG_EN, convertToString(metadata, Janus.Metadata.NAME));
    em.addName(LANG_NL, convertToString(metadata,Janus.Metadata.NAME_NL));
    em.addDescription(LANG_EN, convertToString(metadata,Janus.Metadata.DESCRIPTION));
    em.addDescription(LANG_NL, convertToString(metadata,Janus.Metadata.DESCRIPTION_NL));

    em.setOauthConsumerSecret(convertToString(metadata,Janus.Metadata.OAUTH_SECRET));
    em.setOauthConsumerKey(convertToString(metadata,Janus.Metadata.OAUTH_CONSUMERKEY));
    em.setAppDescription(convertToString(metadata,Janus.Metadata.OAUTH_APPDESCRIPTION));
    em.setAppIcon(convertToString(metadata,Janus.Metadata.OAUTH_APPICON));
    em.setAppThumbNail(convertToString(metadata,Janus.Metadata.OAUTH_APPTHUMBNAIL));
    em.setAppTitle(convertToString(metadata,Janus.Metadata.OAUTH_APPTITLE));
    em.setOauthCallbackUrl(convertToString(metadata,Janus.Metadata.OAUTH_CALLBACKURL));

    em.addAppHomeUrl(LANG_EN, convertToString(metadata,Janus.Metadata.ORGANIZATION_URL));
    em.addAppHomeUrl(LANG_NL, convertToString(metadata,Janus.Metadata.ORGANIZATION_URL_NL));
    em.setAppLogoUrl(convertToString(metadata,Janus.Metadata.LOGO_URL));
    em.setEula(convertToString(metadata,Janus.Metadata.EULA));
    em.setInstutionId(convertToString(metadata,Janus.Metadata.INSITUTION_ID));
    em.setApplicationUrl(convertToString(metadata,Janus.Metadata.APPLICATION_URL));
    
    em.addUrl(LANG_EN, convertToString(metadata,Janus.Metadata.URL_EN));
    em.addUrl(LANG_NL, convertToString(metadata,Janus.Metadata.URL_NL));

    em.setWorkflowState(convertToString(metadata,Janus.Metadata.WORKFLOWSTATE));
    em.setTwoLeggedOauthAllowed(false);
    if (metadata.get(Janus.Metadata.OAUTH_TWOLEGGEDALLOWED.val()) != null) {
      em.setTwoLeggedOauthAllowed((Boolean) metadata.get(Janus.Metadata.OAUTH_TWOLEGGEDALLOWED.val()));
    }

    em.setConsentNotRequired(false);
    if (metadata.get(Janus.Metadata.OAUTH_CONSENTNOTREQUIRED.val()) != null) {
      em.setConsentNotRequired((Boolean) metadata.get(Janus.Metadata.OAUTH_CONSENTNOTREQUIRED.val()));
    }
    
    em.setIdpVisibleOnly(false);
    if (metadata.get(Janus.Metadata.SS_IDP_VISIBLE_ONLY.val()) != null) {
      em.setIdpVisibleOnly((Boolean) metadata.get(Janus.Metadata.SS_IDP_VISIBLE_ONLY.val()));
    }

    final Object c0Mail = metadata.get(Janus.Metadata.CONTACTS_0_EMAIL.val());
    if (metadata.get(Janus.Metadata.CONTACTS_0_TYPE.val()) != null &&
        !emptyString(c0Mail)) {
      Contact contact = getContact0(metadata, (String) c0Mail);
      em.addContact(contact);
    }

    final Object c1Mail = metadata.get(Janus.Metadata.CONTACTS_1_EMAIL.val());
    if (metadata.get(Janus.Metadata.CONTACTS_1_TYPE.val()) != null &&
        !emptyString(c1Mail)) {
      Contact contact = getContact1(metadata, (String) c1Mail);
      em.addContact(contact);
    }

    final Object c2Mail = metadata.get(Janus.Metadata.CONTACTS_2_EMAIL.val());
    if (metadata.get(Janus.Metadata.CONTACTS_2_TYPE.val()) != null &&
        !emptyString(c2Mail)) {
      Contact contact = getContact2(metadata, (String) c2Mail);
      em.addContact(contact);
    }
    Object publishedInEduGain = metadata.get(Janus.Metadata.PUBLISHED_EDU_GAIN.val());
    if (publishedInEduGain != null) {
      em.setPublishedInEduGain((Boolean) publishedInEduGain);
    }
    return em;
  }

  private static String convertToString(Map<String, Object> metadata, Janus.Metadata key ) {
    Object value = metadata.get(key.val());
    return value != null ? value.toString() : null;
  }

  private static boolean emptyString(Object o) {
    return !(o instanceof String) || "".equals(((String) o).trim());
  }

  private static Contact getContact0(Map<String, Object> metadata, String c0Mail) {
    Contact contact = new Contact();
    contact.setEmailAddress(c0Mail);
    contact.setGivenName((String) metadata.get(Janus.Metadata.CONTACTS_0_GIVENNAME.val()));
    contact.setSurName((String) metadata.get(Janus.Metadata.CONTACTS_0_SURNAME.val()));
    Object phone = metadata.get(Janus.Metadata.CONTACTS_0_TELEPHONE.val());
    contact.setTelephoneNumber(getPhoneAsString(phone));
    contact.setType(Contact.Type.valueOf((String) metadata.get(Janus.Metadata.CONTACTS_0_TYPE.val())));
    return contact;
  }

  private static Contact getContact1(Map<String, Object> metadata, String c1Mail) {
    Contact contact = new Contact();
    contact.setEmailAddress(c1Mail);
    contact.setGivenName((String) metadata.get(Janus.Metadata.CONTACTS_1_GIVENNAME.val()));
    contact.setSurName((String) metadata.get(Janus.Metadata.CONTACTS_1_SURNAME.val()));
    Object phone = metadata.get(Janus.Metadata.CONTACTS_1_TELEPHONE.val());
    contact.setTelephoneNumber(getPhoneAsString(phone));
    contact.setType(Contact.Type.valueOf((String) metadata.get(Janus.Metadata.CONTACTS_1_TYPE.val())));
    return contact;
  }

  private static Contact getContact2(Map<String, Object> metadata, String c2Mail) {
    Contact contact = new Contact();
    contact.setEmailAddress(c2Mail);
    contact.setGivenName((String) metadata.get(Janus.Metadata.CONTACTS_2_GIVENNAME.val()));
    contact.setSurName((String) metadata.get(Janus.Metadata.CONTACTS_2_SURNAME.val()));
    Object phone = metadata.get(Janus.Metadata.CONTACTS_2_TELEPHONE.val());
    contact.setTelephoneNumber(getPhoneAsString(phone));
    contact.setType(Contact.Type.valueOf((String) metadata.get(Janus.Metadata.CONTACTS_2_TYPE.val())));
    return contact;
  }

  /**
   * The value of the phone number sometimes autocasts to an Integer
   *
   * @param p Object that may contain the phone number
   * @return String value of the phone number, can be {@literal null}
   */
  private static String getPhoneAsString(Object p) {
    String phone = null;
    if (p instanceof String) {
      phone = (String) p;

    } else if (p instanceof Integer) {
      phone = p.toString();
    }
    return phone;
  }

  private void addContact(Contact contact) {
    contacts.add(contact);
  }

  public void setAppTitle(String appTitle) {
    this.appTitle = appTitle;
  }

  public void setAppIcon(String appIcon) {
    this.appIcon = appIcon;
  }

  public void setAppDescription(String appDescription) {
    this.appDescription = appDescription;
  }

  public void setAppThumbNail(String appThumbNail) {
    this.appThumbNail = appThumbNail;
  }

  public void setAppEntityId(String appEntityId) {
    this.appEntityId = appEntityId;
  }

  public void setOauthCallbackUrl(String oauthCallbackUrl) {
    this.oauthCallbackUrl = oauthCallbackUrl;
  }

  public void setTwoLeggedOauthAllowed(boolean twoLeggedOauthAllowed) {
    this.twoLeggedOauthAllowed = twoLeggedOauthAllowed;
  }

  public void setOauthConsumerKey(String oauthConsumerKey) {
    this.oauthConsumerKey = oauthConsumerKey;
  }

  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
  }

  public String getAppTitle() {
    return appTitle;
  }

  public String getAppIcon() {
    return appIcon;
  }

  public String getAppDescription() {
    return appDescription;
  }

  public String getAppThumbNail() {
    return appThumbNail;
  }

  public String getAppEntityId() {
    return appEntityId;
  }

  public String getOauthCallbackUrl() {
    return oauthCallbackUrl;
  }

  public boolean isTwoLeggedOauthAllowed() {
    return twoLeggedOauthAllowed;
  }

  public String getOauthConsumerKey() {
    return oauthConsumerKey;
  }

  public void setOauthConsumerSecret(String oauthConsumerSecret) {
    this.oauthConsumerSecret = oauthConsumerSecret;
  }

  public String getOauthConsumerSecret() {
    return oauthConsumerSecret;
  }

  public String getAppLogoUrl() {
    return appLogoUrl;
  }

  public void setAppLogoUrl(String appLogoUrl) {
    this.appLogoUrl = appLogoUrl;
  }

  /**
   * @deprecated use #getAppHomeUrls with the language code as key
   */
  public String getAppHomeUrl() {
    return this.getAppHomeUrls().get(LANG_EN);
  }

  /**
   * @deprecated use #setAppHomeUrls with the language code as key
   */
  public void setAppHomeUrl(String appHomeUrl) {
    addAppHomeUrl(LANG_EN, appHomeUrl);
  }

  public List<Contact> getContacts() {
    return contacts;
  }

  public boolean isIdpVisibleOnly() {
    return isIdpVisibleOnly;
  }

  public void setIdpVisibleOnly(boolean idpVisibleOnly) {
    isIdpVisibleOnly = idpVisibleOnly;
  }

  public String getEula() {
    return eula;
  }

  public void setEula(String eula) {
    this.eula = eula;
  }

  /**
   * @deprecated use #getNames with the language code as key
   */
  public String getName() {
    return names.get(LANG_EN);
  }

  /**
   * @deprecated use #setNames with the language code as key
   */
  public void setName(String name) {
    addName(LANG_EN, name);
  }

  /**
   * @deprecated use #setDescriptions with the language code as key
   */
  public String getDescription() {
    return this.descriptions.get(LANG_EN);
  }

  /**
   * @deprecated use #getDescriptions with the language code as key
   */
  public void setDescription(String description) {
    addDescription(LANG_EN, description);
  }

  /**
   * @return the consentNotRequired
   */
  public boolean isConsentNotRequired() {
    return consentNotRequired;
  }

  /**
   * @param consentNotRequired the consentNotRequired to set
   */
  public void setConsentNotRequired(boolean consentNotRequired) {
    this.consentNotRequired = consentNotRequired;
  }


  public String getWorkflowState() {
    return workflowState;
  }

  public void setWorkflowState(String workflowState) {
    this.workflowState = workflowState;
  }

  public Map<String, String> getNames() {
    return names;
  }

  public void setNames(Map<String, String> names) {
    this.names = names;
  }

  public void addName(String language, String value) {
    this.names.put(language, value);
  }

  public Map<String, String> getAppHomeUrls() {
    return appHomeUrls;
  }

  public void setAppHomeUrls(Map<String, String> appHomeUrls) {
    this.appHomeUrls = appHomeUrls;
  }

  public void addAppHomeUrl(String language, String value) {
    this.appHomeUrls.put(language, value);
  }

  public Map<String, String> getDescriptions() {
    return descriptions;
  }

  public void setDescriptions(Map<String, String> descriptions) {
    this.descriptions = descriptions;
  }

  public void addDescription(String language, String value) {
    this.descriptions.put(language, value);
  }

  public Map<String, String> getUrls() {
    return urls;
  }

  public void setUrls(Map<String, String> urls) {
    this.urls = urls;
  }

  public void addUrl(String language, String value) {
    this.urls.put(language, value);
  }

  public String getInstutionId() {
    return instutionId;
  }

  public void setInstutionId(String instutionId) {
    this.instutionId = instutionId;
  }

  public String getApplicationUrl() {
    return applicationUrl;
  }

  public void setApplicationUrl(String applicationUrl) {
    this.applicationUrl = applicationUrl;
  }

  public boolean isPublishedInEduGain() {
    return publishedInEduGain;
  }

  public void setPublishedInEduGain(boolean publishedInEduGain) {
    this.publishedInEduGain = publishedInEduGain;
  }
}
