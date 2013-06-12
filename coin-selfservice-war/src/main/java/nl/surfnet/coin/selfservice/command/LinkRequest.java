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

package nl.surfnet.coin.selfservice.command;

import nl.surfnet.coin.csa.model.JiraTask;

import javax.validation.constraints.AssertTrue;

public class LinkRequest extends AbstractAction{

  private String notes;

  @AssertTrue
  private boolean agree;

  public boolean isAgree() {
    return agree;
  }

  public void setAgree(boolean agree) {
    this.agree = agree;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }



}
