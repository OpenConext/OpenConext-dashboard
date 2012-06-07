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
<spring:message var="title" code="jsp.home.title"/>
<jsp:include page="header.jsp">
  <jsp:param name="activeSection" value="actions"/>
  <jsp:param name="title" value="${title}"/>
</jsp:include>
<section>
  <div class="content">

    <h3><spring:message code="jsp.actions.title"/></h3>

    <spring:message code="jsp.actions.intro"/>

    <table class="table table-bordered table-striped table-above-pagination">
      <thead>
        <th class="cw55 small center"><spring:message code="jsp.actions.issue"/></th>
        <th class="cw75 small center"><spring:message code="jsp.actions.date"/></th>
        <th><spring:message code="jsp.actions.sp"/></th>
        <th class="cw55 small center"><spring:message code="jsp.actions.status"/></th>
      </thead>
      <tbody>
      <c:forEach items="${actionList}" var="action">
        <tr class="rowdetails">
          <%--@elvariable id="action" type="nl.surfnet.coin.selfservice.domain.Action"--%>
          <td class="center">${action.jiraKey}</td>
          <td class="center">${action.requestDate}</td>
          <td>${action.sp}</td>
          <td class="center">
            <c:if test="${action.status eq 'CLOSED'}">
              <i class="icon-ok"></i>
            </c:if>
          </td>
        </tr>
      </c:forEach>
      </tbody>
    </table>

  </div>
</section>
<jsp:include page="footer.jsp"/>