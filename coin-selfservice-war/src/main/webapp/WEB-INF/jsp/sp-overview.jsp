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

<c:choose>
  <c:when test="${activeSection eq 'linked-sps'}">
    <spring:message var="title" code="jsp.mysp.title"/>
  </c:when>
  <c:otherwise>
    <spring:message var="title" code="jsp.allsp.title"/>
  </c:otherwise>
</c:choose>

<jsp:include page="header.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>

<section>

  <h2>${title}</h2>

  <div class="content">

    <c:if test="${activeSection eq 'linked-sps'}">
      <div id="chart">
      </div>
    </c:if>

    <spring:message code="jsp.sp_overview.n_results" arguments="${fn:length(sps)}"/>
    <table class="table table-bordered table-striped table-above-pagination">
      <thead>
      <tr>
        <th><spring:message code="jsp.sp_overview.name"/></th>
        <th><spring:message code="jsp.sp_overview.description"/></th>
        <th></th>
        <th class="cw55 center"><spring:message code="jsp.sp_overview.active"/></th>
        <th class="cw55 small center"><spring:message code="jsp.sp_overview.actions"/></th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${sps}" var="sp">
        <c:if test="${not empty sp.id}">
          <tr>
            <td>
              <c:out default="${sp.id}" value="${sp.name}"/>
            </td>
            <td class="text-overflow"><c:out value="${fn:substring(sp.description, 0, 40)}"/></td>
            <td>
              <c:if test="${not empty sp.homeUrl}">
                <img src="${sp.logoUrl}" alt="<c:out value="${sp.name}"/>"/>
              </c:if>
            </td>
            <td class="center">
            <c:choose>
              <c:when test="${sp.linked}">
              <i class="icon-ok"></i>
              </c:when>
              <c:otherwise>
                <i class="icon-ban-circle"></i>
              </c:otherwise>
            </c:choose>
            </td>
            <td class="center">
              <spring:message var="detailTitle" code="jsp.sp_overview.detail"/>
              <spring:url value="/sp/detail.shtml" var="detailUrl" htmlEscape="true">
                <spring:param name="spEntityId" value="${sp.id}" />
              </spring:url>
              <a href="${detailUrl}" rel="tooltip" data-type="info"
                 title="${detailTitle}"><i class="icon-info-sign"></i></a>
            </td>
          </tr>
        </c:if>
      </c:forEach>

      </tbody>
    </table>


  </div>
</section>


<jsp:include page="footer.jsp" >
  <jsp:param name="chart" value="${activeSection eq 'linked-sps'}"/>
</jsp:include>