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

<%--@elvariable id="sps" type="java.util.List<nl.surfnet.coin.selfservice.domain.ServiceProvider>"--%>
<spring:message var="title" code="jsp.allsp.title"/>

<jsp:include page="header.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>

<section>

  <h2>${title}</h2>

  <div class="content">

    <table id="sp_overview_table" class="table table-bordered table-striped table-above-pagination">
      <thead>
      <tr>
        <th><spring:message code="jsp.sp_overview.name"/></th>
        <th><spring:message code="jsp.sp_overview.description"/></th>
        <%--<th></th>--%>
        <th class="center" data-filtertype="select"><spring:message code="jsp.sp_overview.status"/></th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${sps}" var="sp">
        <c:if test="${not empty sp.id}">
          <spring:url value="/sp/detail.shtml" var="detailUrl" htmlEscape="true">
            <spring:param name="spEntityId" value="${sp.id}" />
          </spring:url>
          <tr>
            <td><a href="${detailUrl}"><c:out default="${sp.id}" value="${sp.name}"/></a>
            </td>
            <td class="text-overflow"><c:out value="${fn:substring(sp.description, 0, 40)}"/></td>
            <%--<td>
              <c:if test="${not empty sp.logoUrl}">
                <img src="${sp.logoUrl}" alt="<c:out value="${sp.name}"/>"/>
              </c:if>
            </td>--%>
            <td class="center">
              <a href="${detailUrl}" class="btn btn-primary btn-small cw65">
            <c:choose>
              <c:when test="${sp.linked}">
              <i class="icon-ok"></i> <spring:message code="jsp.sp_overview.action-linked" />
              </c:when>
              <c:otherwise>
                <i class="icon-plus"></i> <spring:message code="jsp.sp_overview.action-dolink" />
              </c:otherwise>
            </c:choose>
                </a>
            </td>
          </tr>
        </c:if>
      </c:forEach>

      </tbody>
    </table>


  </div>
</section>


<jsp:include page="footer.jsp">
  <jsp:param name="datatables" value="true"/>
</jsp:include>