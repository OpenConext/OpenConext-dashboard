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
package nl.surfnet.coin.selfservice.domain;

import nl.surfnet.coin.shared.domain.DomainObject;
import org.apache.commons.lang.builder.CompareToBuilder;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;

@SuppressWarnings("serial")
@Entity
@Proxy(lazy = false)
public class FacetValue extends DomainObject implements Comparable<FacetValue> {

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "facet_id", nullable = false)
  private Facet facet;

  @Column(unique = true)
  private String value;

  @Transient
  private int count;

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
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
   * can be used to space-separate the different FacetValues
   */
  public String getSearchValue() {
    String val = getValue();
    return val != null ? val.replaceAll(" ", "_").toLowerCase() : val;
  }

  @Override
  public int compareTo(FacetValue that) {
    return new CompareToBuilder()
            .append(this.value, that.value)
            .toComparison();
  }
}
