<%@ page isErrorPage="true" %>
<%@ include file="include.jsp"%>
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
<!doctype html>

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<html lang="en">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <c:set var="errorMessage"><spring:message code="jsp.general.errordescription" text="SURFconext - An error occurred"/></c:set>

    <title>${errorMessage}</title>

    <link rel="stylesheet" href="<c:url value="/css/bootstrap-2.0.4.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/bootstrap-button.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/bootstrap-dropdown.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/bootstrap-generic.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/bootstrap-modal.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/bootstrap-responsive.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/font-awesome.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/header.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/app-grid.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/app-detail.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/forms.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/graphs.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/gallery.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/header.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/idp.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/modals.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/notifications.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/pagination.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/secondary-menu.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/tables.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/taxonomy.css"/>"/>
    <link rel="stylesheet" href="<c:url value="/css/grid.css"/>"/>

    <!--[if lt IE 9]>
      <script src="<c:url value="/js/tools/html5shiv.js"/>"></script>
    <![endif]-->
  </head>
<body>
  <script>document.body.className = 'js-loading'</script>
  
  <spring:url value="/app-overview.shtml" var="homeUrl" htmlEscape="true" />

  <header class="header">
    <a class="logo" href="${homeUrl}"> <img src="<c:url value="/images/surf-conext-logo.png"/>" alt="Surf Conext">
    </a>
  </header>

  <div class="wrapper has-left">
    <div class="column-center content-wrapper">

      <h1>${errorMessage}</h1>

      <c:set var="endUserMessage">
        <c:choose>
          <c:when test="${pageContext.errorData.statusCode == 400}">Bad request. Go to the <a href="<c:url value="/"/>">homepage</a>.</c:when>
          <c:when test="${pageContext.errorData.statusCode == 403}">Unfortunately you don't have the permission to access the requested url.<br> You can visit the <a href="<c:url value="/"/>">homepage</a> instead or send an email to <a href="mailto:help@surfconext.nl">help@surfconext.nl</a></c:when>
          <c:when test="${pageContext.errorData.statusCode == 404}">Page not found. Go to the <a href="<c:url value="/"/>">homepage</a>.</c:when>
          <c:when test="${pageContext.errorData.statusCode == 500}">An error occurred. Please try to reload the page or go to the <a href="<c:url value="/"/>">homepage</a>.</c:when>
        </c:choose>
      </c:set>

      <p>${endUserMessage}</p>

      <c:if test="${developmentMode}">
        <div class="well">
        <h2>Details (shown only in development mode)</h2>
        <dl>
          <dt>Requested uri</dt>
          <dd>${pageContext.errorData.requestURI}</dd>
          <dt>Servlet</dt>
          <dd>${pageContext.errorData.servletName}</dd>
          <dt>  Status code</dt>
          <dd>${pageContext.errorData.statusCode}</dd>
          <dt>Exception message</dt>
          <dd>${pageContext.errorData.throwable.message}</dd>
          <dt>Stacktrace</dt>
          <c:forEach var="trace" items="${pageContext.errorData.throwable.stackTrace}">
            <dd>${trace}</dd>
          </c:forEach>
        </dl>
        </div>
      </c:if>
    </div>
  </div>


  <footer class="footer">
    <div class="content-some-dense">
      <a href="http://www.surfnet.nl" target="_blank">
        SURFnet
      </a>
      |
      <a href="mailto:help@surfconext.nl ">
          help@surfconext.nl
      </a>
    </div>
  </footer>


</body>
</html>