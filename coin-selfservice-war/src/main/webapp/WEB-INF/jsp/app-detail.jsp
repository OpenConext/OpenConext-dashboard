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
  <jsp:param name="wrapperAdditionalCssClass" value="has-left-right" />
</jsp:include>

<div class="column-right side-content-holder">
  <section>
    <c:if test="${not empty compoundSp.detailLogo}">
      <img src="${compoundSp.detailLogo}" alt="<c:out value=""/>" class="application-logo">
    </c:if>
    <ul class="action-list">
      <c:if test="${not empty compoundSp.appUrl}">
        <li>
          <a href="${compoundSp.appUrl}" target="_blank">
            <spring:message code="jsp.app_detail.app_url_label" arguments="${spname}"/>
          </a>
        </li>
      </c:if>
      <c:if test="${not empty compoundSp.serviceUrl}">
        <li>
          <a href="${compoundSp.serviceUrl}" target="_blank">
            <spring:message code="jsp.app_detail.service_url_label" />
          </a>
        </li>
      </c:if>
      <c:set var="supportUrl"><tags:locale-specific nlVariant="${compoundSp.supportUrlNl}" enVariant="${compoundSp.supportUrlEn}" /></c:set>
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

        <c:if test="${applyAllowed}">
        <c:choose>
          <c:when test="${not compoundSp.sp.linked}">
            <div class="service-not-connected">
              <p><strong><spring:message code="jsp.app_detail.no_technical_connection"/></strong></p>
              <a href="<c:url value="/requests/linkrequest.shtml">
                <c:param name="spEntityId" value="${compoundSp.sp.id}" />
                <c:param name="compoundSpId" value="${compoundSp.id}" />
              </c:url>"
                 title="<spring:message code="jsp.sp_detail.requestlink"/>"><spring:message code="jsp.sp_detail.requestlink"/></a>
              |
             <tags:ask-question csp="${compoundSp}" invariant="${questionAllowed}" />
            </div>
          </c:when>
          <c:when test="${compoundSp.sp.linked}">
            <div class="service-connected">
              <p><strong><spring:message code="jsp.app_detail.technical_connection"/></strong></p>
              <a href="<c:url value="/requests/unlinkrequest.shtml">
                <c:param name="spEntityId" value="${compoundSp.sp.id}" />
                <c:param name="compoundSpId" value="${compoundSp.id}" />
              </c:url>"
                 title="<spring:message code="jsp.sp_detail.requestunlink"/>"><spring:message
                  code="jsp.sp_detail.requestunlink"/></a>
              |
              <tags:ask-question csp="${compoundSp}" invariant="${questionAllowed}" />
            </div>
          </c:when>
        </c:choose>
        </c:if>


        
        <c:if test="${lmngActiveModus}">    
          <c:choose>
            <c:when test="${compoundSp.articleLicenseAvailable}">
              <div class="license-available">
                <c:choose>
                  <c:when test="${compoundSp.article.licence.groupLicense}">
                    <p><strong><spring:message code="jsp.app_detail.group_license_available"/></strong></p>
                    <p><spring:message code="jsp.app_detail.group_license_available_detail"/></p>
                  </c:when>
                  <c:otherwise>
                    <p><strong><spring:message code="jsp.app_detail.license_available"/></strong></p>
                  </c:otherwise>
                </c:choose>          
                <c:set var="endDate"><fmt:formatDate pattern="dd-MM-yyyy" value="${compoundSp.article.endDate}"/></c:set>
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
                <br/>
              </div>
            </c:otherwise>
          </c:choose>
       </c:if>   

        <tags:ask-question csp="${compoundSp}" invariant="${questionAllowed and !applyAllowed}" />
      </div>

    <div class="with-read-more" data-read-more-text="<spring:message code="jsp.app_detail.read_more"/>" data-read-less-text="<spring:message code="jsp.app_detail.read_less"/>">
      <%--span rel="tooltip" data-original-title="<spring:message code="jsp.app_detail.institution_description"/>"--%>
      <tags:html-format>
        <jsp:attribute name="input">
            <tags:locale-specific nlVariant="${compoundSp.institutionDescriptionNl}" enVariant="${compoundSp.institutionDescriptionEn}" />
        </jsp:attribute>
      </tags:html-format>
      <%--/span--%>
    </div>

    <div class="with-read-more" data-read-more-text="<spring:message code="jsp.app_detail.read_more"/>" data-read-less-text="<spring:message code="jsp.app_detail.read_less"/>">
      <tags:html-format>
        <jsp:attribute name="input">
          <tags:locale-specific nlVariant="${compoundSp.enduserDescriptionNl}" enVariant="${compoundSp.enduserDescriptionEn}" />
        </jsp:attribute>
      </tags:html-format>
    </div>
 
    <c:set var="sp" value="${compoundSp.sp}" scope="request" />
    <div class="arp">
          <jsp:include page="requests/arp.jsp" />
    </div>

    <hr>

    <%--@elvariable id="oAuthTokens" type="java.util.List<nl.surfnet.coin.selfservice.domain.OAuthTokenInfo>"--%>
    <c:if test="${fn:length(oAuthTokens) gt 0}">
      <spring:url value="/user/service/revokekeys.shtml" htmlEscape="true" var="revokeUrl">
        <spring:param name="spEntityId" value="${compoundSp.serviceProviderEntityId}"/>
      </spring:url>
      <spring:message code="jsp.service_detail.oauth_present"/> (<a href="${revokeUrl}"><spring:message
          code="jsp.service_detail.oauth_revoke"/></a>)
    </c:if>

    <c:if test="${revoked eq 'true' and fn:length(oAuthTokens) eq 0}">
      <div class="alert alert-success">
        <a class="close" data-dismiss="alert">&times;</a>
        <spring:message code="jsp.service_detail.oauth_revoke.success"/>
      </div>
    </c:if>
    
    <c:if test="${not empty compoundSp.screenShotsImages}">
      <h2><spring:message code="jsp.app_detail.screenshots_of" arguments="${spname}" htmlEscape="false"/></h2>

      <div class="screenshots-holder gallery-holder">
        <ul class="gallery">
          <c:forEach items="${compoundSp.screenShotsImages}" var="screenshot"><li>
            <a href="<spring:url value="${screenshot.fileUrl}" />">
              <img src="<spring:url value="${screenshot.fileUrl}" />" alt="Screenshot <c:out value="${spname}"/>">
            </a>
          </li></c:forEach>
        </ul>
      </div>
    </c:if>
  </section>

</div><!-- .column-center.content-holder -->


<jsp:include page="foot.jsp" />