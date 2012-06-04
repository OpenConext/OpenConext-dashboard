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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

/**
 * Attribute release policy.
 * <p/>
 * Note that there is a difference in the ARP between the federatie and conext.
 * <p/>
 * Federatie: an ARP can be linked to an IdP, otherwise the default ARP is applied.
 * If an attribute is released, all values are allowed.
 * <p/>
 * Conext: an ARP applies to all IdP's, but whether the value of an attribute is passed,
 * may depend on the allowed value. By default all values are allowed [*].
 */
@XStreamAlias("ARP")
public class ARP implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Only available for the SURFfederatie ARP
   */
  @XStreamAlias("idpid")
  @XStreamAsAttribute
  private String idpId;

  /**
   * List of SURFfederatie ARP attributes. Contains the names of the allowed attributes
   */
  @XStreamImplicit(itemFieldName = "Attribute")
  private List<String> fedAttributes = new ArrayList<String>();

  /**
   * Maps of SURFconext ARP attributes. The key is the attribute name, the values are the allowed values
   */
  @XStreamOmitField
  private Map<String, List<Object>> conextAttributes = new LinkedHashMap<String, List<Object>>();

  public ARP() {
  }

  public ARP(nl.surfnet.coin.janus.domain.ARP janusARP) {
    this.conextAttributes = janusARP.getAttributes();
  }

  public String getIdpId() {
    return idpId;
  }

  public void setIdpId(String idpId) {
    this.idpId = idpId;
  }

  public List<String> getFedAttributes() {
    return fedAttributes;
  }

  public void setFedAttributes(List<String> fedAttributes) {
    this.fedAttributes = fedAttributes;
  }

  public void addAttributeName(String attributeName) {
    this.fedAttributes.add(attributeName);
  }

  public Map<String, List<Object>> getConextAttributes() {
    return conextAttributes;
  }

  public void setConextAttributes(Map<String, List<Object>> conextAttributes) {
    this.conextAttributes = conextAttributes;
  }

  private static List<Object> allValues() {
    List<Object> allValues = new ArrayList<Object>(1);
    allValues.add("*");
    return allValues;
  }
}
