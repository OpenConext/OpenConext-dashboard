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

package nl.surfnet.coin.selfservice.util;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.lang.StringUtils;

import nl.surfnet.coin.selfservice.domain.ARP;

/**
 * Returns a List of {@link nl.surfnet.coin.selfservice.domain.ARP} for the current Identity Provider
 */
@SuppressWarnings("serial")
public class ARPFilter extends TagSupport {

  private String var;
  private List<ARP> arpList;
  private String idpId;

  @Override
  public int doEndTag() throws JspException {
    List<ARP> filteredList = new ArrayList<ARP>();

    if (arpList == null) {
      return EVAL_PAGE;
    }

    for (ARP arp : arpList) {
      if (idpId.equals(arp.getIdpId())) {
        filteredList.add(arp);
      }
    }

    // No specific ARP found, now use all ARP without an IDP
    if (filteredList.isEmpty()) {
      for (ARP arp : arpList) {
        if (StringUtils.isBlank(arp.getIdpId())) {
          filteredList.add(arp);
        }
      }
    }

    pageContext.setAttribute(var, filteredList);

    return EVAL_PAGE;
  }

  public String getVar() {
    return var;
  }

  public void setVar(String var) {
    this.var = var;
  }

  public List<ARP> getArpList() {
    return arpList;
  }

  public void setArpList(List<ARP> arpList) {
    this.arpList = arpList;
  }

  public String getIdpId() {
    return idpId;
  }

  public void setIdpId(String idpId) {
    this.idpId = idpId;
  }
}
