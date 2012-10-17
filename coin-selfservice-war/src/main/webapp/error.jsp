<%@ include file="WEB-INF/jsp/include.jsp"%>
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
    <title>SURFconext - Selfservice - An error occurred</title>

    <link rel="stylesheet" href="<c:url value="/css/bootstrap-2.0.4.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/bootstrap-alert.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/bootstrap-button.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/bootstrap-dropdown.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/bootstrap-generic.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/bootstrap-modal.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/bootstrap-responsive.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/font-awesome.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/generic.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/layout.css"/>" />
    <link rel="stylesheet" href="<c:url value="/css/screen.css"/>" />

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

      <h1>SURFconext Self service</h1>
      <p>An error occurred. Please try to reload the page or go to the <a href="<c:url value="/"/>">homepage</a>.</p>
    </div>
  </div>
  <footer>
    <div class="content-some-dense">
      SURFnet | T +31 302 305 305 | <a href="mailto:surfconext-beheer@surfnet.nl">surfconext-beheer@surfnet.nl</a>
    </div>
  </footer>
</body>
</html>