<%@ include file="../include.jsp" %>
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
<jsp:useBean id="actionList" scope="request" type="java.util.List"/>
<spring:message var="title" code="jsp.notifications.title"/>
<jsp:include page="../head.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>

  <div class="column-center content-holder no-right-left">
        <section class="data-table-holder">
      <c:if test="${empty actionList}">
        <h1><spring:message code="jsp.requests-overview.title"/></h1>
        <p class="notificationMessage"><spring:message code="jsp.requests-overview.none"/></p>
      </c:if>

      <c:if test="${not empty actionList}">
        <section class="data-table-holder">

          <h1><spring:message code="jsp.requests-overview.title"/></h1>
          <div class="data-table-wrapper">
            <c:set var="searchPlaceholderRequests"><spring:message code="jsp.requests-overview.search.placeholder"/></c:set>
            <table class="table table-bordered table-striped table-above-pagination table-with-statuses table-sortable"
                   id="request-overview-table" data-search-placeholder="${searchPlaceholderRequests}"
              >
              <thead>
                <tr>
                  <th><spring:message code="jsp.requests-overview.date"/></th>
                  <th><spring:message code="jsp.requests-overview.type"/></th>
                  <th><spring:message code="jsp.requests-overview.status"/></th>
                  <th><spring:message code="jsp.requests-overview.by"/></th>
                </tr>
              </thead>
              <tbody>
                <c:forEach items="${actionList}" var="action">
                  <c:set var="actionType">
                    <c:choose>
                      <c:when test="${action.type == 'LINKREQUEST'}"><spring:message code="jsp.actions.typeLinkrequest" arguments="${action.sp.name}" /></c:when>
                      <c:when test="${action.type == 'UNLINKREQUEST'}"><spring:message code="jsp.actions.typeUnlinkrequest" arguments="${action.sp.name}" /></c:when>
                    </c:choose>
                  </c:set>
                  <tr>
                    <td>
                      <fmt:formatDate value="${action.requestDate}" pattern="dd-MM-yyyy"/>
                    </td>
                    <td>
                      <c:out value="${actionType}"/>
                    </td>
                    <td>
                      <c:choose>
                        <c:when test="${action.status eq 'CLOSED'}">
                          <spring:message code="jsp.requests-overview.tooltip-closed" />
                        </c:when>
                        <c:when test="${action.status eq 'OPEN'}">
                          <spring:message code="jsp.requests-overview.tooltip-open" />
                        </c:when>
                      </c:choose>
                    </td>
                    <td>
                      <c:out value="${action.userName}"/>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </section>
        </c:if> 
      </div>

<jsp:include page="../foot.jsp"/>