<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%--
  ~ Copyright 2013 SURFnet bv, The Netherlands
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%@ attribute required="true" type="java.lang.String" name="spName" %>
<%@ attribute required="true" type="java.lang.Boolean" name="hasServiceDescription" %>
<%@ attribute required="true" type="java.lang.Boolean" name="hasConnectButton" %>
<c:set var="maxLength">
  <c:choose>
    <c:when test="${hasServiceDescription and hasConnectButton}">43</c:when>
    <c:when test="${hasServiceDescription or hasConnectButton}">48</c:when>
    <c:otherwise>53</c:otherwise>
  </c:choose>
</c:set>
<c:choose>
  <c:when test="${maxLength > fn:length(spName)}"><c:out value="${spName}" /></c:when>
  <c:otherwise><c:out value="${fn:substring(spName, 0, maxLength)}" />...</c:otherwise>
</c:choose>
