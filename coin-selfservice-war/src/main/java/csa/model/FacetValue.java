/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package csa.model;

import java.util.Locale;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.CompareToBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Proxy;

import csa.util.DomainObject;


@SuppressWarnings("serial")
@Entity
@Proxy(lazy = false)
public class FacetValue extends DomainObject implements Comparable<FacetValue> {

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "facet_id", nullable = false)
  @JsonIgnore
  private Facet facet;

  @Transient
  private int count;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "multilingual_string_id", nullable = false)
  private MultilingualString multilingualString = new MultilingualString();

  public String getValue() {
    return multilingualString.getValue();
  }

  public String getLocaleValue(String locale) {
    return multilingualString.getLocaleValue(locale);
  }

  public void setValue(String value) {
    this.multilingualString.setValue(value);
  }

  public void addValue(Locale locale, String value) {
    this.multilingualString.addValue(locale, value);
  }

  public MultilingualString getMultilingualString() {
    return multilingualString;
  }

  public void setMultilingualString(MultilingualString multilingualString) {
    this.multilingualString = multilingualString;
  }

  public Facet getFacet() {
    return facet;
  }

  public void setFacet(Facet facet) {
    this.facet = facet;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  /*
   * The value of a FacetValue may contain spaces, but if we want to search in (any) clients, then we
   * want to be able to have all the FacetValues of a Service separated by spaces therefore this method
   * can be used to underscore-separate the different FacetValues
   */
  @JsonIgnore
  public String getSearchValue() {
    String val = getValue();
    return val != null ? val.replaceAll(" ", "_").toLowerCase() : val;
  }

  @Override
  public int compareTo(FacetValue that) {
    return new CompareToBuilder()
      .append(this.getValue(), that.getValue())
      .toComparison();
  }
}
