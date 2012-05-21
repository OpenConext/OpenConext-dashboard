<!doctype html>
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
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<html lang="en">
<head>
  <meta charset="UTF-8"/>
  <meta content="width=device-width,initial-scale=1" name="viewport"/>
  <title>SURFconext - Selfservice home</title>

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
<div class="wrapper">

<header>
  <a href="<c:url value="/"/>">Home</a>

  <h1>SURFconext Self service</h1>
</header>

<section class="user-box content-dense">
  <span class="user-name">ReneS</span>
  <a href="#" class="logout">Logout <i class="icon-signout"></i></a>

  <!-- b:dropdown -->

  <div class="dropdown">

    <div class="dropdown-toggle user-role-noc" data-toggle="dropdown">
      <div class="user">
        NOC Engineer<br>
        Institute A
        <b class="caret"></b>
      </div>
    </div>

    <form action="#" method="POST" class="dropdown-menu">
      <ul>
        <li class="user-role-default" data-roleId="default">
          <div class="user">
            Default/New User
          </div>
        </li>
        <li class="user-role-manager" data-roleId="manager">
          <div class="user">
            Manager/Admin<br>
            Institute C
          </div>
        </li>
        <li class="user-role-user" data-roleId="user">
          <div class="user">
            BoD User<br>
            Some institute with a very long and uncommon name
          </div>
        </li>
        <li class="user-role-noc" data-roleId="noc">
          <div class="user">
            NOC Engineer
          </div>
        </li>
      </ul>
    </form>

  </div>

</section>

<!-- b:navbar -->

<nav class="navbar">
  <div class="navbar-inner">
    <div class="container">
      <ul class="nav">
        <li <c:if test="${param.activeSection == 'home'}">class="active"
        </c:if>><a href="home.shtml">Home</a></li>
        <li <c:if test="${param.activeSection == 'styleguide'}">class="active"
        </c:if>><a href="styleguide.shtml">Styleguide</a></li>
      </ul>
    </div>
  </div>
</nav>
