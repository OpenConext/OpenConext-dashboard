<%@ include file="/WEB-INF/jsp/include.jsp" %>
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
<%@attribute name="enVariant" type="java.lang.String" required="true" %>
<%@attribute name="nlVariant" type="java.lang.String" required="true" %>
<%@attribute name="escapeXml" type="java.lang.Boolean" required="false" %>

<c:if test="${empty escapeXml}">
<c:set var="escapeXml" >false</c:set>
</c:if>  

<c:choose>
  <c:when test="${locale.language eq 'nl'}">
    <c:out escapeXml="${escapeXml}" value="${nlVariant}" />
  </c:when>
  <c:otherwise>
    <c:out escapeXml="${escapeXml}" value="${enVariant}" />
  </c:otherwise>
</c:choose>