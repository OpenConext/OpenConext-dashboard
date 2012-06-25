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
<spring:message var="title" code="jsp.home.title"/>
<jsp:include page="../header.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>
<section>
  <div class="content">

    <h3><spring:message code="jsp.actions.title"/></h3>

    <spring:message code="jsp.actions.intro"/>

    <table class="table table-bordered table-striped table-above-pagination">
      <thead>
      <th class="cw55 small"><spring:message code="jsp.actions.issue"/></th>
      <th class="cw55 small"><spring:message code="jsp.actions.type"/></th>
        <th class="cw75 small"><spring:message code="jsp.actions.date"/></th>
        <th class="small"><spring:message code="jsp.actions.by"/></th>
        <th><spring:message code="jsp.actions.sp"/></th>
        <th class="cw55 small center"><spring:message code="jsp.actions.status"/></th>
      </thead>
      <tbody>
      <c:forEach items="${actionList}" var="action">
        <c:set var="actionType">
          <c:choose>
            <c:when test="${action.type == 'LINKREQUEST'}"><spring:message code="jsp.actions.typeLinkrequest"/></c:when>
            <c:when
                test="${action.type == 'UNLINKREQUEST'}"><spring:message code="jsp.actions.typeUnlinkrequest"/></c:when>
          </c:choose>
        </c:set>
        <tr class="rowdetails">
          <%--@elvariable id="action" type="nl.surfnet.coin.selfservice.domain.Action"--%>
          <td><c:out value="${action.jiraKey}"/></td>
          <td><c:out value="${actionType}"/></td>
          <td><fmt:formatDate value="${action.requestDate}" pattern="yyyy-MM-dd"/></td>
          <td><c:out value="${action.userName}"/></td>
          <td><tags:providername provider="${action.sp}"/></td>
          <td class="center">
            <c:choose>
              <c:when test="${action.status eq 'CLOSED'}">
                <i class="icon-ok" rel="tooltip"
                   data-original-title="<spring:message code="jsp.actions.tooltip-closed" />" data-type="info"></i>
              </c:when>
              <c:when test="${action.status eq 'OPEN'}">
                <i class="icon-time" rel="tooltip" data-original-title="<spring:message code="jsp.actions.tooltip-open"/>" data-type="info"></i>
              </c:when>
            </c:choose>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>

  </div>
</section>
<jsp:include page="../footer.jsp"/>