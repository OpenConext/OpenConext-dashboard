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

package selfservice.service;

import selfservice.domain.Statistics;

/**
 * Service for statistical information from CSA
 */
public interface StatisticsService {

  /**
   * Retrieve statistics information for the given year and month. Currently
   * this information consists of the number of actions that are performed in
   * this months (actions being, questions, link- and unlink requests). Both the
   * total number of actions as well as the number of action per IDP are
   * returned.
   * 
   * @param month
   *          the month
   * @param year
   *          the year
   * @return the statistical information for the given time period
   */
  Statistics getStatistics(final int month, final int year);
}
