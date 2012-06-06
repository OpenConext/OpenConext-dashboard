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
import java.util.Date;
import java.util.List;

/**
 * Representation for HighCharts of
 * <pre>{
 * name:'Mujina',
 * data:[5, 7, 3, 0, 0, 0],
 * pointStart: Date.UTC(2010, 0, 1),
 * pointInterval: 24 * 3600 * 1000 // one day
 * }</pre>
 */
public class ChartSerie {

  private String name;
  private List<Integer> data = new ArrayList<Integer>();
  private Date pointStart;
  private long pointInterval = 24L * 3600L * 1000L; // one day

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<Integer> getData() {
    return data;
  }

  public void setData(List<Integer> data) {
    this.data = data;
  }

  public void addData(Integer number) {
    this.data.add(number);
  }

  public Date getPointStart() {
    return pointStart;
  }

  public void setPointStart(Date pointStart) {
    this.pointStart = pointStart;
  }

  public long getPointInterval() {
    return pointInterval;
  }

  public void setPointInterval(long pointInterval) {
    this.pointInterval = pointInterval;
  }
}
