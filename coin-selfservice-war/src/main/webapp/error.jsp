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
  <title>SURFconext - Selfservice - An error occurred</title>

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

  <!--[if lt IE 9]>
  <script src="<c:url value="/js/tools/html5shiv.js"/>"></script>
  <![endif]-->
</head>
<body>
<div class="wrapper">

  <header>
    <a href="<c:url value="/"/>">Error</a>

    <h1>SURFconext Self service</h1>
  </header>



  <section class="section-modal">

    <div class="content">

      <div class="modal modal-relative">
        <div class="modal-header">
          <h3>Error</h3>
        </div>
        <div class="modal-body">
          <i class="icon-remove-sign"></i>

          <p>      An error occurred. The world has come to an end.</p>
        </div>
      </div>

    </div>

  </section>



  <footer>
    <div class="content-some-dense">
      SURFnet bv | Postbus 190-35, 3501 DA Utrecht | T +31 302 305 305 | F +31 302 305 329 | <a
        href="mailto:admin@surfnet.nl">Admin@SURFnet.nl</a>
    </div>
  </footer>
</div>

<script src="<c:url value="/js/jquery/jquery-1.7.2.min.js"/>"></script>
<script src="<c:url value="/js/bootstrap/bootstrap-2.0.2.min.js"/>"></script>
    <script src="<c:url value="/js/main.js"/>"></script>
    <script src="<c:url value="/js/modules/global.js"/>"></script>
    <script src="<c:url value="/js/modules/form.js"/>"></script>
    <script src="<c:url value="/js/modules/message.js"/>"></script>
    <script src="<c:url value="/js/modules/table.js"/>"></script>
    <script src="<c:url value="/js/modules/reservation.js"/>"></script>

<spring:url var="url_plugin_socket" value="/js/jquery/jquery-socket-1.0a.js"/>
<spring:url var="url_plugin_autoSuggest" value="/js/jquery/jquery-autoSuggest.js"/>
<spring:url var="url_plugin_datepicker" value="/js/datepicker/bootstrap-datepicker.js"/>
<spring:url var="url_plugin_dropdownReload" value="/js/jquery/dropdown-reload.js"/>

<script>
  app.plugins = {
    jquery:{
      socket:'<c:out value="${url_plugin_socket}"/>',
      autoSuggest:'<c:out value="${url_plugin_autoSuggest}"/>',
      datepicker:'<c:out value="${url_plugin_datepicker}"/>',
      dropdownReload:'<c:out value="${url_plugin_dropdownReload}"/>'
    }
  }
</script>

</body>
</html>