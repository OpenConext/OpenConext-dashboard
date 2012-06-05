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
  <jsp:param name="activeSection" value="home"/>
  <jsp:param name="title" value="${title}"/>
</jsp:include>
<section>
  <div class="content">

    <h3>${title}</h3>

    Actions!

    <table>
      <c:forEach items="${actionList}" var="action">
        <tr>
          <%--@elvariable id="action" type="nl.surfnet.coin.selfservice.domain.Action"--%>
          <td>${action.jiraKey}</td>
          <td>${action.idp}</td>
          <td>${action.sp}</td>
          <td>${action.institutionId}</td>
          <td>${action.userId}</td>
          <td>${action.userName}</td>
          <td>${action.status}</td>
          <td>${action.type}</td>
        </tr>
      </c:forEach>
    </table>


  </div>
</section>
<jsp:include page="footer.jsp"/>