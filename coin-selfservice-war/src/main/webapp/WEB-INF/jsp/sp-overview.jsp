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

<jsp:include page="header.jsp">
  <jsp:param name="title" value="Service providers"/>
</jsp:include>

<section>

  <h2>Service Providers</h2>

  <div class="content">
    <c:out value="${fn:length(sps)}" /> results.
    <table class="table table-bordered table-striped table-above-pagination">
      <thead>
      <tr>
        <th>Name</th>
        <th>Description</th>
        <th></th>
        <th class="cw55 center">Active</th>
        <th class="cw55 small center">Actions</th>
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
              <a href="<c:url value="/sp/detail.shtml">
                <c:param name="spEntityId" value="${sp.id}" />
              </c:url>"
                 rel="tooltip" data-type="info"
                 title="Detail"><i class="icon-info-sign"></i></a>
            </td>
          </tr>
        </c:if>
      </c:forEach>

      </tbody>
    </table>


  </div>
</section>


<jsp:include page="footer.jsp" />