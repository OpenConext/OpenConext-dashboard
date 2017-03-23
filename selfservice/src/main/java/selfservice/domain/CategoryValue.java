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

  @JsonIgnore
  private Category category;

  public CategoryValue() {
  }

  public CategoryValue(String value, Category category) {
    this.value = value;
    this.category = category;
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

  @JsonIgnore
  public Category getCategory() {
    return category;
  }

  /*
   * The value of a FacetValue may contain spaces, but if we want to search in (any) clients, then we
   * want to be able to have all the FacetValues of a Service separated by spaces therefore this method
   * can be used to underscore-separate the different FacetValues.
   *
   * Because it is possible to have FacetValues that have the same value, but belong to different Facet's, we need
   * to include the Facet value (represented by Category) as well in the String
   */
  @JsonIgnore
  public String getSearchValue() {
    Assert.notNull(category, "Category not null");
    Assert.hasLength(category.getName(), "Category name not null");
    Assert.hasLength(value, "Category value not null");

    return category.getName().replaceAll(" ", "_").toLowerCase() + "_" + getValue().replaceAll(" ", "_").toLowerCase();
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
