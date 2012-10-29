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
<%@attribute name="csp" description="A CompoundServcieProvider object" type="nl.surfnet.coin.selfservice.domain.CompoundServiceProvider"
    required="true" %>
<%@attribute name="invariant" type="java.lang.Boolean" required="true" %>
<c:if test="${invariant}">
  <a href="<c:url value="/requests/question.shtml">
    <c:param name="spEntityId" value="${csp.sp.id}" />
    <c:param name="compoundSpId" value="${csp.id}" />
    </c:url>"
    title="<spring:message code="jsp.sp_detail.askquestion"/>"><spring:message code="jsp.sp_detail.askquestion"/>
  </a>
</c:if>
