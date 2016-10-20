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

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.Assert.notNull;
import static org.springframework.util.StringUtils.hasText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.google.common.collect.ImmutableMap;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Proxy;
import org.hibernate.annotations.SortNatural;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;

import selfservice.domain.FacetValue;
import selfservice.domain.License;
import selfservice.domain.LicenseStatus;
import selfservice.domain.Provider.Language;
import selfservice.domain.ServiceProvider;
import selfservice.domain.csa.Field.Key;
import selfservice.domain.csa.Field.Source;
import selfservice.util.DomainObject;

@SuppressWarnings("serial")
@Entity
@Proxy(lazy = false)
public class CompoundServiceProvider extends DomainObject {

  public static final String SR_DEFAULT_LOGO_VALUE = "https://.png";

  @Transient
  private ServiceProvider serviceProvider;

  @Transient
  private Article article;

  @Transient
  private List<License> licenses;

  @Column(unique = true)
  private String serviceProviderEntityId;

  @Column
  private String lmngId;

  @Enumerated(EnumType.STRING)
  private LicenseStatus licenseStatus = LicenseStatus.NOT_NEEDED;

  @Column
  private boolean normenkaderPresent;

  @Column
  private String normenkaderUrl;

  @Column
  private boolean exampleSingleTenant;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "compoundServiceProvider")
  @SortNatural
  private SortedSet<FieldString> fields = new TreeSet<>();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "compoundServiceProvider")
  @SortNatural
  private SortedSet<FieldImage> fieldImages = new TreeSet<>();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "compoundServiceProvider")
  private Set<Screenshot> screenShotsImages = new HashSet<>();

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinTable(name = "facet_value_compound_service_provider", joinColumns = {
    @JoinColumn(name = "compound_service_provider_id", nullable = false, updatable = false)},
    inverseJoinColumns = {@JoinColumn(name = "facet_value_id", nullable = false, updatable = false)})

  @SortNatural
  private SortedSet<FacetValue> facetValues = new TreeSet<>();


  public static CompoundServiceProvider builder(ServiceProvider serviceProvider, Optional<Article> article) {
    notNull(serviceProvider);

    byte[] appStoreLogoImageBytes = getImageBytesFromClasspath("300x300.png");
    byte[] detailLogoImageBytes = getImageBytesFromClasspath("500x300.png");

    CompoundServiceProvider provider = new CompoundServiceProvider();
    provider.setServiceProvider(serviceProvider);
    article.ifPresent(provider::setArticle);
    provider.setExampleSingleTenant(serviceProvider.isExampleSingleTenant());

    buildFieldImage(Key.DETAIL_LOGO, article.map(Article::getDetailLogo).orElse(null), validSrLogo(serviceProvider.getLogoUrl()), detailLogoImageBytes, provider);
    buildFieldString(Key.ENDUSER_DESCRIPTION_NL, article.map(Article::getEndUserDescriptionNl).orElse(null), null, provider);
    buildFieldString(Key.INSTITUTION_DESCRIPTION_NL, article.map(Article::getInstitutionDescriptionNl).orElse(null), null, provider);
    buildFieldString(Key.SERVICE_DESCRIPTION_NL, article.map(Article::getServiceDescriptionNl).orElse(null), serviceProvider.getDescription(Language.NL), provider);

    buildFieldString(Key.TITLE_EN, null, serviceProvider.getName(Language.EN), provider);
    buildFieldString(Key.TITLE_NL, null, serviceProvider.getName(Language.NL), provider);
    buildFieldImage(Key.APPSTORE_LOGO, null, validSrLogo(serviceProvider.getLogoUrl()), appStoreLogoImageBytes, provider);
    buildFieldString(Key.APP_URL, null, serviceProvider.getApplicationUrl(), provider);
    buildFieldString(Key.ENDUSER_DESCRIPTION_EN, null, null, provider);
    buildFieldString(Key.EULA_URL, null, serviceProvider.getEulaURL(), provider);
    buildFieldString(Key.INSTITUTION_DESCRIPTION_EN, null, null, provider);
    buildFieldString(Key.SERVICE_DESCRIPTION_EN, null, serviceProvider.getDescription(Language.EN), provider);
    buildFieldString(Key.SERVICE_URL, null, getServiceUrl(serviceProvider), provider);
    buildFieldString(Key.SUPPORT_MAIL, null, getMail(serviceProvider, ContactPersonType.help), provider);
    buildFieldString(Key.SUPPORT_URL_NL, null, getSupportUrl(serviceProvider, Language.NL), provider);
    buildFieldString(Key.SUPPORT_URL_EN, null, getSupportUrl(serviceProvider, Language.EN), provider);
    buildFieldString(Key.TECHNICAL_SUPPORTMAIL, null, getMail(serviceProvider, ContactPersonType.technical), provider);
    buildFieldString(Key.WIKI_URL_EN, null, null, provider);
    buildFieldString(Key.WIKI_URL_NL, null, null, provider);
    buildFieldString(Key.INTERFED_SOURCE, null, serviceProvider.getInterfedSource(), provider);
    buildFieldString(Key.PRIVACY_STATEMENT_URL_EN, null, serviceProvider.getPrivacyStatementUrlEn(), provider);
    buildFieldString(Key.PRIVACY_STATEMENT_URL_NL, null, serviceProvider.getPrivacyStatementUrlNl(), provider);
    buildFieldString(Key.REGISTRATION_INFO_URL, null, serviceProvider.getRegistrationInfo(), provider);
    buildFieldString(Key.REGISTRATION_POLICY_URL_EN, null, serviceProvider.getRegistrationPolicyUrlEn(), provider);
    buildFieldString(Key.REGISTRATION_POLICY_URL_NL, null, serviceProvider.getRegistrationPolicyUrlNl(), provider);
    buildFieldString(Key.ENTITY_CATEGORIES_1, null, serviceProvider.getEntityCategories1(), provider);
    buildFieldString(Key.ENTITY_CATEGORIES_2, null, serviceProvider.getEntityCategories2(), provider);
    buildFieldString(Key.PUBLISH_IN_EDUGAIN_DATE, null, serviceProvider.getPublishInEdugainDate(), provider);

    return provider;
  }

  public Set<FieldString> getFields() {
    return fields;
  }

  public void setFields(SortedSet<FieldString> fields) {
    this.fields = fields;
  }

  public Set<FieldImage> getFieldImages() {
    return fieldImages;
  }

  public void setFieldImages(SortedSet<FieldImage> fieldImages) {
    this.fieldImages = fieldImages;
  }

  public Set<Screenshot> getScreenShotsImages() {
    return screenShotsImages;
  }

  public void setScreenShotsImages(Set<Screenshot> screenshots) {
    this.screenShotsImages = screenshots;
  }

  public ServiceProvider getSp() {
    return serviceProvider;
  }

  public String getServiceProviderEntityId() {
    return serviceProviderEntityId;
  }

  public String getLmngId() {
    return lmngId;
  }

  public String getTitleNl() {
    return (String) getFieldValue(Key.TITLE_NL);
  }

  public String getTitleEn() {
    return (String) getFieldValue(Key.TITLE_EN);
  }

  public String getServiceDescriptionNl() {
    return (String) getFieldValue(Key.SERVICE_DESCRIPTION_NL);
  }

  public String getServiceDescriptionEn() {
    return (String) getFieldValue(Key.SERVICE_DESCRIPTION_EN);
  }

  public String getInstitutionDescriptionNl() {
    return (String) getFieldValue(Key.INSTITUTION_DESCRIPTION_NL);
  }

  public String getInstitutionDescriptionEn() {
    return (String) getFieldValue(Key.INSTITUTION_DESCRIPTION_EN);
  }

  public String getEnduserDescriptionNl() {
    return (String) getFieldValue(Key.ENDUSER_DESCRIPTION_NL);
  }

  public String getEnduserDescriptionEn() {
    return (String) getFieldValue(Key.ENDUSER_DESCRIPTION_EN);
  }

  public String getAppStoreLogo() {
    return (String) getFieldValue(Key.APPSTORE_LOGO);
  }

  public String getDetailLogo() {
    return (String) getFieldValue(Key.DETAIL_LOGO);
  }

  public String getAppUrl() {
    return (String) getFieldValue(Key.APP_URL);
  }

  public List<byte[]> getScreenshots() {
    return screenShotsImages.stream().map(Screenshot::getImage).collect(toList());
  }

  public String getServiceUrl() {
    return (String) getFieldValue(Key.SERVICE_URL);
  }

  public String getSupportUrlNl() {
    return (String) getFieldValue(Key.SUPPORT_URL_NL);
  }

  public String getSupportUrlEn() {
    return (String) getFieldValue(Key.SUPPORT_URL_EN);
  }

  public String getEulaUrl() {
    return (String) getFieldValue(Key.EULA_URL);
  }

  public String getWikiUrlEn() {
    return (String) getFieldValue(Key.WIKI_URL_EN);
  }

  public String getWikiUrlNl() {
    return (String) getFieldValue(Key.WIKI_URL_NL);
  }

  public String getInterfedSource() {
    return (String) getFieldValue(Key.INTERFED_SOURCE);
  }
  
  public String getPrivacyStatementUrlEn() {
    return (String) getFieldValue(Key.PRIVACY_STATEMENT_URL_EN);
  }
  
  public String getPrivacyStatementUrlNl() {
    return (String) getFieldValue(Key.PRIVACY_STATEMENT_URL_NL);
  }
  
  public String getRegistrationInfo() {
    return (String) getFieldValue(Key.REGISTRATION_INFO_URL);
  }
  
  public String getRegistrationPolicyUrlEn() {
    return (String) getFieldValue(Key.REGISTRATION_POLICY_URL_EN);
  }
  
  public String getRegistrationPolicyUrlNl() {
    return (String) getFieldValue(Key.REGISTRATION_POLICY_URL_NL);
  }

  public String getEntityCategories1() {
    return (String) getFieldValue(Key.ENTITY_CATEGORIES_1);
  }

  public String getEntityCategories2() {
    return (String) getFieldValue(Key.ENTITY_CATEGORIES_2);
  }

  public String getPublishInEdugainDate() {
    return (String) getFieldValue(Key.PUBLISH_IN_EDUGAIN_DATE);
  }

  public String getSupportMail() {
    return (String) getFieldValue(Key.SUPPORT_MAIL);
  }

  public String getTechnicalSupportMail() {
    return (String) getFieldValue(Key.TECHNICAL_SUPPORTMAIL);
  }

  public boolean addFieldString(FieldString f) {
    checkNotNull(f).setCompoundServiceProvider(this);

    return this.fields.add(f);
  }

  public boolean addFieldImage(FieldImage f) {
    checkNotNull(f).setCompoundServiceProvider(this);

    return this.fieldImages.add(f);
  }

  public boolean addScreenShot(Screenshot s) {
    checkNotNull(s).setCompoundServiceProvider(this);

    return this.screenShotsImages.add(s);
  }

  public boolean removeScreenShot(Screenshot s) {
    checkNotNull(s).setCompoundServiceProvider(null);

    return this.screenShotsImages.remove(s);
  }

  /*
   * Note that we could use reflection and create a very generic framework.
   * However this puts to many constraints on the naming convention of unrelated
   * Objects (e.g. Service Provider and License). Therefore we have chosen to
   * explicitly retrieve values.
   */
  private Object getFieldValue(Key key) {
    checkNotNull(key);

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

    throw new RuntimeException("Unset key (" + key + ") for ('" + this + "'");
  }

  private String getDistributionChannelProperty(Field field) {
    if (field instanceof FieldImage) {
      return ((FieldImage) field).getFileUrl();
    }
    if (field instanceof FieldString) {
      return ((FieldString) field).getValue();
    }
    throw new RuntimeException("Unknown Field class: " + field.getClass());
  }

  public Map<Key, String> getSurfConextFieldValues() {
    return getFieldValues(Source.SURFCONEXT);
  }

  public Map<Key, String> getLmngFieldValues() {
    return getFieldValues(Source.LMNG);
  }

  public Map<Key, String> getDistributionFieldValues() {
    return getFieldValues(Source.DISTRIBUTIONCHANNEL);
  }

  /**
   * Convenience method for JSP access
   *
   * @return Map with all Keys currently supported by SURFconext
   */
  private Map<Key, String> getFieldValues(Source source) {
    Key[] values = Key.values();
    Map<Key, String> result = new HashMap<Key, String>();
    if (source.equals(Source.DISTRIBUTIONCHANNEL)) {
      for (FieldString field : this.fields) {
        result.put(field.getKey(), field.getValue());
      }
      for (FieldImage fieldImage : this.fieldImages) {
        result.put(fieldImage.getKey(), fieldImage.getFileUrl());
      }

    } else {
      for (Key key : values) {
        try {
          switch (source) {
            case SURFCONEXT:
              result.put(key, (String) getSurfConextProperty(key));
              break;
            case LMNG:
              result.put(key, (String) getLmngProperty(key));
              break;
            case DISTRIBUTIONCHANNEL:
              // already covered
              break;
          }
        } catch (RuntimeException e) {
          // WHAT???
          // not a problem here
        }
      }
    }
    return result;
  }

  private static final ImmutableMap<Key, Function<CompoundServiceProvider, Object>> surfConextProperties = new ImmutableMap.Builder<Key, Function<CompoundServiceProvider, Object>>()
      .put(Key.SERVICE_DESCRIPTION_NL, provider -> provider.serviceProvider.getDescription(Language.NL))
      .put(Key.SERVICE_DESCRIPTION_EN, provider -> provider.serviceProvider.getDescription(Language.EN))
      .put(Key.APPSTORE_LOGO, provider -> provider.serviceProvider.getLogoUrl())
      .put(Key.DETAIL_LOGO, provider -> provider.serviceProvider.getLogoUrl())
      .put(Key.APP_URL, provider -> provider.serviceProvider.getApplicationUrl())
      .put(Key.SERVICE_URL, provider -> getServiceUrl(provider.serviceProvider))
      .put(Key.SUPPORT_URL_NL, provider -> getSupportUrl(provider.serviceProvider, Language.NL))
      .put(Key.SUPPORT_URL_EN, provider -> getSupportUrl(provider.serviceProvider, Language.EN))
      .put(Key.SUPPORT_MAIL, provider -> {
        ContactPerson helpCP = provider.serviceProvider.getContactPerson(ContactPersonType.help);
        return helpCP != null ? helpCP.getEmailAddress() : null;
      })
      .put(Key.TECHNICAL_SUPPORTMAIL, provider -> {
        ContactPerson cp = provider.serviceProvider.getContactPerson(ContactPersonType.technical);
        return cp != null ? cp.getEmailAddress() : null;
      })
      .put(Key.EULA_URL, provider -> provider.serviceProvider.getEulaURL())
      .put(Key.TITLE_NL, provider -> Optional.ofNullable(provider.serviceProvider).map(sp -> sp.getName(Language.NL)).orElse(provider.serviceProviderEntityId))
      .put(Key.TITLE_EN, provider -> Optional.ofNullable(provider.serviceProvider).map(sp -> sp.getName(Language.EN)).orElse(provider.serviceProviderEntityId))
      .put(Key.INTERFED_SOURCE, provider -> provider.serviceProvider.getInterfedSource())
      .put(Key.PRIVACY_STATEMENT_URL_EN, provider -> provider.serviceProvider.getPrivacyStatementUrlEn())
      .put(Key.PRIVACY_STATEMENT_URL_NL, provider -> provider.serviceProvider.getPrivacyStatementUrlNl())
      .put(Key.REGISTRATION_INFO_URL, provider -> provider.serviceProvider.getRegistrationInfo())
      .put(Key.REGISTRATION_POLICY_URL_EN, provider -> provider.serviceProvider.getRegistrationPolicyUrlEn())
      .put(Key.REGISTRATION_POLICY_URL_NL, provider -> provider.serviceProvider.getRegistrationPolicyUrlNl())
      .put(Key.ENTITY_CATEGORIES_1, provider -> provider.serviceProvider.getEntityCategories1())
      .put(Key.ENTITY_CATEGORIES_2, provider -> provider.serviceProvider.getEntityCategories2())
      .put(Key.PUBLISH_IN_EDUGAIN_DATE, provider -> provider.serviceProvider.getPublishInEdugainDate())

      .build();

  private static final ImmutableMap<Key, Function<Optional<Article>, Object>> lmngProperites = new ImmutableMap.Builder<Key, Function<Optional<Article>, Object>>()
      .put(Key.ENDUSER_DESCRIPTION_NL, article -> article.map(Article::getEndUserDescriptionNl).orElse(null))
      .put(Key.INSTITUTION_DESCRIPTION_NL, article -> article.map(Article::getInstitutionDescriptionNl).orElse(null))
      .put(Key.SERVICE_DESCRIPTION_NL, article -> article.map(Article::getServiceDescriptionNl).orElse(null))
      .put(Key.DETAIL_LOGO, article -> article.map(Article::getDetailLogo).orElse(null))
      .build();

  private Object getSurfConextProperty(Key key) {
    return Optional.ofNullable(surfConextProperties.get(key))
        .orElseThrow(() -> new RuntimeException("SURFConext does not support property: " + key))
        .apply(this);
  }

  private static boolean isSupportedSurfConextProperty(Key key) {
    return surfConextProperties.containsKey(key);
  }

  private Object getLmngProperty(Key key) {
    Optional<Article> optionalArticle = Optional.ofNullable(article);
    return Optional.ofNullable(lmngProperites.get(key))
        .orElseThrow(() -> new RuntimeException("LMNG does not support property: " + key))
        .apply(optionalArticle);
  }

  private static boolean isSupportedLmngProperty(Key key) {
    return lmngProperites.containsKey(key);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("id", getId()).append("serviceProvider", serviceProvider)
      .append("serviceProviderEntityId", serviceProviderEntityId).append("lmngId", lmngId).toString();
  }

  private static byte[] getImageBytesFromClasspath(String filename) {
    try {
      return IOUtils.toByteArray(new ClassPathResource(filename).getInputStream());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public ServiceProvider getServiceProvider() {
    return serviceProvider;
  }

  public void setServiceProvider(ServiceProvider serviceProvider) {
    this.serviceProvider = serviceProvider;
    this.serviceProviderEntityId = serviceProvider.getId();
  }

  public Article getArticle() {
    return article;
  }

  public void setArticle(Article article) {
    this.article = article;
    this.lmngId = article.getLmngProductIdentifier();
  }

  public boolean isArticleAvailable() {
    return article != null;
  }

  public List<License> getLicenses() {
    return licenses;
  }

  /**
   * Convenience method for the first (and only?) license belonging to an idp and a service
   *
   * @return the first license found or null
   */
  public License getLicense() {
    return licenses == null || licenses.isEmpty() ? null : licenses.get(0);
  }

  public void setLicenses(List<License> licenses) {
    this.licenses = licenses;
  }

  public boolean isLicenseAvailable() {
    return licenses != null && !licenses.isEmpty();
  }

  public boolean isArticleLicenseAvailable() {
    return isArticleAvailable() && isLicenseAvailable();
  }

  public LicenseStatus getLicenseStatus() {
    return licenseStatus;
  }

  public void setLicenseStatus(LicenseStatus licenseStatus) {
    this.licenseStatus = licenseStatus;
  }

  public boolean isNormenkaderPresent() {
    return normenkaderPresent;
  }

  public void setNormenkaderPresent(boolean normenkaderPresent) {
    this.normenkaderPresent = normenkaderPresent;
  }

  public String getNormenkaderUrl() {
    return normenkaderUrl;
  }

  public void setNormenkaderUrl(String normenkaderUrl) {
    this.normenkaderUrl = normenkaderUrl;
  }

  public boolean isExampleSingleTenant() {
    return exampleSingleTenant;
  }

  public void setExampleSingleTenant(boolean exampleSingleTenant) {
    this.exampleSingleTenant = exampleSingleTenant;
  }

  private static void buildFieldString(Key key, String lmng, String surfconext, CompoundServiceProvider provider) {
    FieldString fieldString;
    if (hasText(lmng)) {
      fieldString = new FieldString(Source.LMNG, key, null);
    } else if (hasText(surfconext)) {
      fieldString = new FieldString(Source.SURFCONEXT, key, null);
    } else {
      fieldString = new FieldString(Source.DISTRIBUTIONCHANNEL, key, null);
    }

    updatePossibleFieldOrigin(fieldString);

    provider.addFieldString(fieldString);
  }

  private static void buildFieldImage(Key key, String lmng, String surfconext, byte[] distributionChannel, CompoundServiceProvider provider) {
    FieldImage fieldImage;
    byte[] nullByte = null;
    if (hasText(lmng)) {
      fieldImage = new FieldImage(Source.LMNG, key, nullByte);
    } else if (hasText(surfconext)) {
      fieldImage = new FieldImage(Source.SURFCONEXT, key, nullByte);
    } else {
      fieldImage = new FieldImage(Source.DISTRIBUTIONCHANNEL, key, distributionChannel);
    }

    updatePossibleFieldOrigin(fieldImage);
    provider.addFieldImage(fieldImage);
  }

  private static void updatePossibleFieldOrigin(Field field) {
    // Cloud Distribution is always a possible origin for fields
    if (isAllowedCombination(field.getKey(), Source.LMNG)) {
      field.setAvailableInSurfMarket(TRUE);
    } else {
      field.setAvailableInSurfMarket(FALSE);
    }
    if (isAllowedCombination(field.getKey(), Source.SURFCONEXT)) {
      field.setAvailableInSurfConext(TRUE);
    } else {
      field.setAvailableInSurfConext(FALSE);
    }
  }

  public void updateTransientOriginFields() {
    for (Field current : this.fields) {
      updatePossibleFieldOrigin(current);
    }

    for (Field current : this.fieldImages) {
      updatePossibleFieldOrigin(current);
    }
  }

  private static String getServiceUrl(ServiceProvider sp) {
    Map<String, String> homeUrls = sp.getHomeUrls();
    if (!CollectionUtils.isEmpty(homeUrls)) {
      String homeUrl = homeUrls.get(Language.NL.name().toLowerCase());
      if (StringUtils.isNotBlank(homeUrl)) {
        return homeUrl;
      }
      homeUrl = homeUrls.get(Language.EN.name().toLowerCase());
      if (StringUtils.isNotBlank(homeUrl)) {
        return homeUrl;
      }
    }
    return null;
  }

  private static String getSupportUrl(ServiceProvider sp, Language lang) {
    Map<String, String> urls = sp.getUrls();
    if (CollectionUtils.isEmpty(urls)) {
      return sp.getUrl();
    }
    return urls.get(lang.name().toLowerCase());
  }

  private static String getMail(ServiceProvider serviceProvider, ContactPersonType type) {
    ContactPerson helpCP = serviceProvider.getContactPerson(type);
    return (helpCP == null ? null : helpCP.getEmailAddress());
  }

  public static boolean isAllowedCombination(Key key, Source source) {
    switch (source) {
      case LMNG:
        return isSupportedLmngProperty(key);
      case SURFCONEXT:
        return isSupportedSurfConextProperty(key);
      default:
        return true;
    }
  }

  public SortedSet<FacetValue> getFacetValues() {
    return facetValues;
  }

  public void setFacetValues(SortedSet<FacetValue> facetValues) {
    this.facetValues = facetValues;
  }

  public void addFacetValue(FacetValue facetValue) {
    this.facetValues.add(facetValue);
  }

  public void removeFacetValue(FacetValue facetValue) {
    this.facetValues.remove(facetValue);
  }

  public String getSearchFacetValues() {
    Collection<String> values = new ArrayList<String>();
    if (!CollectionUtils.isEmpty(facetValues)) {
      for (FacetValue facetValue : facetValues) {
        values.add(facetValue.getSearchValue());
      }
    }
    return StringUtils.join(values, " ");
  }

  private static String validSrLogo(String appStoreLogo) {
    // we need to nullify the 'https://.png' value as this is the default value in SR (see module_janus_metadata_fields.php)
    String result = appStoreLogo;
    if (StringUtils.isNotBlank(result) && result.equalsIgnoreCase(SR_DEFAULT_LOGO_VALUE)) {
      result = null;
    }
    return result;
  }

}
