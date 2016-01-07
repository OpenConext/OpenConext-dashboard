<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ include file="../include.jsp" %>
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

<%--@elvariable id="sps" type="java.util.List<csa.domain.ServiceProvider>"--%>
<spring:message var="title" code="jsp.license_contact_persons.title"/>

<jsp:include page="../head.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>

<div class="column-center content-holder no-right-left">

  <section class="data-table-holder">

    <h1>${title}</h1>
    <em>#${fn:length(licenseContactPersons)} contacts</em>
    <div class="data-table-wrapper">

      <c:set var="searchPlaceholder"><spring:message code="jsp.license_contact_persons.search.placeholder" /></c:set>
      <table id="license_contact_persons_overview_table" class="table table-bordered table-striped table-above-pagination table-sortable"  data-search-placeholder="${searchPlaceholder}">
        <thead>
        <tr>
          <th><spring:message code="jsp.license_contact_persons.name"/></th>
          <th><spring:message code="jsp.license_contact_persons.email"/></th>
          <th><spring:message code="jsp.license_contact_persons.phone"/></th>
          <th><spring:message code="jsp.license_contact_persons.idp"/></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${licenseContactPersons}" var="person">
            <tr>
              <td>${person.name}</td>
              <td>${person.email}</td>
              <td>${person.phone}</td>
              <td>${person.idpEntityId}</td>
            </tr>
        </c:forEach>

        </tbody>
      </table>

    </div>
  </section>
</div>

<jsp:include page="../foot.jsp"/>
