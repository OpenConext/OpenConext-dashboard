<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="input" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../include.jsp" %>
<%--
  Copyright 2012 SURFnet bv, The Netherlands

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not <spring:message code="jsp.compound_sp_select_source"/> file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  --%>

<%--@elvariable id="facet" type="csa.model.Facet"--%>


<spring:message var="title" code="jsp.taxonomy.overview.title"/>
<jsp:include page="../head.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>

<div class="column-center content-holder no-right-left">

  <section>
    <h1>${title}<i class="inlinehelp icon-question-sign" data-title="${title}" data-placement="bottom" data-content="<spring:message htmlEscape="true" code="jsp.taxonomy.overview.title.help" />"></i></h1>

    <div id="csp-taxonomy-overview" class="content csp-taxonomy-overview" data-token-check="${tokencheck}">
      <table id="csp-taxonomy-overview-table">
        <thead>
          <tr>
          <th class="service">Service</th>
          <c:forEach items="${facets}" var="facet">
            <th class="skew"><span>${facet.name}</span></th>
              <c:forEach items="${facet.facetValues}" var="facetValue">
                <th class="skew"><span class="facet-value">${facetValue.value}</span></th>
              </c:forEach>
          </c:forEach>
          </tr>
        </thead>
        <tbody>
          <c:forEach items="${csps}" var="csp">
            <tr>
              <c:set var="serviceDescription"><tags:providername provider="${csp.sp}" /></c:set>
              <td class="csp-name">${serviceDescription}</td>
              <c:forEach items="${facets}" var="facet">
                <td></td>
                  <c:forEach items="${facet.facetValues}" var="facetValue">
                    <c:set var="checked">
                      <tags:facet-value-present facetValue="${facetValue}" csp="${csp}"/>
                    </c:set>
                    <td><input class="facet-value-csp-checkbox" type="checkbox" ${checked} data-facet-value-id="${facetValue.id}" data-csp-id="${csp.id}"></td>
                  </c:forEach>
              </c:forEach>
            </tr>
          </c:forEach>
        </tbody>
      </table>
  </div>
  </section>
</div>

<jsp:include page="../foot.jsp"/>
