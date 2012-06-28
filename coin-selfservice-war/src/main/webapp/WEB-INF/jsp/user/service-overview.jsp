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

<%--@elvariable id="sps" type="java.util.List<nl.surfnet.coin.selfservice.domain.ServiceProvider>"--%>
<spring:message var="title" code="jsp.allsp.title"/>

<jsp:include page="../header.jsp">
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
        <th></th>
        <th></th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${sps}" var="sp">
        <c:if test="${not empty sp.id}">
          <spring:url value="/user/service/detail.shtml" var="detailUrl" htmlEscape="true">
            <spring:param name="spEntityId" value="${sp.id}" />
          </spring:url>
          <tr>
            <td><a href="${detailUrl}"><tags:providername provider="${sp}"/></a>
            </td>
            <td class="text-overflow"><c:out value="${fn:substring(sp.descriptions[locale.language], 0, 40)}"/></td>
            <%-- TODO wait for redesign with the logo --%>
            <%--<td>
              <c:if test="${not empty sp.logoUrl}">
                <img src="${sp.logoUrl}" alt="<c:out value=""/>"/>
              </c:if>
            </td>--%>
            <td class="center">
              <a href="${detailUrl}" class="btn btn-info btn-small cw75">
                <i class="icon-info-sign"></i> <spring:message code="jsp.sp_overview.detail"/>
              </a>
            </td>
            <td class="center">
              <c:if test="${not empty sp.urls[locale.language]}">
                <a href="<c:out value="${sp.urls[locale.language]}"/>" class="btn btn-primary btn-small cw85" target="_blank">
                  <i class="icon-external-link"></i> <spring:message code="jsp.sp_detail.serviceurl"/>
                </a>
              </c:if>
            </td>
          </tr>
        </c:if>
      </c:forEach>

      </tbody>
    </table>


  </div>
</section>


<jsp:include page="../footer.jsp">
  <jsp:param name="datatables" value="true"/>
</jsp:include>