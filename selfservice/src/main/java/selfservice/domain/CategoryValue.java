/*
 * Copyright 2013 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package selfservice.domain;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.springframework.util.Assert;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;

public class CategoryValue implements Comparable<CategoryValue>, Serializable {

  private static final long serialVersionUID = 0L;

  private int count;
  private String value;

  public CategoryValue(String value) {
    this.value = value;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int getCount() {
    return count;
  }

  public String getValue() {
    return value;
  }

  @Override
  public int compareTo(CategoryValue o) {
    return new CompareToBuilder()
      .append(this.value, o.value)
      .toComparison();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(CategoryValue.class)
        .add("value", value).add("count", count).toString();
  }
}
