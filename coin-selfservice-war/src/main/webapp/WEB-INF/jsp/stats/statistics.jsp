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

<div class="column-center content-holder no-right-left">

    <h1>${title}</h1>

    <p id="stats-info-text">
        <spring:message code="jsp.stats.info" arguments="${selectedIdp.name}"/>
    </p>

    <section class="statistics-holder">

        <nav class="statistics-navigation">
            <div class="show">
                <a href="#" class="back hide">
                    <i class="icon-arrow-left"></i> <spring:message code="jsp.stats.back_to_overview"/>
                </a>
                <a href="#" class="forward hide">
                    <i class="icon-arrow-right"></i> <spring:message code="jsp.stats.back_to_sp"/>
                </a>
            </div>
        </nav>

        <nav class="statistics-filters">
            <div class="show">
                <a href="#" data-show="year">
                    <spring:message code="jsp.stats.year"/>
                </a>
                <a href="#" data-show="month">
                    <spring:message code="jsp.stats.month"/>
                </a>
                <a href="#" data-show="week">
                    <spring:message code="jsp.stats.week"/>
                </a>
                <a class="csv" href="<c:url value="/stats/stats.csv"/>">
                    CSV
                </a>
            </div>
            <div class="time-offset">
                <select id="choose-time-offset" title="<spring:message code="jsp.stats.select_offset" />"></select>
            </div>
        </nav>

        <div id="sp-overview-chart" class="ajax-loader" data-idp="${selectedIdp.id}" data-is-god="false"></div>
        <div id="sp-detail-chart" data-spEntityId="${selectedSp}"></div>
    </section>
</div>
<script>
    var login_stats = eval(<c:out escapeXml="false" value="${login_stats}"/>);
</script>
<jsp:include page="../foot.jsp"/>