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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Category implements Serializable {

  private static final long serialVersionUID = 0L;

  private String name;
  private List<CategoryValue> values;

  public Category() {
  }

  public Category(String name) {
    this.name = name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<CategoryValue> getValues() {
    return values;
  }

  public void setValues(List<CategoryValue> values) {
    this.values = values;
  }

  public String getName() {
    return name;
  }

  @JsonIgnore
  public void addCategoryValue(CategoryValue value) {
    if (values == null) {
      values = new ArrayList<>();
    }
    values.add(value);
  }

  @JsonIgnore
  public boolean containsValue(String value) {
    if (values == null) {
      return false;
    }
    for (CategoryValue cv : values) {
      if (cv.getValue().equals(value)) {
        return true;
      }
    }
    return false;
  }

  @JsonIgnore
  public boolean isUsedFacetValues() {
    if (values == null) {
      return false;
    }
    for (CategoryValue categoryValue : values) {
      if (categoryValue.getCount() > 0) {
        return true;
      }
    }
    return false;
  }
}
