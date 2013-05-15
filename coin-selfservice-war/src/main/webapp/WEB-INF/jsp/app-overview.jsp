<%@ include file="include.jsp"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>
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
<spring:message var="title" code="jsp.home.title" />
<jsp:include page="head.jsp">
  <jsp:param name="title" value="${title}" />
</jsp:include>
<div class="wrapper has-left app-grid-wrapper">
  <nav class="column-left">
    <section class="search-grid-app-holder">
      <input type="search" class="app-grid-search-2" placeholder="Search in applications...">
    </section>
    <c:if test="${showFacetSearch}">
    <section class="facet-search">
      <ul class="facets">
        <li class="facet-name"><spring:message code="jsp.app_overview.license_info"/></li>
          <ul class="facets-values">
            <li><a class="facet-value inactive" data-facet-search-term="licensed" href="#"><spring:message code="jsp.app_overview.license"/> <span>(${licensedCount})</span></a></li>
            <li><a class="facet-value inactive" data-facet-search-term="not-licensed" href="#"><spring:message code="jsp.app_overview.no_license"/> <span>(${notLicensedCount})</span></a></li>
          </ul>
        <c:if test="${facetConnectionVisible}">
          <li class="facet-name"><spring:message code="jsp.app_overview.connection_info"/></li>
            <ul class="facets-values">
              <li><a class="facet-value inactive" data-facet-search-term="connected" href="#"><spring:message code="jsp.app_overview.connection"/> <span>(${connectedCount})</span></a></li>
              <li><a class="facet-value inactive" data-facet-search-term="not-connected" href="#"><spring:message code="jsp.app_overview.no_connection"/> <span>(${notConnectedCount})</span></a></li>
            </ul>
        </c:if>
        <c:if test="${facetsUsed}">
          <c:forEach items="${facets}" var="facet">
            <c:if test="${facet.usedFacetValues}">
              <li class="facet-name">${facet.name}</li>
              <ul class="facets-values">
                <c:forEach items="${facet.facetValues}" var="facetValue">
                  <c:if test="${facetValue.count gt 0}">
                    <li><a class="facet-value inactive" data-facet-search-term="${facetValue.searchValue}" href="#">${facetValue.value} <span>(${facetValue.count})</span></a></li>
                  </c:if>
                </c:forEach>
              </ul>
            </c:if>
          </c:forEach>
        </c:if>
      </ul>
    </section>
    </c:if>

  </nav>
  <div class="column-center content-holder app-grid-holder">
    <h1 class="hidden-phone">${title}<i class="inlinehelp icon-question-sign" data-title="${title}" data-placement="bottom" data-content="<spring:message htmlEscape="true" code="jsp.appoverview.help" />"></i></h1>
    <div class="view-option">
      <c:set var="isCard" value="${view eq 'card'}" />
      <spring:url value="app-overview.shtml" var="cardUrl" htmlEscape="true">
        <spring:param name="view" value="card" />
      </spring:url>
      <a href="${isCard ? '#' : cardUrl}" class="${isCard ? 'disabled' : ''} card-view"><spring:message code="jsp.app_overview.card_view"/></a>
      <spring:url value="app-overview.shtml" var="listUrl" htmlEscape="true">
        <spring:param name="view" value="list" />
      </spring:url>
      <a href="${isCard ? listUrl : '#'}" class="${isCard ? '' : 'disabled'}"><spring:message code="jsp.app_overview.list_view"/></a>
    </div>
    <section>
    <div style="padding-top: 10px;">
    <div>
      <ul class="${view}-view app-grid ${filterAppGridAllowed == true ? 'filters-available' : ''} ${lmngActive == true ? 'lmng-active' : ''}">
      <c:forEach items="${compoundSps}" var="compoundSp">
            <c:if test="${not empty compoundSp.id}">
              <c:set var="serviceDescription"><tags:locale-specific nlVariant="${compoundSp.serviceDescriptionNl}" enVariant="${compoundSp.serviceDescriptionEn}" /></c:set>
              <c:set var="showConnectButton" value="${applyAllowed and (not compoundSp.sp.linked)}" />
              <li class="${view}-view ${compoundSp.sp.linked ? "connected" : "not-connected"} ${compoundSp.articleLicenseAvailable ? "licensed" : "not-licensed"} ${compoundSp.searchFacetValues}" data-id="${compoundSp.id}">
                <spring:url value="app-detail.shtml" var="detailUrl" htmlEscape="true">
                  <spring:param name="compoundSpId" value="${compoundSp.id}" />
                </spring:url>

                <c:set var="spTitle">
                  <tags:providername provider="${compoundSp.sp}" />
                </c:set>
                <h2>
                  <a href="${detailUrl}">
                    <tags:truncatedSpName
                        spName="${spTitle}"
                        hasServiceDescription="${not empty serviceDescription}"
                        hasConnectButton="${showConnectButton}" />
                  </a>
                </h2>
                  <c:if test="${!isCard}">
                  <div class="app-meta-cta">
                    <c:if test="${not empty compoundSp.appUrl}">
                      <a href="${compoundSp.appUrl}" target="_blank" rel="tooltip" title="<spring:message code="jsp.sp_overview.gotoapp" />">
                        <i class="icon-external-link"></i>
                      </a>
                    </c:if>
                    <c:if test="${showConnectButton and !isCard}">
                        <a href="<c:url value="/requests/linkrequest.shtml">
                                <c:param name="spEntityId" value="${compoundSp.sp.id}" />
                                <c:param name="compoundSpId" value="${compoundSp.id}" />
                              </c:url>" target="_blank" rel="tooltip" title="<spring:message code="jsp.sp_detail.requestlink"/>">
                          <i class='icon-cloud-upload'></i>
                        </a>
                    </c:if>

                  </div>
                </c:if>

                <c:if test="${not empty compoundSp.appStoreLogo}">
                  <img src="<c:url value="${compoundSp.appStoreLogo}"/>"/>
                </c:if>
                <p class="desc">
                  <c:out value="${serviceDescription}" />
                </p>
                <c:if test="${showConnectButton and isCard}">
                  <p class="connect-app">
                    <a href="<c:url value="/requests/linkrequest.shtml">
                            <c:param name="spEntityId" value="${compoundSp.sp.id}" />
                            <c:param name="compoundSpId" value="${compoundSp.id}" />
                          </c:url>">
                      <spring:message code="jsp.sp_detail.requestlink"/>
                    </a>
                  </p>
                </c:if>
                <c:if test="${isCard}">
                  <div class="app-meta-cta">
                    <c:if test="${not empty compoundSp.appUrl}">
                      <a href="${compoundSp.appUrl}" target="_blank" rel="tooltip" title="<spring:message code="jsp.sp_overview.gotoapp" />">
                        <i class="icon-external-link"></i>
                      </a>
                    </c:if>
                  </div>
                </c:if>
            </li>
            </c:if>
          </c:forEach>
        </ul>
      </div>
    </div>
    </section>
  </div>
</div>
<jsp:include page="foot.jsp" />