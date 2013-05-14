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

<%--@elvariable id="sps" type="java.util.List<nl.surfnet.coin.selfservice.domain.ServiceProvider>"--%>
<spring:message var="title" code="jsp.allsplmng.title"/>

<jsp:include page="../head.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>

<div class="column-center content-holder no-right-left">

  <section class="data-table-holder">

    <h1>${title}</h1>
    <div class="data-table-wrapper">

      <c:set var="searchPlaceholder"><spring:message code="jsp.search.placeholder.sp" /></c:set>
      <table id="sp_overview_table" class="table table-bordered table-striped table-above-pagination table-sortable"  data-search-placeholder="${searchPlaceholder}">
        <thead>
        <tr>
          <th><spring:message code="jsp.lmng_binding_overview.name"/></th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${sps}" var="sp" varStatus="status">
            <spring:url value="compoundSp-detail.shtml" var="detailUrl" htmlEscape="true">
              <spring:param name="spEntityId" value="${sp.id}" />
              <spring:param name="lmngActive" value="false" />
            </spring:url>
            <tr>
              <td title="${sp.id} - ${fn:substring(sp.descriptions[locale.language], 0, 40)}">
                <a href="${detailUrl}">
              		<tags:providername provider="${sp}"/>
              	</a>
              </td>
            </tr>
        </c:forEach>

        </tbody>
      </table>

    </div>
  </section>
</div>

<jsp:include page="../foot.jsp"/>