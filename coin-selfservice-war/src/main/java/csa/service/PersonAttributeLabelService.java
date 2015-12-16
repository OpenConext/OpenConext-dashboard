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

package csa.service;

import java.util.Map;

import csa.domain.PersonAttributeLabel;

/**
 * Service to retrieve labels for person attributes
 */
public interface PersonAttributeLabelService {

  /**
   * Gets a {@link Map} of (Person) attribute names and types for pretty labels in the front end.
   * The keys in the map are identical to {@link PersonAttributeLabel#getKey()}
   *
   * @return {@link Map}
   */
  public Map<String, PersonAttributeLabel> getAttributeLabelMap();
}
