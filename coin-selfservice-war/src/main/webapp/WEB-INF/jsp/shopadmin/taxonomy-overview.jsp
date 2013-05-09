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
    <h1>${title}<i class="inlinehelp icon-question-sign" data-title="${title}" data-placement="bottom" data-content="<spring:message htmlEscape="true" code="jsp.taxonomy_configuration.title.help" />"></i></h1>

    <div id="taxonomy" class="content" data-token-check="${tokencheck}">
      <div class="accordion" id="fieldaccordion">
        <c:forEach items="${facets}" var="facet">
          <div class="accordion-group" data-facet-id="${facet.id}">
            <div class="accordion-heading">
                <a class="accordion-toggle with-options" data-toggle="collapse" data-parent="#fieldaccordion" href="#${facet.id}-body">
                  <i class='icon-arrow-down'></i><c:out value='${facet.name}'/>
                </a>
                <div class="options">
                  <button type="button" class="btn edit-facet"><i class="icon-edit icon-white"></i></button>
                  <button type="button" class="btn remove-facet"><i class="icon-trash"></i></button>
                </div>
            </div>
            <div id="${facet.id}-body" class="accordion-body collapse">
              <div class="accordion-inner">
                <ul class="nav facet-values">
                  <c:forEach items="${facet.facetValues}" var="facetValue">
                    <li data-facet-value-id="${facetValue.id}"><span><c:out value='${facetValue.value}'/></span>
                      <div class="options inner">
                        <button type="button" class="btn edit-facet-value"><i class="icon-edit"></i></button>
                        <button type="button" class="btn remove-facet-value"><i class="icon-trash"></i></button>
                      </div>
                    </li>
                  </c:forEach>
                </ul>
                <a class="btn btn-primary btn-modest" href="#" id="add_facet_value_${facet.id}"> Add Facet Value <i class="icon-plus icon-white"></i></a>
              </div>
            </div>
          </div>
        </c:forEach>
      </div>
      <a class="btn btn-primary-alt btn-modest" href="#" id="add_facet"><span> Add Facet </span><i class="icon-plus icon-white"></i></a>
  </div>
  </section>
</div>

<article id="new_facet_template" style="display: none;">
  <div class="accordion-group">
    <div class="accordion-heading">
      <a class="accordion-toggle with-options" data-toggle="collapse" data-parent="#fieldaccordion" href="" style="display: none;">
      </a>
      <input type="text" class="inline-edit" value="">
      <div class="options">
        <button type="button" class="btn edit-facet"><i class="icon-edit icon-white"></i></button>
        <button type="button" class="btn remove-facet"><i class="icon-trash"></i></button>
      </div>
    </div>
    <div id="" class="accordion-body collapse">
      <div class="accordion-inner">
        <ul class="nav facet-values">
        </ul>
        <a class="btn btn-primary btn-modest" href="#" id="add_facet_value-X"><span> Add Facet Value </span><i class="icon-plus icon-white"></i></a>
      </div>
    </div>
  </div>
</article>

<article id="new_facet_value_template" style="display: none;">
  <li>
    <span style="display: none;"></span>
    <input type="text" class="inline-edit">
    <div class="options inner">
      <button type="button" class="btn edit-facet-value"><i class="icon-edit"></i></button>
      <button type="button" class="btn remove-facet-value"><i class="icon-trash"></i></button>
    </div>
  </li>
</article>


<jsp:include page="../foot.jsp"/>