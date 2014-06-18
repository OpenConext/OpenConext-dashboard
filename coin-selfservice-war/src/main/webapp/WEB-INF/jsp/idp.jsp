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

    <spring:eval expression="@applicationProperties['jsp.role.explanation.link']" var="explanationLink"/>
    <h2><spring:message code="jsp.role.information.header"/><span class="roleExplanationLink icon-info-sign"><a href="${explanationLink}" target="_blank"><spring:message code="jsp.role.explanation.linkDescription"/></a></span></h2>
    <table class="role-listing table table-striped table-above-pagination">
      <thead>
      <tr>
        <th><spring:message code="jsp.role.information.role"/></th>
        <th><spring:message code="jsp.role.information.users"/></th>
      </tr>
      </thead>
      <tbody>
        <c:forEach items="${roleAssignments}" var="entry" varStatus="vs">
          <tr>
            <td><c:out value="${entry.key}"/> </td>
            <td><c:out value="${entry.value}"/> </td>
          </tr>
        </c:forEach>

      </tbody>
    </table>
    <p/>
    <h2><spring:message code="jsp.my.idp.apps.header"/></h2>
    <table class="my-idp-apps-listing table table-striped table-above-pagination">
      <thead>
      <tr>
        <th><spring:message code="jsp.my.idp.apps.title"/></th>
        <th><spring:message code="jsp.my.idp.apps.used.by"/></th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${idpApps}" var="entry" varStatus="vs">
        <tr>
          <td><c:out value="${entry.key}"/> </td>
          <td><c:out value="${entry.value}"/> </td>
        </tr>
      </c:forEach>
        <tr>
          <td>Service Bar</td>
          <td>Erasmus, TuDelft</td>
        </tr>
        <tr>
          <td>Service Foo</td>
          <td>Radboud, UniMaas</td>
        </tr>
      </tbody>
    </table>
  </section>
  </div>

<jsp:include page="foot.jsp"/>