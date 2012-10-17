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
      <c:if test="${not empty compoundSp.serviceUrl}">
        <li>
          <a href="${compoundSp.serviceUrl}">
            <spring:message code="jsp.app_detail.service_url_label" arguments="${spname}" />
          </a>
        </li>
      </c:if>
      <c:if test="${not empty compoundSp.supportUrl}">
        <li>
          <a href="${compoundSp.supportUrl}">
            <spring:message code="jsp.app_detail.support_url_label" arguments="${spname}" />
          </a>
        </li>
      </c:if>
      <c:if test="${not empty compoundSp.eulaUrl}">
        <li><a href="${compoundSp.eulaUrl}">Terms & Conditions</a></li>
      </c:if>
    </ul>
    <p>Wordt gebruikt door 21 andere instellingen en door 2401 personen.</p>
    <p>Is Service Provider van Surfnet sinds 12-02-2012.</p>
  </section>
</div>

<div class="column-center content-holder">
  <section>

    <h1><c:out value="${spname}"/></h1>
    


    <div class="with-read-more" data-read-more-text="Meer" data-read-less-text="Minder">
      <tags:html-format>
        <jsp:attribute name="input">
          <tags:locale-specific nlVariant="${compoundSp.descriptionNl}" enVariant="${compoundSp.descriptionEn}" />
        </jsp:attribute>
      </tags:html-format>
    </div>


    <div>
      <c:choose>
        <c:when test="${not compoundSp.sp.linked}">
          <a class="btn btn-primary btn-primary-alt" href="<c:url value="/requests/linkrequest.shtml">
            <c:param name="spEntityId" value="${compoundSp.sp.id}" />
          </c:url>"
             title="<spring:message code="jsp.sp_detail.requestlink"/>"><spring:message code="jsp.sp_detail.requestlink"/>
          </a>
        </c:when>
        <c:when test="${compoundSp.sp.linked}">
          <a class="btn btn-primary btn-primary-alt" href="<c:url value="/requests/unlinkrequest.shtml">
            <c:param name="spEntityId" value="${compoundSp.sp.id}" />
          </c:url>"
             title="<spring:message code="jsp.sp_detail.requestunlink"/>"><spring:message
              code="jsp.sp_detail.requestunlink"/>
          </a>
        </c:when>
      </c:choose>
      <a class="btn" href="<spring:url value="/requests/question.shtml">
            <spring:param name="spEntityId" value="${compoundSp.sp.id}" />
          </spring:url>"
         title="<spring:message code="jsp.sp_detail.askquestion"/>"><spring:message code="jsp.sp_detail.askquestion"/>
      </a>
    </div>

    <hr>

    <c:if test="${not empty compoundSp.screenShotsImages}">
      <h2>Screenshots van <c:out value="${spname}"/></h2>

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