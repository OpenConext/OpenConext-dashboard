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

<c:set var="spname"><tags:providername provider="${compoundSp.sp}" /></c:set>
<spring:message var="title" code="jsp.home.title" />
<jsp:include page="head.jsp">
  <jsp:param name="title" value="${title}" />
</jsp:include>
<div class="wrapper has-right">
  <div class="column-right side-content-holder">
    <section>
      <c:if test="${not empty compoundSp.detailLogo}">
        <img src="${compoundSp.detailLogo}" alt="<c:out value=""/>" class="application-logo">
      </c:if>
      <ul class="launch-icons">
        <c:if test="${not empty compoundSp.appUrl}">
          <li>
            <a class="btn btn-primary-alt start-app" href="${compoundSp.appUrl}" target="_blank">
              <spring:message code="jsp.app_detail.app_url_label"/>
              <i class="icon-play"></i>
            </a>
          </li>
        </c:if>
        <c:if test="${not empty compoundSp.article.appleAppStoreMedium}">
          <li>
            <a href="${compoundSp.article.appleAppStoreMedium.url}">
              <img src="<c:url value="/images/icon-app-store.png"/>" alt="iTunes App Store">
            </a>
          </li>
        </c:if>
        <c:if test="${not empty compoundSp.article.androidPlayStoreMedium}">
          <li>
            <a href="${compoundSp.article.androidPlayStoreMedium.url}">
              <img src="<c:url value="/images/icon-google-play.png"/>" alt="Google Play Store">
            </a>
          </li>
        </c:if>
        <li>
          <spring:url var="recommendAppLink" value="/app-recommend.shtml">
            <spring:param name="compoundSpId" value="${compoundSp.id}" />
          </spring:url>
          <a id="recommend-app" class="btn btn-primary recommend-app" href="${recommendAppLink}">
              <spring:message code="jsp.app_detail.recommend_app"/>
              <i class="icon-comments-alt"></i>
            </a>
          </li>
      </ul>
      <ul class="action-list">
        <c:if test="${not empty compoundSp.serviceUrl}">
          <li>
            <a href="${compoundSp.serviceUrl}" target="_blank">
              <spring:message code="jsp.app_detail.service_url_label" arguments="${spname}"/>
            </a>
          </li>
        </c:if>
        <c:set var="supportUrl"><tags:locale-specific escapeXml="true" nlVariant="${compoundSp.supportUrlNl}" enVariant="${compoundSp.supportUrlEn}" /></c:set>
        <c:if test="${not empty supportUrl}">
          <li>
            <a href="${supportUrl}" target="_blank">
              <spring:message code="jsp.app_detail.support_url_label" arguments="${spname}"/>
            </a>
          </li>
        </c:if>
        <c:if test="${not empty compoundSp.eulaUrl}">
          <li>
            <a href="${compoundSp.eulaUrl}" target="_blank">
              <spring:message code="jsp.app_detail.terms_conditions" />
            </a>
          </li>
        </c:if>
        <c:if test="${isAdminUser && ebLinkActive}">
          <spring:url var="statsLink" value="/stats/stats.shtml" htmlEscape="true">
            <spring:param name="spEntityId" value="${compoundSp.sp.id}" />
          </spring:url>
          <c:set var="tooltipStats"><spring:message code="jsp.sp_detail.statslink"/></c:set>
          <li>
            <a class="service-stats" rel="tooltip" data-type="info" data-original-title="${tooltipStats}"
                href="${statsLink}"></a>
          </li>
        </c:if>
        </ul>
        <c:if test="${not empty compoundSp.supportMail}">
          <ul class="action-list email-addresses">
          <li>
            <spring:message code="jsp.app_detail.support_email" />
            <a href="mailto:<c:out value="${compoundSp.supportMail}"/>"><c:out value="${compoundSp.supportMail}"/></a>
          </li>
        </ul>
        </c:if>
    </section>
  </div>

  <div class="column-center content-holder">
    <section>
        <h1>${spname}</h1>
        <div class="license-connect">
          <c:if test="${connectionVisible}">
          <c:choose>
            <c:when test="${not compoundSp.sp.linked}">
              <div class="service-not-connected">
                <p>
                  <strong><spring:message code="jsp.app_detail.no_technical_connection"/></strong>
                </p>
                <ul>
                <c:if test="${applyAllowed}">
                  <li>
                  <a href="<c:url value="/requests/linkrequest.shtml">
                    <c:param name="spEntityId" value="${compoundSp.sp.id}" />
                    <c:param name="compoundSpId" value="${compoundSp.id}" />
                  </c:url>"
                     title="<spring:message code="jsp.sp_detail.requestlink"/>"><spring:message code="jsp.sp_detail.requestlink"/></a>
                  </li>
                </c:if>
                <c:if test="${questionAllowed}"><li><tags:ask-question csp="${compoundSp}" invariant="${questionAllowed}" /></li></c:if>
                <c:if test="${applyAllowed or isGod}"><li><a href="#" rel="tooltip" data-placement="top" title="Entity ID: ${compoundSp.sp.id}"><i class="icon-info-sign"></i></a></li></c:if>

                </ul>
              </div>
            </c:when>
            <c:when test="${compoundSp.sp.linked}">
              <div class="service-connected">
                <p>
                  <strong><spring:message code="jsp.app_detail.technical_connection"/></strong>
                </p>
                <ul>
                  <c:if test="${applyAllowed}">
                    <li>    <a href="<c:url value="/requests/unlinkrequest.shtml">
                              <c:param name="spEntityId" value="${compoundSp.sp.id}" />
                              <c:param name="compoundSpId" value="${compoundSp.id}" />
                                </c:url>" title="<spring:message code="jsp.sp_detail.requestunlink"/>">
                              <spring:message code="jsp.sp_detail.requestunlink"/>
                            </a>
                    </li>
                  </c:if>
                <c:if test="${questionAllowed}"><li><tags:ask-question csp="${compoundSp}" invariant="${questionAllowed}" /></li></c:if>
                <c:if test="${applyAllowed or isGod}"><li><a href="#" rel="tooltip" data-placement="top" title="Entity ID: ${compoundSp.sp.id}"><i class="icon-info-sign"></i></a></li></c:if>
                </ul>
              </div>
            </c:when>
          </c:choose>
          </c:if>
          <c:if test="${lmngActive}">
            <c:choose>
              <c:when test="${compoundSp.articleLicenseAvailable}">
                <div class="license-available">
                  <c:choose>
                    <c:when test="${compoundSp.license.groupLicense}">
                      <p><strong><spring:message code="jsp.app_detail.group_license_available"/></strong></p>
                      <p><spring:message code="jsp.app_detail.group_license_available_detail"/></p>
                    </c:when>
                    <c:otherwise>
                      <p><strong><spring:message code="jsp.app_detail.license_available"/></strong></p>
                    </c:otherwise>
                  </c:choose>
                  <c:set var="endDate"><fmt:formatDate pattern="dd-MM-yyyy" value="${compoundSp.license.endDate}"/></c:set>
                  <p><spring:message code="jsp.app_detail.license_validity" arguments="${endDate}"/></p>

                  <c:if test="${deepLinkToSurfMarketAllowed}">
                    <c:set var="url" value="${lmngDeepLinkUrl}${compoundSp.lmngId}" />
                    <p><spring:message code="jsp.app_detail.license_deeplink_text" arguments="${url}"/></p>
                  </c:if>
                </div>
              </c:when>
              <c:when test="${compoundSp.articleAvailable}">
                <div class="license-not-available">
                  <p><strong><spring:message code="jsp.app_detail.license_not_available"/></strong></p>
                  <c:if test="${deepLinkToSurfMarketAllowed}">
                    <c:set var="url" value="${lmngDeepLinkUrl}${compoundSp.lmngId}" />
                    <p><spring:message code="jsp.app_detail.license_deeplink_text" arguments="${url}"/></p>
                  </c:if>
                </div>
              </c:when>
              <c:otherwise>
                <div class="license-not-needed">
                  <p><strong><spring:message code="jsp.app_detail.license_not_needed"/></strong></p>
                  <p><spring:message code="jsp.app_detail.license_not_needed_detail"/></p>
                </div>
              </c:otherwise>
            </c:choose>
         </c:if>

          <tags:ask-question csp="${compoundSp}" invariant="${questionAllowed and !applyAllowed}" />
        </div>

      <div class="with-read-more" data-read-more-text="<spring:message code="jsp.app_detail.read_more"/>"
                data-read-less-text="<spring:message code="jsp.app_detail.read_less"/>">
        <tags:html-format>
          <jsp:attribute name="input">
              <tags:locale-specific escapeXml="false" nlVariant="${compoundSp.institutionDescriptionNl}" enVariant="${compoundSp.institutionDescriptionEn}" />
          </jsp:attribute>
        </tags:html-format>
      </div>

      <div class="with-read-more" data-read-more-text="<spring:message code="jsp.app_detail.read_more"/>" data-read-less-text="<spring:message code="jsp.app_detail.read_less"/>">
        <tags:html-format>
          <jsp:attribute name="input">
            <tags:locale-specific escapeXml="false" nlVariant="${compoundSp.enduserDescriptionNl}" enVariant="${compoundSp.enduserDescriptionEn}" />
          </jsp:attribute>
        </tags:html-format>
      </div>

      <c:set var="sp" value="${compoundSp.sp}" scope="request" />
      <div class="arp">
        <jsp:include page="requests/arp.jsp" />
      </div>

      <%--@elvariable id="oAuthTokens" type="java.util.List<nl.surfnet.coin.selfservice.domain.OAuthTokenInfo>"--%>
      <c:if test="${fn:length(oAuthTokens) gt 0}">
        <div>
          <p>
        <spring:url value="revokekeys.shtml" htmlEscape="true" var="revokeUrl">
          <spring:param name="compoundSpId" value="${compoundSp.id}"/>
          <spring:param name="spEntityId" value="${compoundSp.serviceProviderEntityId}"/>
        </spring:url>
        <spring:message code="jsp.service_detail.oauth_present"/> (<a href="${revokeUrl}"><spring:message
            code="jsp.service_detail.oauth_revoke"/></a>)
          </p>
        </div>
      </c:if>

      <c:if test="${revoked eq 'true' and fn:length(oAuthTokens) eq 0}">
        <div class="alert alert-success">
          <a class="close" data-dismiss="alert">&times;</a>
          <spring:message code="jsp.service_detail.oauth_revoke.success"/>
        </div>
      </c:if>

      <hr>

      <c:if test="${not empty compoundSp.screenShotsImages}">
        <h2><spring:message code="jsp.app_detail.screenshots_of" arguments="${spname}" htmlEscape="false"/></h2>

        <div class="screenshots-holder gallery-holder">
          <ul class="gallery">
            <c:forEach items="${compoundSp.screenShotsImages}" var="screenshot">
              <li>
                <a href="<spring:url value="${screenshot.fileUrl}" />">
                  <img src="<spring:url value="${screenshot.fileUrl}" />" alt="Screenshot <c:out value="${spname}"/>">
                </a>
              </li>
            </c:forEach>
          </ul>
        </div>
      </c:if>
    </section>

  </div><!-- .column-center.content-holder -->
</div>

<jsp:include page="foot.jsp" />