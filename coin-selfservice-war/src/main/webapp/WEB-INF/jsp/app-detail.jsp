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

<c:set var="spname"><tags:providername provider="${compoundSp}" /></c:set>
<spring:message var="title" code="jsp.home.title" />
<jsp:include page="head.jsp">
  <jsp:param name="title" value="${title}" />
</jsp:include>

<div class="column-right side-content-holder">
  <section>
    <c:if test="${not empty compoundSp.detailLogo}">
      <img src="${compoundSp.detailLogo}" alt="<c:out value=""/>" class="application-logo">
    </c:if>
    <ul class="action-list">
      <li><a href="index.html">Website <c:out value="${spname}"/></a></li>
      <li><a href="index.html">Contact <c:out value="${spname}"/></a></li>
      <c:if test="${not empty compoundSp.eulaURL}">
        <li><a href="${compoundSp.eulaURL}">Terms & Conditions</a></li>
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
      <p>
      Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi eu orci sit amet est mattis varius. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nunc tincidunt tempus accumsan. Cras tortor orci, faucibus ultricies euismod et, fringilla vitae purus. Nunc eleifend dictum metus, at laoreet libero congue nec. Aliquam at velit risus, in dictum libero.
      </p>
      <p>
      Nunc eros libero, aliquam in eleifend at, dapibus vel libero. Proin imperdiet blandit metus at suscipit. Duis volutpat eros eu ante rhoncus lacinia dictum eros mollis. Vestibulum id neque dolor, id laoreet odio. Sed vel tellus nec nunc sollicitudin aliquet. Aliquam tempus luctus sem. Sed id felis dolor. Sed vel odio at justo sollicitudin euismod. Integer a nisi ut massa bibendum placerat adipiscing eu nunc.
      <p>
      Cras condimentum, turpis vel egestas tristique, neque sapien consequat odio, ut congue est turpis in lectus. Sed imperdiet lacus nec purus pretium pretium.
      </p>
    </div>

    <div>
      <c:choose>
        <c:when test="${not compoundSp.sp.linked}">
          <a class="btn btn-primary" href="<c:url value="/requests/linkrequest.shtml">
            <c:param name="spEntityId" value="${compoundSp.sp.spid}" />
          </c:url>"
             title="<spring:message code="jsp.sp_detail.requestlink"/>"><spring:message code="jsp.sp_detail.requestlink"/>
          </a>
        </c:when>
        <c:when test="${compoundSp.sp.splinked}">
          <a class="btn btn-primary" href="<c:url value="/requests/unlinkrequest.shtml">
            <c:param name="spEntityId" value="${compoundSp.sp.spid}" />
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

    <h2>Screenshots van <c:out value="${spname}"/></h2>

    <div class="screenshots-holder gallery-holder">
      <ul class="gallery">
        <li>
          <a href="../images/screenshots/screenshot-1.jpg">
            <img src="../images/screenshots/screenshot-1-thumb.jpg" alt="Foo">
          </a>
        </li><li>
          <a href="../images/screenshots/screenshot-1.jpg">
            <img src="../images/screenshots/screenshot-1-thumb.jpg" alt="Foo">
          </a>
        </li><li>
          <a href="../images/screenshots/screenshot-1.jpg">
            <img src="../images/screenshots/screenshot-1-thumb.jpg" alt="Foo">
          </a>
        </li><li>
          <a href="../images/screenshots/screenshot-1.jpg">
            <img src="../images/screenshots/screenshot-1-thumb.jpg" alt="Foo">
          </a>
        </li>
      </ul>
    </div>
  </section>

</div><!-- .column-center.content-holder -->


<jsp:include page="foot.jsp" />