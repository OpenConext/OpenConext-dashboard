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

  <nav class="statistics-filters">
    <ul>
      <li class="back hide">
        <a href="#">
          <i class="icon-arrow-left"></i>
        </a>
      </li>
      <li class="time-offset">
        <a class="prev-time-offset" href="#">
          <i class="icon-arrow-left"></i>
        </a>
        <select id="choose-time-offset"></select>
        <a class="next-time-offset" href="#">
          <i class="icon-arrow-right"></i>
        </a>
      </li>
      <li class="show">
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
      </li>
    </ul>
  </nav>

  <section class="statistics-holder">
    <div id="sp-overview-chart"></div>
    <div id="sp-detail-chart"></div>
  </section>
</div>

<jsp:include page="../foot.jsp">
  <jsp:param name="datatables" value="true"/>
</jsp:include>