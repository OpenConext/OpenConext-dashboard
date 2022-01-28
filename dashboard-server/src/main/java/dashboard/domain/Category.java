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
package dashboard.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.MoreObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable {

    private static final long serialVersionUID = 0L;

    private String name;
    private String searchValue;
    private List<CategoryValue> values = new ArrayList<>();

    public Category() {
    }

    public Category(String name, String searchValue, List<CategoryValue> values) {
        this.name = name;
        this.searchValue = searchValue;
        this.values = values;
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
        values.add(value);
    }

    @JsonIgnore
    public boolean containsValue(String value) {
        return values.stream().anyMatch(cv -> cv.getValue().equals(value));
    }

    @JsonIgnore
    public boolean isUsedFacetValues() {
        return values.stream().anyMatch(cv -> cv.getCount() > 0);
    }

    public String getSearchValue() {
        return searchValue;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(Category.class)
                .add("name", name).add("values", values).toString();
    }
}
