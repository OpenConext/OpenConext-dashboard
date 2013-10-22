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
        <li class="facet-name"><spring:message code="jsp.app_overview.recently_login_info"/></li>
          <ul class="facets-values">
            <li><a class="facet-value inactive" data-facet-search-term="recently-logged-in" href="#"><spring:message code="jsp.app_overview.recent_login"/> <span>(${recentLoginCount})</span></a></li>
            <li><a class="facet-value inactive" data-facet-search-term="not-recently-logged-in" href="#"><spring:message code="jsp.app_overview.no_recent_login"/> <span>(${noRecentLoginCount})</span></a></li>
          </ul>
        <c:if test="${facetsUsed}">
          <c:forEach items="${facets}" var="facet">
            <c:if test="${facet.usedFacetValues}">
              <li class="facet-name">${facet.name}</li>
              <ul class="facets-values">
                <c:forEach items="${facet.values}" var="facetValue">
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
    <div class="view-option">
      <c:set var="isCard" value="${view eq 'card'}" />
      <spring:url value="app-overview.shtml" var="cardUrl" htmlEscape="true">
        <spring:param name="view" value="card" />
      </spring:url>
      <a href="${isCard ? '#' : cardUrl}" class="${isCard ? 'disabled' : ''} card-view"><i class="icon-th-large"></i></a>
      <spring:url value="app-overview.shtml" var="listUrl" htmlEscape="true">
        <spring:param name="view" value="list" />
      </spring:url>
      <a href="${isCard ? listUrl : '#'}" class="${isCard ? '' : 'disabled'}"><i class="icon-th-list"></i></a>
    </div>
    <section>
    <div style="padding-top: 10px;">
    <div>
      <ul class="${view}-view app-grid ${isDashBoard == false ? 'crmAvailable' : ''}">
      <c:forEach items="${services}" var="service">
              <c:set var="serviceDescription" value="${service.description}" />
              <c:set var="showConnectButton" value="${applyAllowed and (not service.connected)}" />
              <li class="${view}-view" data-id="${service.id}"
                          data-facet-values="${service.connected ? "connected" : "not-connected"} ${empty service.license ? "not-licensed" : "licensed"} ${empty service.lastLoginDate ? "not-recently-logged-in" : "recently-logged-in"} ${service.searchFacetValues}">
                <spring:url value="app-detail.shtml" var="detailUrl" htmlEscape="true">
                  <spring:param name="serviceId" value="${service.id}" />
                </spring:url>

                <c:set var="spTitle" >
                  <c:out default="${service.id}" value="${service.name}" />
                </c:set>
                <c:if test="${not empty service.logoUrl}">
                  <img src="<c:url value="${service.logoUrl}"/>" data-toggle="tooltip" data-placement="top" title="<c:out value="${serviceDescription}" />" />
                </c:if>
                <div class="service-info-${view}">
                <h2>
                  <a id="detail-${service.spEntityId}" href="${detailUrl}" data-toggle="tooltip" data-placement="top" title="<c:out value="${serviceDescription}" />">
                    <tags:truncatedSpName
                        spName="${spTitle}"
                        hasServiceDescription="${not empty serviceDescription}"
                        hasConnectButton="${showConnectButton}" />
                  </a>
                </h2>
                  <c:if test="${!isCard}">
                    <div class="app-meta-cta">
                      <c:if test="${not empty service.appUrl}">
                        <a href="${service.appUrl}" target="_blank" rel="tooltip" title="<spring:message code="jsp.sp_overview.gotoapp" />">
                          <i class="icon-external-link"></i>
                        </a>
                      </c:if>
                        <c:if test="${showConnectButton and !isCard}">
                          <a href="<c:url value="/requests/linkrequest.shtml">
                                  <c:param name="serviceId" value="${service.id}" />
                                </c:url>" rel="tooltip" title="<spring:message code="jsp.sp_detail.requestlink"/>">
                            <i class='icon-cloud-upload'></i>
                          </a>
                      </c:if>
                    </div>
                  </c:if>
                </div>
                <c:if test="${isCard}">
                  <div class="app-meta-cta">
                    <c:if test="${not empty service.appUrl}">
                      <a href="${service.appUrl}" class="external-link" target="_blank" rel="tooltip" title="<spring:message code="jsp.sp_overview.gotoapp" />">
                        <i class="icon-external-link"></i>
                      </a>
                    </c:if>
                    <c:if test="${not empty service.lastLoginDate and isCard}">
                       <p class="recent-login" data-title="${spTitle}" data-placement="bottom" data-content="<spring:message htmlEscape="true" code="jsp.app_overview.recent_login_popover" />">
                            <i class="icon-user"></i>
                            <fmt:formatDate value="${service.lastLoginDate}" pattern="dd-MM-yyyy HH:mm" />
                       </p>
                   </c:if>
                    <c:if test="${showConnectButton and isCard}">
                       <p class="connect-app">
                          <a href="<c:url value="/requests/linkrequest.shtml"><c:param name="serviceId" value="${service.id}" /></c:url>">
                            <spring:message code="jsp.sp_detail.requestlink"/>
                          </a>
                       </p>
                    </c:if>
                  </div>
                </c:if>
            </li>
          </c:forEach>
        </ul>
      </div>
    </div>
    </section>
  </div>
</div>
<jsp:include page="foot.jsp" />