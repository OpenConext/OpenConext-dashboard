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

<%--@elvariable id="facet" type="nl.surfnet.coin.selfservice.domain.Facet"--%>


  <spring:message var="title" code="jsp.taxonomy.title"/>
  <jsp:include page="../head.jsp">
    <jsp:param name="title" value="${title}"/>
  </jsp:include>

<div class="column-center content-holder">
<section>

  <h1>${title}</h1>

  <div class="content">
      <div class="accordion" id="fieldaccordion">
      <c:forEach items="${facets}" var="facet">

      <div class="accordion-group">
        <div class="accordion-heading">

          <c:set var="facetId" value="${facet.id}" />
            <a class="accordion-toggle" data-toggle="collapse" data-parent="#fieldaccordion" href="#${facetId}-body">
              <c:out value='${facet.name}'/>
            </a>
            <div class="actions">
              <button type="button" id="addScope" class="btn btn-success"><i class="icon-plus icon-white"></i></button>
              <button type="button" class="btn removeScope"><i class="icon-trash"></i></button>
            </div>
        </div>
        <div id="${facetId}-body" class="accordion-body collapse">
          <div class="accordion-inner">
            <ul class="nav facet-values">
              <c:forEach items="${facet.facetValues}" var="facetValue">
                <li><c:out value='${facetValue.value}'/></li>
              </c:forEach>
            </ul>
          </div>
        </div>
      </div>
      </c:forEach>

  </div>


</div>


</section>

</div>

<jsp:include page="../foot.jsp"/>