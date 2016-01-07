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


<spring:message var="title" code="jsp.taxonomy_translations.title"/>
<jsp:include page="../head.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>

<div class="column-center content-holder no-right-left">

  <section class="taxonomy-translations">
    <h1>${title}<i class="inlinehelp icon-question-sign" data-title="${title}" data-placement="bottom" data-content="<spring:message htmlEscape="true" code="jsp.taxonomy_translations.title.help" />"></i></h1>

    <div id="taxonomy_translations" class="content" data-token-check="${tokencheck}">
      <div class="accordion" id="fieldaccordion">
        <c:forEach items="${facets}" var="facet">
          <div class="accordion-group" data-multilingual-string-id="${facet.multilingualString.id}">
            <div class="accordion-heading">
                <a class="accordion-toggle with-options" data-toggle="collapse" data-parent="#fieldaccordion" href="#${facet.id}-body">
                  <i class='icon-arrow-down'></i><c:out value='${facet.name}'/>
                </a>
                <div class="options">
                      <c:forEach items="${facet.multilingualString.allowedLocalizedStrings}" var="localizedString">
                        <span class="label label-info">${localizedString.locale}</span>
                        <input id="facet_${facet.id}_loc_${localizedString.locale}" type='text' class='inline-edit'
                                data-localized-string-id="${localizedString.id}"
                                data-multilingual-string-id="${localizedString.multilingualString.id}"
                                data-locale-value="${localizedString.locale}"
                                value="${localizedString.value}" disabled>
                        <button type="button" class="btn edit-facet-translation"><i class="icon-edit icon-white"></i></button>
                      </c:forEach>
                </div>
            </div>
            <div id="${facet.id}-body" class="accordion-body collapse">
              <div class="accordion-inner">
                <ul class="nav facet-values">
                  <c:forEach items="${facet.facetValues}" var="facetValue">
                    <li class="facet-value-translations" data-facet-value-id="${facetValue.id}"><span><c:out value='${facetValue.value}'/></span>
                      <div class="options inner">
                            <c:forEach items="${facetValue.multilingualString.allowedLocalizedStrings}" var="localizedString">
                              <input id="facetvalue_${facetValue.id}_loc_${localizedString.locale}" type='text' class='inline-edit'
                                      data-localized-string-id="${localizedString.id}"
                                      data-multilingual-string-id="${localizedString.multilingualString.id}"
                                      data-locale-value="${localizedString.locale}"
                                      value="${localizedString.value}" disabled>
                              <button type="button" class="btn edit-facet-translation"><i class="icon-edit icon-white"></i></button>
                            </c:forEach>
                      </div>
                    </li>
                  </c:forEach>
                </ul>
              </div>
            </div>
          </div>
        </c:forEach>
      </div>
  </div>
  <nav class="nav-divider">
    <a class="btn btn-primary-alt btn-modest" href="taxonomy-overview.shtml" ><i class="icon-angle-left icon-white"></i><span> Go back to the taxonomy overview </span></a>
  </nav>
  </section>
</div>

<jsp:include page="../foot.jsp"/>
