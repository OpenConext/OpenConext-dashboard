<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ include file="../include.jsp" %>
<%--
  ~ Copyright 2012 SURFnet bv, The Netherlands
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>
<jsp:useBean id="sp" scope="request" type="nl.surfnet.coin.selfservice.domain.ServiceProvider"/>
<sec:authentication property="principal.idp" scope="request" htmlEscape="true" var="idp"/>
<selfservice:arpFilter var="arps" idpId="${idp}" arpList="${sp.arps}"/>
<div class="arp-info">
	<c:choose>
	  <c:when test="${fn:length(arps) gt 0}">
		  <h2><spring:message code="jsp.sp_detail.arp"/></h2>
		
		  <c:set var="spname"><tags:providername provider="${sp}" /></c:set>
		  <p><spring:message code="jsp.sp_detail.arp.intro" arguments="${spname}"/></p>
		  <c:forEach items="${arps}" var="arp">
		    <p><spring:message code="jsp.sp_detail.arp.policy"/></p>
		    <ul>
		      <c:if test="${empty arp.fedAttributes and empty arp.conextAttributes}">
		        <li><spring:message code="jsp.sp_detail.arp.nopolicy"/></li>
		      </c:if>
		      <c:forEach items="${arp.fedAttributes}" var="att">
		        <li>
		          <tags:arp-attribute-info attributeKey="${att}"/>
		        </li>
		      </c:forEach>
		      <c:forEach items="${arp.conextAttributes}" var="att">
		        <li><tags:arp-attribute-info attributeKey="${att.key}"/>
		          <%-- In ServiceRegistry the ARP can also contain an array of values to filter.
		     By default it is ['*'] --%>
		          <c:if test="${not(fn:length(att.value) eq 1 and att.value[0] eq '*')}">
		            <br/><spring:message code="jsp.sp_detail.arp.specific_values"/>
		            <ul>
		              <c:forEach items="${att.value}" var="value">
		                <li><c:out value="${value}"/></li>
		              </c:forEach>
		            </ul>
		          </c:if>
		        </li>
		      </c:forEach>
		    </ul>
		  </c:forEach>
	  </c:when>
	  <c:otherwise>
	    <c:set var="spname"><tags:providername provider="${sp}" /></c:set>
      <p><spring:message code="jsp.sp_detail.arp.noarp.text" arguments="${spname}"/></p>
	  </c:otherwise>
	</c:choose>
</div>