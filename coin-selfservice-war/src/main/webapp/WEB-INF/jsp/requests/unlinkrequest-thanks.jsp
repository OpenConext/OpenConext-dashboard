<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ include file="../include.jsp" %>
<%--
  ~ Copyright 2012 SURFnet bv, The Netherlands
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%--@elvariable id="sp" type="nl.surfnet.coin.selfservice.domain.ServiceProvider"--%>
<%--@elvariable id="issueKey" type="java.lang.String"--%>

<c:set var="spname"><tags:providername provider="${sp}"/></c:set>

<jsp:include page="../head.jsp">
  <jsp:param name="title" value="${spname}"/>
</jsp:include>

<div class="column-center content-holder">
  <section>

    <h1><c:out value="${spname}"/></h1>

    <p>
      <spring:message code="jsp.sp_unlinkrequest.thankstext" />
    </p>

    <div class="actions">
      <spring:url value="../app-overview.shtml" var="overviewUrl" htmlEscape="true" />
      <spring:url value="../app-detail.shtml" var="detailUrl" htmlEscape="true">
        <spring:param name="compoundSpId" value="${compoundSpId}" />
      </spring:url>

      <a class="btn btn-primary btn-small" href="${overviewUrl}">
        <spring:message code="jsp.request.backtooverview" />
      </a>
      <a class="btn btn-small" href="${detailUrl}">
        <spring:message code="jsp.request.backtodetail" arguments="${spname}" />
      </a>
    </div>
  </section>
</div>

<jsp:include page="../foot.jsp"/>