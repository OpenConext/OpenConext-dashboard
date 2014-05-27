<%@ include file="include.jsp" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
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
<spring:message var="title" code="jsp.idp.title"/>
<jsp:include page="head.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>

  <div class="column-center content-holder no-right-left">
    <section class="data-table-holder">

    <h1>${title}</h1>

    <p><tags:context-specific messageKey="jsp.role.information.header" isDashBoard="${isDashBoard}"/></p>
    <table class="table table-bordered table-striped table-above-pagination">
      <thead>
      <tr>
        <th><spring:message code="jsp.role.information.user"/></th>
        <th><spring:message code="jsp.role.information.role"/></th>
      </tr>
      </thead>
      <tbody>
        <c:forEach items="${maintainers.sabPersons}" var="sabPerson" varStatus="vs">
          <tr>
            <td><c:out value="${sabPerson.fullname()}"/> </td>
            <td><c:out value="${maintainers.role}"/> </td>
          </tr>
        </c:forEach>
        <c:forEach items="${responsibles.sabPersons}" var="sabPerson" varStatus="vs">
          <tr>
            <td><c:out value="${sabPerson.fullname()}"/> </td>
            <td><c:out value="${responsibles.role}"/> </td>
          </tr>
        </c:forEach>
      </tbody>
    </table>


  </section>
  </div>

<jsp:include page="foot.jsp"/>