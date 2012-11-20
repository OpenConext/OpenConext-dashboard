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

import java.io.Serializable;

/**
 * Article information. An article is our LMNG implementation of a service.
 * 
 */
@SuppressWarnings("serial")
public class Article implements Serializable {

  /*
   * Optional pattern applied to prevent many, many nullpointer checks and
   * exceptions
   */
  public static final Article NONE = new Article();

  private String endUserDescriptionNl;
  private String institutionDescriptionNl;
  private String serviceDescriptionNl;
  private String supplierName;
  private String productName;
  private String productNumber;
  private String detailLogo;
  private String specialConditions;
  private String articleState;
  private String lmngIdentifier;
  private String serviceProviderEntityId;
  
  private ArticleMedium appleAppStoreMedium;
  private ArticleMedium androidPlayStoreMedium;
  
  /**
   * Default constructor
   */
  public Article() {
  }

  /**
   * @param lmngIdentifier
   */
  public Article(String lmngIdentifier) {
    super();
    this.lmngIdentifier = lmngIdentifier;
  }

  public String getEndUserDescriptionNl() {
    return endUserDescriptionNl;
  }

  public void setEndUserDescriptionNl(String endUserDescriptionNl) {
    this.endUserDescriptionNl = endUserDescriptionNl;
  }

  public String getInstitutionDescriptionNl() {
    return institutionDescriptionNl;
  }

  public void setInstitutionDescriptionNl(String institutionDescriptionNl) {
    this.institutionDescriptionNl = institutionDescriptionNl;
  }

  public String getServiceDescriptionNl() {
    return serviceDescriptionNl;
  }

  public void setServiceDescriptionNl(String serviceDescriptionNl) {
    this.serviceDescriptionNl = serviceDescriptionNl;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public String getProductNumber() {
    return productNumber;
  }

  public void setProductNumber(String productNumber) {
    this.productNumber = productNumber;
  }

  public String getDetailLogo() {
    return detailLogo;
  }

  public void setDetailLogo(String detailLogo) {
    this.detailLogo = detailLogo;
  }

  public String getSpecialConditions() {
    return specialConditions;
  }

  public void setSpecialConditions(String specialConditions) {
    this.specialConditions = specialConditions;
  }

  public String getArticleState() {
    return articleState;
  }

  public void setArticleState(String articleState) {
    this.articleState = articleState;
  }

  public String getLmngIdentifier() {
    return lmngIdentifier;
  }

  public void setLmngIdentifier(String lmngIdentifier) {
    this.lmngIdentifier = lmngIdentifier;
  }

  public String getServiceProviderEntityId() {
    return serviceProviderEntityId;
  }

  public void setServiceProviderEntityId(String serviceProviderEntityId) {
    this.serviceProviderEntityId = serviceProviderEntityId;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public ArticleMedium getAppleAppStoreMedium() {
    return appleAppStoreMedium;
  }

  public void setAppleAppStoreMedium(ArticleMedium appleAppStoreMedium) {
    this.appleAppStoreMedium = appleAppStoreMedium;
  }

  public ArticleMedium getAndroidPlayStoreMedium() {
    return androidPlayStoreMedium;
  }

  public void setAndroidPlayStoreMedium(ArticleMedium androidPlayStoreMedium) {
    this.androidPlayStoreMedium = androidPlayStoreMedium;
  }

}