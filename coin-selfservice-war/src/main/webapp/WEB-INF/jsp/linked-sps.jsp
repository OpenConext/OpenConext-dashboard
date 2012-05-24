<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>
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
    <jsp:param name="activeSection" value="linked-sps" />
</jsp:include>

<section>

  <h2>Linked Service Providers</h2>

  <div class="content">

    This is a list of Service Providers that have been linked to your Identity Provider:

    <table class="table table-bordered table-striped table-above-pagination">
      <thead>
      <tr>
        <th>Name</th>
        <th>Description</th>
        <%--<th>Since</th>--%>
        <th></th>
        <th class="cw55 center">Enabled</th>
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
            <td><c:out value="${sp.description}"/></td>
            <%--<td>05/21/2012</td>--%>
            <td><c:if test="${not empty sp.homeUrl}"><img src="${sp.homeUrl}" alt="<c:out value="${sp.name}"/>"/></c:if></td>
            <td class="center"><i class="icon-ok"></i></td>
            <td class="center"><i class="icon-ok"></i></td>
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