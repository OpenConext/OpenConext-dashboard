<!doctype html>

<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="include.jsp"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<html lang="${locale.language}">
  <head>
    <meta charset="UTF-8">
    <title><tags:context-specific messageKey="jsp.general.pageTitle" isDashBoard="${isDashBoard}"/></title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <meta http-equiv="X-UA-Compatible" content="IE=edge;chrome=1">

    <c:choose>
      <c:when test="${developmentMode eq true}">
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
        <%--
        Reminder: if you change this list in any way, remember to update the corresponding list in the POM (for the minify-plugin.
       --%>

      </c:when>
      <c:otherwise>
        <spring:eval expression="@applicationProperties['app.timestamp']" var="buildTimestamp"/>
        <link rel="stylesheet" href="<c:url value="/css/style.min.css"/>?t=${buildTimestamp}" />
      </c:otherwise>
    </c:choose>

  <!--[if lt IE 9]>
  <script src="<c:url value="/js/tools/html5shiv.js"/>"></script>
  <![endif]-->
</head>

<body>
  <script>document.body.className = 'js-loading'</script>

  <spring:url value="/app-overview.shtml" var="homeUrl" htmlEscape="true" />
<div id="swappable-menus">
  <header class="header">
    <a class="logo" href="${homeUrl}"> <img src="<c:url value="/images/surf-conext-logo.png"/>" alt="Surf Conext">
    </a>

    <nav class="primary-navigation">
      <ul>
        <c:if test="${fn:length(institutionIdentityProviders) gt 0}">

          <c:if test="${not empty switchedIdentitySwitch.role}">
            <li class="identity-disclaimer">
              Your current role is '<spring:message code="jsp.role.information.key.${switchedIdentitySwitch.role}"/>', institution: '${selectedIdp.name}'
            </li>
          </c:if>
            <li> <spring:url var="switchIdentityLink" value="/identity/switch.shtml"/>
              <a id="switch-identity" href="${switchIdentityLink}">
                <spring:message code="jsp.identity.switch"/>
                <i class="inlinehelp icon-question-sign" data-title="<spring:message code="jsp.identity.switch"/>"
                    data-placement="bottom" data-content="<spring:message htmlEscape="true" code="jsp.identity.help" />"></i>
              </a>
            </li>
       </c:if>
        <li class="user">
          <spring:message code="jsp.general.welcome" /> <a href="<spring:url value="/user.shtml" htmlEscape="true" />">
            <sec:authentication property="principal.displayName" scope="request" htmlEscape="true" />
          </a>
        </li>
      <c:if test="${empty switchedIdentitySwitch.role}">
          <li class="role-switch">
          <c:if test="${fn:length(idps) gt 1}">
            <ul class="user-dropdown">
              <c:forEach items="${idps}" var="idp">
                <li class="user-role-manager ${selectedIdp.id == idp.id ? 'active' : ''}" data-roleId="${idp.id}">
                  <spring:url var="toggleLink" value="/app-overview.shtml" htmlEscape="true">
                    <spring:param name="switchIdpId" value="${idp.id}" />
                  </spring:url>
                    <c:choose>
                      <c:when test="${fn:length(idp.name) gt 23}">
                        <a href="${toggleLink}" title="${idp.name}"><c:out value="${fn:substring(idp.name, 0, 23)}"/>...</a>
                      </c:when>
                      <c:otherwise>
                        <a href="${toggleLink}" title="${idp.name}"><c:out value="${idp.name}"/></a>
                      </c:otherwise>
                    </c:choose>
                </li>
              </c:forEach>
            </ul>
          </c:if>
          <c:if test="${fn:length(idps) == 1}">
            ${idps[0].name}
          </c:if>
        </li>
      </c:if>

        <spring:url value="" var="langNL" htmlEscape="true">
          <c:forEach var="par" items="${paramValues}">
            <c:if test="${par.key ne 'lang'}">
              <spring:param name="${par.key}" value="${par.value[0]}" />
            </c:if>
          </c:forEach>
          <spring:param name="lang" value="nl" />
        </spring:url>
        <spring:url value="" var="langEN" htmlEscape="true">
          <c:forEach var="par" items="${paramValues}">
            <c:if test="${par.key ne 'lang'}">
              <spring:param name="${par.key}" value="${par.value[0]}" />
            </c:if>
          </c:forEach>
          <spring:param name="lang" value="en" />
        </spring:url>

        <li class="language">
          <div>

          <c:choose>
            <c:when test="${locale.language  eq 'en'}">
              <a href="${langNL}" hreflang="nl" title="Nederlands">NL</a> | <span>EN</span>
            </c:when>
            <c:otherwise>
              <span>NL</span> | <a href="${langEN}" hreflang="en" title="English">EN</a>
            </c:otherwise>
          </c:choose>
            </div>
          </li>
        <li class="help">
          <c:set var="supporturl"><tags:context-specific messageKey="jsp.general.footertext.supportpages.url" isDashBoard="${isDashBoard}"/></c:set>
          <a href="${supporturl}"  target="_blank">
            <spring:message code="jsp.general.footertext.supportpages"/>
          </a>
        </li>
        <li class="logout">
          <a href="<spring:url value="/logout.shtml" htmlEscape="true" />">
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
<div>
