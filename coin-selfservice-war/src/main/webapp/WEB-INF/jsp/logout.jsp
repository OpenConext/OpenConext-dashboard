<%@ include file="include.jsp" %>
<%--
  Copyright 2012 SURFnet bv, The Netherlands

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  --%>

<spring:message code="jsp.logout.title" var="title"/>

<jsp:include page="header.jsp">
  <jsp:param name="activeSection" value="linked-sps"/>
  <jsp:param name="title" value="${title}"/>
</jsp:include>

<section>

  <h2><c:out value="${title}"/></h2>

  <div class="row">
    <div class="span12">
      <div class="content">
        <p><spring:message code="jsp.logout.status"/></p>
        <p><spring:message code="jsp.logout.closebrowser" htmlEscape="false"/></p>
      </div>
    </div>
  </div>
</section>


<jsp:include page="footer.jsp"/>