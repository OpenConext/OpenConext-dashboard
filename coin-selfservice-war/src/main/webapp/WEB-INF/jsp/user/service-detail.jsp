<%@ include file="../include.jsp" %>
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

<%--@elvariable id="sp" type="nl.surfnet.coin.selfservice.domain.ServiceProvider"--%>

<c:choose>
  <c:when test="${empty sp.name}"><c:set var="spname" value="${sp.id}"/></c:when>
  <c:otherwise><c:set var="spname" value="${sp.name}"/></c:otherwise>
</c:choose>

<jsp:include page="../header.jsp">
  <jsp:param name="title" value="${spname}"/>
</jsp:include>

<section>

  <h2><c:out value="${spname}"/></h2>

  <div class="row">
    <c:if test="${not empty sp.logoUrl}">
      <div class="span2">
        <div class="content">
          <p>
            <c:set var="logo"><img alt="" style="float:left" src="<c:out value="${sp.logoUrl}"/>"/>
            </c:set>
            <c:choose>
              <c:when test="${not empty sp.homeUrl}">
                <a href="<c:out value="${sp.homeUrl}"/>" target="_blank">${logo}</a>
              </c:when>
              <c:otherwise>${logo}</c:otherwise>
            </c:choose>
          </p>
        </div>
      </div>
    </c:if>
    <%-- bit ugly: if this span does not exist, the right column is pushed to the left --%>
    <div class="span6">
      <div class="content">
        <c:if test="${not empty sp.description}">
          <p>
            <c:out value="${sp.description}"/>
          </p>
        </c:if>
        <c:set var="sp" value="${sp}" scope="request" />
        <jsp:include page="../arp.jsp" />
      </div>
    </div>
    <div class="span4">
      <div class="content">

        <h3><spring:message code="jsp.sp_detail.moreinfo"/></h3>
        <ul class="unstyled">
          <c:if test="${not empty sp.homeUrl}">
            <li>
              <a href="<c:out value="${sp.homeUrl}"/>" target="_blank"><spring:message
                  code="jsp.sp_detail.website"/></a> <i class="icon-external-link"></i>
            </li>
          </c:if>
          <c:if test="${not empty sp.eulaURL}">
            <li>
              <a href="<c:out value="${sp.eulaURL}"/>" target="_blank"><spring:message code="jsp.sp_detail.eula"/></a>
              <i class="icon-external-link"></i>
            </li>
          </c:if>
        </ul>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="span12">
      <div class="content">
        <div id="chart"></div>
      </div>
    </div>
  </div>
</section>


<jsp:include page="../footer.jsp">
  <jsp:param name="chart" value="${sp.linked eq true}"/>
</jsp:include>
