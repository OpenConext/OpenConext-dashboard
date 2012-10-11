<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ include file="include.jsp" %>

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

<footer>
  <div class="content-some-dense">
    <spring:message code="jsp.general.footertext" htmlEscape="false"/>
  </div>
</footer>
</div>

<script src="<c:url value="/js/jquery/jquery-1.7.2.min.js"/>"></script>
<script src="<c:url value="/js/bootstrap/bootstrap-2.0.2.min.js"/>"></script>
<c:choose>
  <c:when test="${dev eq true}">
    <script src="<c:url value="/js/main.js"/>"></script>
    <script src="<c:url value="/js/modules/global.js"/>"></script>
    <script src="<c:url value="/js/modules/form.js"/>"></script>
    <script src="<c:url value="/js/modules/message.js"/>"></script>
    <script src="<c:url value="/js/modules/table.js"/>"></script>
    <script src="<c:url value="/js/modules/reservation.js"/>"></script>
    <script src="<c:url value="/js/jquery/jquery.ui.widget.js"/>"></script>
    <script src="<c:url value="/js/jquery/jquery.fileupload.js"/>"></script>
    <script src="<c:url value="/js/jquery/jquery.iframe-transport.js"/>"></script>
  </c:when>
  <c:otherwise>
    <script src="<c:url value="/js/script.min.js"/>"></script>
  </c:otherwise>
</c:choose>

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

<c:if test="${param.chart eq true}">
<tags:sp_renderchart/>
</c:if>

<c:if test="${param.datatables eq true}">
<tags:sp_renderdatatables columnFilter="${param.columnFilter}"/>
</c:if>
</body>
</html>