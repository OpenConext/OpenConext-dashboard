<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
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

<spring:message var="title" code="jsp.stats.title"/>

<jsp:include page="../head.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>

<div class="column-center content-holder">

  <section class="data-table-holder">

    <h1>${title}</h1>
    <%-- <c:if test="${param.chart eq true}"> --%>
    <tags:sp_renderchart/>
    <%-- </c:if> --%>
    <br/>
    <%-- <c:if test="${param.datatables eq true}"> --%>
    <tags:sp_renderdatatables columnFilter="${param.columnFilter}"/>
    <%-- </c:if> --%>

  </section>
</div>

<jsp:include page="../foot.jsp">
  <jsp:param name="datatables" value="true"/>
</jsp:include>