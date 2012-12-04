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

package nl.surfnet.coin.selfservice.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation for HighCharts of
 * <pre>{
 * name:'Mujina',
 * data:[5, 7, 3, 0, 0, 0],
 * pointStart: 123123123123,
 * pointInterval: 24 * 3600 * 1000 // one day
 * }</pre>
 */
public class ChartSerie {

  private String name;
  private List<Integer> data = new ArrayList<Integer>();
  private long pointStart;
  private long pointInterval = 24L * 3600L * 1000L; // one day
  private String idp;

  public ChartSerie(String name, String idP, long pointStart) {
    super();
    this.name = name;
    this.pointStart = pointStart;
    this.idp = idP;
  }

  public String getName() {
    return name;
  }

  public List<Integer> getData() {
    return data;
  }

  public long getPointStart() {
    return pointStart;
  }

  public long getPointInterval() {
    return pointInterval;
  }

  public void addData(Integer number) {
    this.data.add(number);
  }

  public void addZeroDays(int nbr) {
    for (int i = 0; i < nbr; i++) {
      this.data.add(0);
    }
  }

  public String getIdp() {
    return idp;
  }
  
}
