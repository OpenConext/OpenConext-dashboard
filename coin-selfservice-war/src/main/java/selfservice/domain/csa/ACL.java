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

package selfservice.domain.csa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("ACL")
public class ACL implements Serializable {

  private static final long serialVersionUID = 1L;

  @XStreamImplicit(itemFieldName = "IdPRef")
  private List<String> idpRefs = new ArrayList<String>();

  public List<String> getIdpRefs() {
    return idpRefs;
  }

  public void setIdpRefs(List<String> idpRefs) {
    this.idpRefs = idpRefs;
  }

  public void addIdpRef(String idpRef) {
    this.idpRefs.add(idpRef);
  }
}
