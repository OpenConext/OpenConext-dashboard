<%@ include file="include.jsp" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
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
<spring:message var="title" code="jsp.notifications.title"/>
<jsp:include page="head.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>

  <div class="column-center content-holder">
      <section class="data-table-holder">
    <c:if test="${empty notificationMessage.arguments}">
      <h1><spring:message code="jsp.notifications.title"/></h1>
      <p class="notificationMessage"><spring:message code="jsp.notifications.none.present.text"/></p>
    </c:if>
    <c:if test="${not empty notificationMessage.arguments}">
      <h1><spring:message code="jsp.notifications.title"/></h1>
          <c:forEach items="${notificationMessage.messageKeys}" var="notificationMessage">
            <p><spring:message code="${notificationMessage}"/></p>
          </c:forEach>
        <div class="data-table-wrapper">

            <c:set var="searchPlaceholderNotifications"><spring:message code="jsp.notifications.search.placeholder"/></c:set>
            <table class="table table-bordered table-striped table-above-pagination table-with-statuses table-sortable"
                   id="notifications-overview-table" data-search-placeholder="${searchPlaceholderNotifications}">
              <thead>
                <tr>
                  <th><spring:message code="jsp.notifications.image"/></th>
                  <th class="html sorting_asc"><spring:message code="jsp.notifications.name"/></th>
                  <th><spring:message code="jsp.notifications.haslicense"/></th>
                  <th><spring:message code="jsp.notifications.islinked"/></th>
                </tr>
              </thead>
              <tbody>
                <c:forEach items="${notificationMessage.arguments}" var="cspArgument">
                  <c:set var="spname"><tags:providername provider="${cspArgument.sp}" /></c:set>
                  <spring:url value="/app-detail.shtml" var="detailUrl" htmlEscape="true">
                    <spring:param name="compoundSpId" value="${cspArgument.id}" />
                  </spring:url>
                  <tr>
                    <td class="notification-image">
                      <c:if test="${not empty cspArgument.appStoreLogo}">
                        <img src="<c:url value="${cspArgument.appStoreLogo}"/>" width="30" height="30" alt="">
                      </c:if>
                    </td>
                    <td>
                      <a href="${detailUrl}">
                        ${spname}
                      </a>
                    </td>
                    <td>
                      <c:choose>
                        <c:when test="${cspArgument.licenseAvailable}">
                          <i class="icon-ok"/>
                        </c:when>
                        <c:otherwise>
                          <i class="icon-remove icon-greyed-out"/>
                        </c:otherwise>
                      </c:choose>
                    </td>
                    <td>
                      <c:choose>
                        <c:when test="${cspArgument.sp.linked}">
                          <i class="icon-ok"/>
                        </c:when>
                        <c:otherwise>
                          <i class="icon-remove icon-greyed-out"/>
                        </c:otherwise>
                      </c:choose>
                    </td>
                </c:forEach>
              </tbody>
            </table>
            <hr/>
        </div>
    </c:if>
  </section>
</div>

<jsp:include page="foot.jsp"/>