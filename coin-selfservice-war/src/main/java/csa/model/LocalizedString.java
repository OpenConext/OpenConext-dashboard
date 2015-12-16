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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Proxy;

import csa.util.DomainObject;

@SuppressWarnings("serial")
@Entity
@Proxy(lazy = false)
public class LocalizedString extends DomainObject {

  @Column
  private String locale;

  @Column
  private String value;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "multilingual_string_id", nullable = false)
  @JsonIgnore
  private MultilingualString multilingualString;

  public LocalizedString() {
    super();
  }

  public LocalizedString(String locale, String value, MultilingualString multilingualString) {
    super();
    this.locale = locale;
    this.value = value;
    this.multilingualString = multilingualString;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public MultilingualString getMultilingualString() {
    return multilingualString;
  }

  public void setMultilingualString(MultilingualString multilingualString) {
    this.multilingualString = multilingualString;
  }
}
