<!doctype html>

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="include.jsp"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<html lang="${locale.language}">
  <head>
    <meta charset="UTF-8">
    <title><spring:message code="jsp.general.pageTitle" arguments="${param.title}" /></title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge;chrome=1">

    <spring:eval expression="@environment.acceptsProfiles('dev')" var="dev"/>
    <c:choose>
      <c:when test="${dev eq true}">
        <link rel="stylesheet" href="<c:url value="/css/bootstrap-2.0.4.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/bootstrap-button.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/bootstrap-datepicker.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/bootstrap-dropdown.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/bootstrap-form.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/bootstrap-generic.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/bootstrap-modal.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/bootstrap-navbar.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/bootstrap-pagination.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/bootstrap-popover.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/bootstrap-responsive.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/bootstrap-table.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/bootstrap-tooltip.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/component-datatables.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/font-awesome.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/select2.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/app-detail.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/base.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/forms.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/gallery.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/grid.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/header.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/modals.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/pagination.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/secondary-menu.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/shopadmin.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/tables.css"/>" />
        <link rel="stylesheet" href="<c:url value="/css/taxonomy.css"/>" />
        <%--
        Reminder: if you change this list in any way, remember to update the corresponding list in the POM (for the minify-plugin.
       --%>

      </c:when>
      <c:otherwise>
        <spring:eval expression="@environment.getProperty('git.build.id')" var="buildTimestamp"/>
        <link rel="stylesheet" href="<c:url value="/css/style.min.css"/>?t=${buildTimestamp}" />
      </c:otherwise>
    </c:choose>

  <!--[if lt IE 9]>
  <script src="<c:url value="/js/tools/html5shiv.js"/>"></script>
  <![endif]-->
</head>

<body>
  <script>document.body.className = 'js-loading'</script>

  <spring:url value="/shopadmin/all-spslmng.shtml" var="homeUrl" htmlEscape="true" />
<div id="swappable-menus">
  <header class="header">
    <a class="logo" href="${homeUrl}"> <img src="<c:url value="/images/surf-conext-logo.png"/>" alt="Surf Conext"> </a>

    <nav class="primary-navigation">
      <ul>
        <li>
          <a href="<spring:url value="/shopadmin/clean-cache.shtml"/>">
            <spring:message code="jsp.general.clean.cache" />
          </a>
        </li>
        <li class="user">
          <spring:message code="jsp.general.welcome" />
          <span>
            <sec:authentication property="principal.displayName" scope="request" htmlEscape="true" />
          </span>
        </li>

        <li class="logout">
          <a href="<spring:url value="/Shibboleth.sso/Logout?target=/" htmlEscape="true"  />"> <!-- Initiates a global SAML logout (with the IDP) -->
            <spring:message code="jsp.general.logout" />
          </a>
        </li>
      </ul>
    </nav>
  </header>
  <c:if test="${not empty menu.menuItems}">
    <nav class="secondary-menu">
      <ul>
        <c:forEach items="${menu.menuItems}" var="menuItem">
          <c:set var="index" value="${fn:indexOf(menuItem.label,'.title')}" />
          <c:set var="classname" value="${fn:substring(menuItem.label, 4, index)}" />
          <li class="${classname}<c:if test="${menuItem.selected}"> active</c:if>">
            <spring:url value="${menuItem.url}" htmlEscape="true" var="url" />
              <a href="${url}"><spring:message code="${menuItem.label}" /></a>
          </li>
        </c:forEach>
      </ul>
    </nav>
  </c:if>
</div>
