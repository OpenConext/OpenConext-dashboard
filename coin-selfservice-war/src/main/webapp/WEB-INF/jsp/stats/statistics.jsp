<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ include file="../include.jsp" %>
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

<spring:message var="title" code="jsp.stats.title"/>

<jsp:include page="../head.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>

<div class="column-center content-holder">
  <h1>${title}</h1>
  
    <!-- Shorter then otherwise -->
    <c:if test="${isGod eq true}">
      <p id="stats-info-text"><spring:message code="jsp.stats.info.admin"/></p>
    </c:if>
    <c:if test="${isGod eq false}">
      <c:set var="idpname"><tags:providername provider="${selectedidp}" /></c:set>
      <p id="stats-info-text">
        <spring:message code="jsp.stats.info" arguments="${idpname}"/>
      </p>
    </c:if>
    
  <section class="statistics-holder" >

    <nav class="statistics-navigation"> 
      <div class="show">
      <a href="#" class="back hide">
        <i class="icon-arrow-left"></i> <spring:message code="jsp.stats.back_to_overview"/>
      </a>
      <c:if test="${isGod}">
        <a href="#" class="forward hide">
          <i class="icon-arrow-right"></i> <spring:message code="jsp.stats.back_to_sp"/>
        </a>
      </c:if>
      </div>
    </nav>

    <c:if test="${isGod eq true}">
      <nav class="statistics-idp-filter">
        <div class="show">
          <select id="idp-select2">
            <option></option>
            <option value="ALL"><spring:message code="jsp.stats.all_idps"/></option>
            <c:forEach items="${allIdps}" var="idp">
              <option value="${idp.entityId}">${idp.name}</option>
            </c:forEach>
          </select>
        </div>
      </nav>
    </c:if>

    <nav class="statistics-filters">
      <div class="show">
        <a href="#" data-show="all">
          <spring:message code="jsp.stats.all" />
        </a>
        <a href="#" data-show="quarter">
          <spring:message code="jsp.stats.quarter" />
        </a>
        <a href="#" data-show="month">
          <spring:message code="jsp.stats.month" />
        </a>
        <a href="#" data-show="week">
          <spring:message code="jsp.stats.week" />
        </a>
      </div>
      <div class="time-offset">
        <select id="choose-time-offset" title="<spring:message code="jsp.stats.select_offset" />"></select>
      </div>
    </nav>
    
    <div id="sp-overview-chart" class="ajax-loader" data-idp="${selectedidp.id}" data-is-god="${isGod}"></div>
    <div id="sp-detail-chart"></div>
  </section>
</div>

<jsp:include page="../foot.jsp">
  <jsp:param name="datatables" value="true"/>
</jsp:include>