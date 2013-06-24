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

<c:set var="spname"><c:out default="${service.id}" value="${service.name}" /></c:set>
<spring:message var="title" code="jsp.home.title" />
<jsp:include page="head.jsp">
  <jsp:param name="title" value="${title}" />
</jsp:include>
<div class="wrapper has-right">
<div class="column-right side-content-holder">
  <section>
    <c:if test="${not empty service.detailLogoUrl}">
      <img src="${service.detailLogoUrl}" alt="<c:out value=""/>" class="application-logo">
    </c:if>
    <ul class="launch-icons">
      <c:if test="${not empty service.appUrl}">
        <li>
          <a class="btn btn-primary-alt start-app" href="${service.appUrl}" target="_blank">
            <spring:message code="jsp.app_detail.app_url_label"/>
            <i class="icon-play"></i>
          </a>
        </li>
      </c:if>
      <c:if test="${not empty service.crmArticle.appleAppStoreUrl}">
	      <li>
          <a href="${service.crmArticle.appleAppStoreUrl}">
	          <img src="<c:url value="/images/icon-app-store.png"/>" alt="iTunes App Store">
	        </a>
	      </li>
      </c:if>
      <c:if test="${not empty service.crmArticle.androidPlayStoreUrl}">
	      <li>
	        <a href="${service.crmArticle.androidPlayStoreUrl}">
	          <img src="<c:url value="/images/icon-google-play.png"/>" alt="Google Play Store">
	        </a>
	      </li>
	    </c:if>
      <li>
        <spring:url var="recommendAppLink" value="/app-recommend.shtml">
          <spring:param name="serviceId" value="${service.id}" />
        </spring:url>
        <a id="recommend-app" class="btn btn-primary recommend-app" href="${recommendAppLink}">
            <spring:message code="jsp.app_detail.recommend_app"/>
            <i class="icon-comments-alt"></i>
          </a>
        </li>
    </ul>
    <ul class="action-list">
      <c:if test="${not empty service.serviceUrl}">
        <li>
          <a href="${service.serviceUrl}" target="_blank">
            <spring:message code="jsp.app_detail.service_url_label" arguments="${spname}"/>
          </a>
        </li>
      </c:if>
      <c:if test="${not empty service.supportUrl}">
        <li>
          <a href="${service.supportUrl}" target="_blank">
            <spring:message code="jsp.app_detail.support_url_label" arguments="${spname}"/>
          </a>
        </li>
      </c:if>
      <c:if test="${not empty service.eulaUrl}">
        <li>
          <a href="${service.eulaUrl}" target="_blank">
            <spring:message code="jsp.app_detail.terms_conditions" />
          </a>
        </li>
      </c:if>

      <c:if test="${isDashBoard}">
        <spring:url var="statsLink" value="/stats/stats.shtml" htmlEscape="true">
          <spring:param name="spEntityId" value="${service.spEntityId}" />
        </spring:url>
        <c:set var="tooltipStats"><spring:message code="jsp.sp_detail.statslink"/></c:set>
        <li>
          <a class="service-stats" rel="tooltip" data-type="info" data-original-title="${tooltipStats}"
              href="${statsLink}"></a>
        </li>
      </c:if>
      </ul>
      <c:if test="${not empty service.supportMail}">
        <ul class="action-list email-addresses">
        <li>
          <spring:message code="jsp.app_detail.support_email" />
          <a href="mailto:<c:out value="${service.supportMail}"/>"><c:out value="${service.supportMail}"/></a>
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
          <c:when test="${not service.connected}">
            <div class="service-not-connected">
              <p>
                <strong><spring:message code="jsp.app_detail.no_technical_connection"/></strong>
              </p>
              <ul>
              <c:if test="${applyAllowed}">
                <li>
                <c:choose>
                <c:when test="${service.hasCrmLink && empty service.license}">
                  <spring:message code="jsp.sp_detail.requestlink"/>
                </c:when>
                <c:otherwise>
		              <a href="<c:url value="/requests/linkrequest.shtml">
		                <c:param name="serviceId" value="${service.id}" />
		              </c:url>"title="<spring:message code="jsp.sp_detail.requestlink"/>"><spring:message code="jsp.sp_detail.requestlink"/></a>
                </c:otherwise>
                </c:choose>
                </li>
	            </c:if>
              <c:if test="${questionAllowed}"><li><tags:ask-question service="${service}" invariant="${questionAllowed}" /></li></c:if>
              <c:if test="${applyAllowed}"><li><a href="#" rel="tooltip" data-placement="top" title="Entity ID: ${service.spEntityId}"><i class="icon-info-sign"></i></a></li></c:if>

              </ul>
            </div>
          </c:when>
          <c:when test="${service.connected}">
            <div class="service-connected">
              <p>
                <strong><spring:message code="jsp.app_detail.technical_connection"/></strong>
              </p>
              <ul>
                <c:if test="${applyAllowed}">
                  <li>    <a href="<c:url value="/requests/unlinkrequest.shtml">
                            <c:param name="serviceId" value="${service.id}" />
                              </c:url>" title="<spring:message code="jsp.sp_detail.requestunlink"/>">
                            <spring:message code="jsp.sp_detail.requestunlink"/>
                          </a>
                  </li>
                </c:if>
              <c:if test="${questionAllowed}"><li><tags:ask-question service="${service}" invariant="${questionAllowed}" /></li></c:if>
              <c:if test="${applyAllowed}"><li><a href="#" rel="tooltip" data-placement="top" title="Entity ID: ${service.spEntityId}"><i class="icon-info-sign"></i></a></li></c:if>
              </ul>
            </div>
          </c:when>
        </c:choose>
        </c:if>

          <c:choose>
            <c:when test="${service.hasCrmLink and !empty service.license}">
              <div class="license-available">
                <c:choose>
                  <c:when test="${service.license.groupLicense}">
                    <p><strong><spring:message code="jsp.app_detail.group_license_available"/></strong></p>
                    <p><spring:message code="jsp.app_detail.group_license_available_detail"/></p>
                  </c:when>
                  <c:otherwise>
                    <p><strong><spring:message code="jsp.app_detail.license_available"/></strong></p>
                  </c:otherwise>
                </c:choose>
                <c:set var="endDate"><fmt:formatDate pattern="dd-MM-yyyy" value="${service.license.endDate}"/></c:set>
                <p><spring:message code="jsp.app_detail.license_validity" arguments="${endDate}"/></p>

                <c:if test="${deepLinkToSurfMarketAllowed}">
                  <c:set var="url" value="${lmngDeepLinkUrl}${service.crmArticle.guid}" />
                  <p><spring:message code="jsp.app_detail.license_deeplink_text" arguments="${url}"/></p>
                </c:if>
              </div>
            </c:when>
            <c:when test="${service.hasCrmLink}">
              <div class="license-not-available">
                <p><strong><spring:message code="jsp.app_detail.license_not_available"/></strong></p>
                <c:if test="${deepLinkToSurfMarketAllowed}">
                  <c:set var="url" value="${lmngDeepLinkUrl}${service.crmArticle.guid}" />
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

        <tags:ask-question service="${service}" invariant="${questionAllowed and !applyAllowed}" />
      </div>

    <div class="with-read-more" data-read-more-text="<spring:message code="jsp.app_detail.read_more"/>"
              data-read-less-text="<spring:message code="jsp.app_detail.read_less"/>">
      <tags:html-format>
        <jsp:attribute name="input"><c:out value="${service.description}" /></jsp:attribute>
      </tags:html-format>
    </div>

    <div class="with-read-more" data-read-more-text="<spring:message code="jsp.app_detail.read_more"/>"
              data-read-less-text="<spring:message code="jsp.app_detail.read_less"/>">
      <tags:html-format>
        <jsp:attribute name="input"><c:out value="${service.institutionDescription}" /></jsp:attribute>
      </tags:html-format>
    </div>

    <div class="with-read-more" data-read-more-text="<spring:message code="jsp.app_detail.read_more"/>" data-read-less-text="<spring:message code="jsp.app_detail.read_less"/>">
      <tags:html-format>
        <jsp:attribute name="input"><c:out value="${service.enduserDescription}" /></jsp:attribute>
      </tags:html-format>
    </div>

    <div class="arp">
      <jsp:include page="requests/arp.jsp" />
    </div>

    <%--@elvariable id="oAuthTokens" type="java.util.List<nl.surfnet.coin.selfservice.domain.OAuthTokenInfo>"--%>
    <c:if test="${fn:length(oAuthTokens) gt 0}">
      <div>
        <p>
      <spring:url value="revokekeys.shtml" htmlEscape="true" var="revokeUrl">
        <spring:param name="id" value="${service.id}"/>
        <spring:param name="spEntityId" value="${service.spEntityId}"/>
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

    <c:if test="${not empty service.screenshotUrls}">
      <h2><spring:message code="jsp.app_detail.screenshots_of" arguments="${spname}" htmlEscape="false"/></h2>

      <div class="screenshots-holder gallery-holder">
        <ul class="gallery">
          <c:forEach items="${service.screenshotUrls}" var="screenshot">
            <li>
              <a href="<spring:url value="${screenshot}" />">
                <img src="<spring:url value="${screenshot}" />" alt="Screenshot <c:out value="${spname}"/>">
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