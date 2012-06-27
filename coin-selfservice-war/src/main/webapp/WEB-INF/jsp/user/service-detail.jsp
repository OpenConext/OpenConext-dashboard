<%--@elvariable id="mayHaveGivenConsent" type="java.lang.Boolean"--%>
<%@ include file="../include.jsp" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
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

<%--@elvariable id="sp" type="nl.surfnet.coin.selfservice.domain.ServiceProvider"--%>

<c:set var="spname"><tags:providername provider="${sp}"/></c:set>

<jsp:include page="../header.jsp">
  <jsp:param name="title" value="${spname}"/>
</jsp:include>

<section>

  <h2><c:out value="${spname}"/></h2>

  <div class="row">
    <c:if test="${not empty sp.logoUrl}">
      <div class="span2">
        <div class="content">
          <p>
            <img alt="" style="float:left" src="<c:out value="${sp.logoUrl}"/>"/>
          </p>
        </div>
      </div>
    </c:if>
    <%-- bit ugly: if this span does not exist, the right column is pushed to the left --%>
    <div class="span6">
      <div class="content">
        <c:if test="${not empty sp.descriptions[locale.language]}">
          <p>
            <c:out value="${sp.descriptions[locale.language]}"/>
          </p>
        </c:if>
        <h3><spring:message code="jsp.service_detail.arp_header"/></h3>
        <c:if test="${mayHaveGivenConsent ne true}">
          <p><spring:message code="jsp.service_detail.arp_firsttime"/></p>
        </c:if>

        <sec:authentication property="principal.idp" scope="request" htmlEscape="true" var="idp"/>
        <sec:authentication property="principal.attributeMap" scope="request" var="attributeMap"/>
        <selfservice:arpFilter var="arps" idpId="${idp}" arpList="${sp.arps}"/>
        <ul>
          <c:if test="${fn:length(arps) eq 0}">
            <c:forEach items="${attributeMap}" var="attribute">
              <li><tags:arp-attribute-info attributeKey="${attribute.key}"/></li>
            </c:forEach>
          </c:if>
          <c:forEach items="${arps}" var="arp">
            <c:if test="${empty arp.fedAttributes and empty arp.conextAttributes}">
              <c:forEach items="${attributeMap}" var="attribute">
                <li><tags:arp-attribute-info attributeKey="${attribute.key}"/></li>
              </c:forEach>
            </c:if>
            <c:forEach items="${arp.fedAttributes}" var="att">
              <li><tags:arp-attribute-info attributeKey="${att}"/></li>
            </c:forEach>
            <c:forEach items="${arp.conextAttributes}" var="att">
              <li><tags:arp-attribute-info attributeKey="${att.key}"/></li>
            </c:forEach>
          </c:forEach>

          <%--@elvariable id="oAuthTokens" type="java.util.List<nl.surfnet.coin.selfservice.domain.OAuthTokenInfo>"--%>
          <c:if test="${fn:length(oAuthTokens) gt 0}">
            <spring:url value="/user/service/revokekeys.shtml" htmlEscape="true" var="revokeUrl">
              <spring:param name="spEntityId" value="${sp.id}"/>
            </spring:url>
            <li><spring:message code="jsp.service_detail.oauth_present"/> (<a href="${revokeUrl}"><spring:message
                code="jsp.service_detail.oauth_revoke"/></a>)
            </li>
          </c:if>
        </ul>

        <c:if test="${revoked eq 'true' and fn:length(oAuthTokens) eq 0}">
          <div class="alert alert-success">
            <a class="close" data-dismiss="alert">&times;</a>
            <spring:message code="jsp.service_detail.oauth_revoke.success"/>
          </div>
        </c:if>

        <c:if test="${not empty sp.urls[locale.language]}">
          <p>
            <a href="<c:out value="${sp.urls[locale.language]}"/>" class="btn btn-primary btn-small cw75 mb10"
               target="_blank">
              <i class="icon-external-link"></i> <spring:message code="jsp.sp_detail.serviceurl"/>
            </a>
          </p>
        </c:if>
      </div>
    </div>
    <div class="span4">
      <div class="content">
        <c:if test="${fn:length(sp.contactPersons) gt 0}">
          <c:forEach items="${sp.contactPersons}" var="cp">
            <c:if test="${cp.contactPersonType eq 'help'}">
              <%-- Only show help for end users --%>
              <h3><spring:message code="jsp.sp_detail.contact"/></h3>
              <ul class="unstyled">
                <li><c:out value="${cp.name}"/>
                  <c:if test="${not empty cp.contactPersonType}">
                    <em>(<c:out value="${cp.contactPersonType}"/>)</em>
                  </c:if>
                </li>
                <c:if test="${not empty cp.emailAddress}">
                  <li><a href="mailto:<c:out value="${cp.emailAddress}"/>"><c:out value="${cp.emailAddress}"/></a> <i
                      class="icon-envelope"></i></li>
                </c:if>
                <c:if test="${not empty cp.telephoneNumber}">
                  <li><c:out value="${cp.telephoneNumber}"/></li>
                </c:if>
              </ul>
            </c:if>
          </c:forEach>
        </c:if>
        <h3><spring:message code="jsp.sp_detail.moreinfo"/></h3>
        <ul class="unstyled">
          <c:if test="${not empty sp.eulaURL}">
            <li>
              <a href="<c:out value="${sp.eulaURL}"/>" target="_blank"><spring:message code="jsp.sp_detail.eula"/></a>
              <i class="icon-external-link"></i>
            </li>
          </c:if>
        </ul>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="span12">
      <div class="content">
        <div id="chart"></div>
      </div>
    </div>
  </div>
</section>


<jsp:include page="../footer.jsp"/>
