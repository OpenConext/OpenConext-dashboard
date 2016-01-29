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
package csa.domain;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.hibernate.annotations.Proxy;

/**
 * StringField.java
 *
 */
@SuppressWarnings("serial")
@Entity
@Proxy(lazy = false)
public class FieldString extends Field {

  @Column(name = "field_value", length=65535)
  private String value;

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
      .appendSuper(super.toString())
      .append("value", value)
      .toString();
  }

  public FieldString() {
    super();
  }

  public FieldString(Source source, Key key, String value) {
    super(source, key, null);
    this.value = value;
  }

  public FieldString(Source source, Key key, String value, CompoundServiceProvider compoundServiceProvider) {
    super(source, key, compoundServiceProvider);
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  /* (non-Javadoc)
   * @see nl.surfnet.coin.csa.domain.Field#isUnset()
   */
  @Override
  public boolean isUnset() {
    return Field.Source.DISTRIBUTIONCHANNEL.equals(getSource()) && StringUtils.isBlank(value);

  }
  
}
