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
<spring:message var="title" code="jsp.home.title"/>
<jsp:include page="header.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>
<section>
  <div class="content">

    <h3>${title}</h3>

    <sec:authorize access="hasRole('ROLE_IDP_SURFCONEXT_ADMIN')">
      <p><spring:message code="jsp.home.adminintro"/></p>
    </sec:authorize>

    <sec:authentication property="principal.attributeMap" scope="request" var="attributeMap"/>

    <p><spring:message code="jsp.home.attributes.header"/><p>
    <table class="table table-bordered table-striped table-above-pagination">
      <thead>
      <tr>
        <th><spring:message code="jsp.person.attributes.key"/></th>
        <th><spring:message code="jsp.person.attributes.value"/></th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${attributeMap}" var="attribute">
        <tr>
          <td><tags:arp-attribute-info attributeKey="${attribute.key}"/></td>
          <td>
            <c:choose>
              <c:when test="${fn:length(attribute.value) gt 1}">
                <ul>
                  <c:forEach items="${attribute.value}" var="value">
                    <li><c:out value="${value}"/></li>
                  </c:forEach>
                </ul>
              </c:when>
              <c:otherwise>
                <c:out value="${attribute.value[0]}"/>
              </c:otherwise>
            </c:choose>
          </td>
        </tr>

      </c:forEach>
      </tbody>
    </table>

  </div>
</section>
<jsp:include page="footer.jsp"/>