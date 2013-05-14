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

<%--@elvariable id="compoundSp" type="nl.surfnet.coin.selfservice.domain.CompoundServiceProvider"--%>


<c:set var="title">
  <tags:providername provider="${compoundSp.sp}"/>
</c:set>

<jsp:include page="../head.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>
<div class="wrapper has-left">

  <section class="taxonomy-sp-configuration">

    <div id="taxonomy_sp_configuration" class="content column-left" data-token-check="${tokencheck}" data-csp-id="${compoundSp.id}">
        <h3>The selected labels</h3>
        <section id="selected_facets">
          <c:if test="${empty compoundSp.facetValues}">
            <span id="no_labels_selected">No labels selected</span>
          </c:if>
            <ul id="selected_facet_values" class="nav facet-values">
              <c:if test="${not empty compoundSp.facetValues}">
                <c:forEach items="${compoundSp.facetValues}" var="facetValue">
                  <li data-facet-value-id="${facetValue.id}">
                    <a id="facet_value_pointer_${facetValue.id}" href="#" class="local-link"><c:out value='${facetValue.value}'/></a>
                  </li>
                </c:forEach>
              </c:if>
            </ul>
        </section>
    </div>

    <div class="accordion column-center" id="fieldaccordion">
      <h1>${title}
        <i class="inlinehelp icon-question-sign" data-title="${title}" data-placement="bottom" data-content="<spring:message htmlEscape="true" code="jsp.compoundsp_taxonomy_configuration.title.help" />"></i>
      </h1>

      <h3>The showroom taxonomy with all available categorized labels</h3>
        <c:forEach items="${facets}" var="facet">
          <div class="accordion-group" data-facet-id="${facet.id}">
            <div class="accordion-heading">
                <a class="accordion-toggle with-options" data-toggle="collapse" data-parent="#fieldaccordion" href="#${facet.id}-body">
                  <i class='icon-arrow-down'></i><c:out value='${facet.name}'/>
                </a>
            </div>
            <div id="${facet.id}-body" class="accordion-body collapse">
              <div class="accordion-inner">
                <ul class="nav facet-values">
                  <c:forEach items="${facet.facetValues}" var="facetValue">
                    <li data-facet-value-id="${facetValue.id}">
                      <i class="icon-arrow-right" style="display:none;"></i>
                      <c:set var="linkedClasses">
                        <tags:facet-value-linked facetValue="${facetValue}" csp="${compoundSp}"/>
                      </c:set>
                      <c:set var="iconClass">
                        <tags:facet-value-icon facetValue="${facetValue}" csp="${compoundSp}"/>
                      </c:set>
                      <label id="link_facet_value_${facetValue.id}" class="btn btn-modest ${linkedClasses}"><i class="${iconClass} icon-white"></i>
                        <span><c:out value='${facetValue.value}'/></span>
                      </label>
                    </li>
                  </c:forEach>
                </ul>
              </div>
            </div>
          </div>
        </c:forEach>
      </div>
  </section>
</div>

<jsp:include page="../foot.jsp"/>