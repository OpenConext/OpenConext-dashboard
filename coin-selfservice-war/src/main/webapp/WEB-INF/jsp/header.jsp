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

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="include.jsp" %>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta content="width=device-width,initial-scale=1" name="viewport"/>
  <title>
    <spring:message code="jsp.general.pageTitle" arguments="${param.title}"/>
  </title>

  <c:choose>
    <c:when test="${dev eq true}">
      <link rel="stylesheet" href="<c:url value="/css/bootstrap-2.0.2.min.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/font-awesome.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/bootstrap-alert.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/bootstrap-button.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/bootstrap-datepicker.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/bootstrap-dropdown.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/bootstrap-form.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/bootstrap-generic.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/bootstrap-navbar.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/bootstrap-pagination.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/bootstrap-popover.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/bootstrap-table.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/bootstrap-tooltip.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/bootstrap-modal.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/layout.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/generic.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/component-userbox.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/component-autoSuggest.css"/>"/>
      <link rel="stylesheet" href="<c:url value="/css/component-datatables.css"/>"/>
    </c:when>
    <c:otherwise>
      <link rel="stylesheet" href="<c:url value="/css/style.min.css"/>"/>
    </c:otherwise>
  </c:choose>

  <!--[if lt IE 9]>
  <script src="<c:url value="/js/tools/html5shiv.js"/>"></script>
  <![endif]-->
</head>
<body>
<spring:url value="/home.shtml" var="homeUrl" htmlEscape="true"/>

<div class="wrapper">

<header>
  <a href="${homeUrl}"><spring:message code="jsp.header.home"/></a>

  <h1><spring:message code="jsp.header.title"/></h1>
</header>

<spring:url value="" var="langNL" htmlEscape="true">
  <c:if test="${not empty sp}"><spring:param name="spEntityId" value="${sp.id}" /></c:if>
  <spring:param name="lang" value="nl" />
</spring:url>
<spring:url value="" var="langEN" htmlEscape="true">
  <c:if test="${not empty sp}"><spring:param name="spEntityId" value="${sp.id}" /></c:if>
  <spring:param name="lang" value="en" />
</spring:url>

<sec:authorize access="hasAnyRole('ROLE_USER','ROLE_ADMIN')">
  <section class="user-box content-dense">
    <span class="user-name">
      <sec:authentication property="principal.displayName" scope="request" htmlEscape="true"/>
    </span>
    <span class="user-name">
    <a href="${langNL}">NL</a>|<a href="${langEN}">EN</a>
    </span>
    <a href="<spring:url value="/j_spring_security_logout" htmlEscape="true" />" class="logout">
      <spring:message code="jsp.general.logout"/> <i class="icon-signout"></i></a>

    <%-- b:dropdown --%>

    <div class="dropdown">

      <div class="dropdown-toggle user-role-manager" data-toggle="dropdown">
        <div class="user">
          <c:out value="${selectedidp.name}"/>
          <c:if test="${fn:length(idps) gt 1}">
            <b class="caret"></b>
          </c:if>
        </div>
      </div>

      <%--@elvariable id="idps" type="java.util.List<nl.surfnet.coin.selfservice.domain.IdentityProvider>"--%>
      <c:if test="${fn:length(idps) gt 1}">
        <ul class="dropdown-menu">
          <c:forEach items="${idps}" var="idp">
            <li class="user-role-manager" data-roleId="${idp.id}">
              <spring:url var="toggleLink" value="/idpadmin/linked-sps.shtml" htmlEscape="true">
                <spring:param name="idpId" value="${idp.id}"/>
              </spring:url>
              <a href="${toggleLink}">
                <div class="user">
                  <c:out value="${idp.name}"/>
                </div>
              </a>
            </li>
          </c:forEach>
        </ul>
      </c:if>

    </div>

  </section>
</sec:authorize>

<%-- b:navbar --%>

<nav class="navbar">
  <div class="navbar-inner">
    <div class="container">
      <ul class="nav">
        <li <c:if test="${activeSection == 'home'}">class="active"
        </c:if>><a href="${homeUrl}"><spring:message code="jsp.home.title"/></a></li>
        <spring:url value="/idpadmin/all-sps.shtml" var="allSpsUrl" htmlEscape="true"/>
        <li <c:if test="${activeSection == 'all-sps'}">class="active"
        </c:if>><a href="${allSpsUrl}"><spring:message code="jsp.allsp.title"/></a></li>

        <li <c:if test="${activeSection == 'actions'}">class="active"
        </c:if>><a href="<c:url value="/idpadmin/actions.shtml"/>"><spring:message code="jsp.actions.title"/></a>
        </li>
      </ul>
    </div>
  </div>
</nav>
