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
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockPageContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import nl.surfnet.coin.selfservice.domain.ARP;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.easymock.EasyMock.createMock;

/**
 * Test for {@link ARPFilter}
 */
public class ARPFilterTest {

  private ARPFilter arpFilter;
  private PageContext mockPageContext;

  private static final String IDP1 = "idp1";
  private static final String IDP2 = "idp2";

  @Before
  public void setUp() throws Exception {
    ServletContext mockServletContext = new MockServletContext();
    WebApplicationContext webApplicationContext = createMock(WebApplicationContext.class);
    mockServletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE,
        webApplicationContext);
    mockPageContext = new MockPageContext(mockServletContext);

    arpFilter = new ARPFilter();
    arpFilter.setPageContext(mockPageContext);
    arpFilter.setVar("var");
  }

  @Test
  public void testDoEndTag_match() throws Exception {
    final List<ARP> arps = getArps();
    arpFilter.setIdpId(IDP1);
    arpFilter.setArpList(arps);

    final int result = arpFilter.doEndTag();
    assertEquals(TagSupport.EVAL_PAGE, result);
    final List<ARP> list = (List<ARP>) mockPageContext.getAttribute("var");
    assertEquals(1, list.size());
    assertEquals(2, list.get(0).getFedAttributes().size());
  }

  @Test
  public void testDoEndTag_noMatch() throws Exception {
    final List<ARP> arps = getArps();
    arpFilter.setIdpId(IDP2);
    arpFilter.setArpList(arps);

    final int result = arpFilter.doEndTag();
    assertEquals(TagSupport.EVAL_PAGE, result);
    final List<ARP> list = (List<ARP>) mockPageContext.getAttribute("var");
    assertEquals(1, list.size());
    assertEquals(1, list.get(0).getFedAttributes().size());
  }

  @Test
  public void testDoEndTag_emptyList() throws Exception {
    arpFilter.setIdpId(IDP1);
    arpFilter.setArpList(Collections.<ARP>emptyList());
    final int result = arpFilter.doEndTag();
    assertEquals(TagSupport.EVAL_PAGE, result);
    final List<ARP> list = (List<ARP>) mockPageContext.getAttribute("var");
    assertTrue(list.isEmpty());
  }

  private static List<ARP> getArps() {
    List<ARP> arps = new ArrayList<ARP>();

    ARP arp1 = new ARP();
    arp1.addAttributeName("name");
    arps.add(arp1);

    ARP arp2 = new ARP();
    arp2.setIdpId(IDP1);
    arp2.addAttributeName("name");
    arp2.addAttributeName("mail");
    arps.add(arp2);

    return arps;
  }
}
