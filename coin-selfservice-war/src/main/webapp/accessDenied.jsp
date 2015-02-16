<%@ page isErrorPage="true" %>
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

<html lang="en">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <c:set var="errorMessage"><spring:message code="jsp.general.errordescription" text="SURFconext - An error occurred"/></c:set>

    <title>${errorMessage}</title>

    <link rel="stylesheet" href='<c:url value="/css/bootstrap-2.0.4.css"/>' />
    <link rel="stylesheet" href='<c:url value="/css/bootstrap-button.css"/>' />
    <link rel="stylesheet" href='<c:url value="/css/bootstrap-dropdown.css"/>' />
    <link rel="stylesheet" href='<c:url value="/css/bootstrap-generic.css"/>' />
    <link rel="stylesheet" href='<c:url value="/css/bootstrap-modal.css"/>' />
    <link rel="stylesheet" href='<c:url value="/css/bootstrap-responsive.css"/>' />
    <link rel="stylesheet" href='<c:url value="/css/font-awesome.css"/>' />
    <link rel="stylesheet" href='<c:url value="/css/header.css"/>'/>
    <link rel="stylesheet" href='<c:url value="/css/app-grid.css"/>'/>
    <link rel="stylesheet" href='<c:url value="/css/app-detail.css"/>'/>
    <link rel="stylesheet" href='<c:url value="/css/forms.css"/>'/>
    <link rel="stylesheet" href='<c:url value="/css/graphs.css"/>'/>
    <link rel="stylesheet" href='<c:url value="/css/gallery.css"/>'/>
    <link rel="stylesheet" href='<c:url value="/css/header.css"/>'/>
    <link rel="stylesheet" href='<c:url value="/css/idp.css"/>'/>
    <link rel="stylesheet" href='<c:url value="/css/modals.css"/>'/>
    <link rel="stylesheet" href='<c:url value="/css/notifications.css"/>'/>
    <link rel="stylesheet" href='<c:url value="/css/pagination.css"/>'/>
    <link rel="stylesheet" href='<c:url value="/css/secondary-menu.css"/>'/>
    <link rel="stylesheet" href='<c:url value="/css/tables.css"/>'/>
    <link rel="stylesheet" href='<c:url value="/css/taxonomy.css"/>'/>
    <link rel="stylesheet" href='<c:url value="/css/grid.css"/>'/>

  </head>
<body>
  <spring:url value="/" var="homeUrl" htmlEscape="true" />

  <header class="header">
    <a class="logo" href="${homeUrl}"> <img src='<c:url value="/images/surf-conext-logo.png"/>' alt="Surf Conext">
    </a>
  </header>

  <div class="wrapper has-left">
    <div class="column-center content-wrapper">
      <h1>Access denied</h1>
      <p>
        Unfortunately you don't have the permission to access the requested url.<br> You can visit the <a href='<c:url value="/"/>'>homepage</a> instead or send an email to <a href="mailto:help@surfconext.nl">help@surfconext.nl</a>
      </p>
    </div>
  </div>


  <footer class="footer">
    <div class="content-some-dense">
      <a href="http://www.surfnet.nl" target="_blank">SURFnet</a>
      |
      <a href="mailto:help@surfconext.nl">help@surfconext.nl</a>
    </div>
  </footer>

</body>
</html>
