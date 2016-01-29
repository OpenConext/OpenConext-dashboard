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

<footer class="footer">
  <div class="content-some-dense">
    <a href="http://www.surfnet.nl" target="_blank">
      <spring:message code="jsp.general.footertext.name"/>
    </a>
    |
    <c:set var="tosurl"><spring:message code="jsp.general.footertext.tos.url"/></c:set>
    <a href="${tosurl}"  target="_blank">
      <spring:message code="jsp.general.footertext.tos"/>
    </a>
    |
    <a href="mailto:<spring:message code="jsp.general.footertext.mail"/>">
      <spring:message code="jsp.general.footertext.mail"/>
    </a>
  </div>
</footer>

<spring:eval expression="@environment.acceptsProfiles('dev')" var="dev"/>
<c:choose>
  <c:when test="${dev eq true}">
    <script src="<c:url value="/js/jquery/jquery-1.8.3.min.js"/>"></script>
    <script src="<c:url value="/js/bootstrap/bootstrap-2.0.3.min.js"/>"></script>
    <script src="<c:url value="/js/bootstrap/bootstrap-tooltip.js"/>"></script>
    <script src="<c:url value="/js/bootstrap/bootstrap-popover.js"/>"></script>
    <script src="<c:url value="/js/bootstrap/bootstrap-transition.js"/>"></script>
    <script src="<c:url value="/js/bootstrap/bootbox.min.js"/>"></script>
    <script src="<c:url value="/js/jquery/jquery.datatables.1.9.4.min.js"/>"></script>
    <script src="<c:url value="/js/jquery/jquery.datatables.columnfilter.1.4.7.js"/>"></script>
    <script src="<c:url value="/js/jquery/jquery.ui.widget.js"/>"></script>
    <script src="<c:url value="/js/jquery/jquery.fileupload.js"/>"></script>
    <script src="<c:url value="/js/jquery/jquery.iframe-transport.js"/>"></script>
    <script src="<c:url value="/js/jquery/jquery.transit.js"/>"></script>
    <script src="<c:url value="/js/jquery/jquery.tickback.min.js"/>"></script>
    <script src="<c:url value="/js/jquery/select2.js"/>"></script>
    <script src="<c:url value="/js/jquery/jquery.fixedheadertable.js"/>"></script>
    <script src="<c:url value="/js/main.js"/>"></script>
    <script src="<c:url value="/js/modules/forms.js"/>"></script>
    <script src="<c:url value="/js/modules/gallery.js"/>"></script>
    <script src="<c:url value="/js/modules/global.js"/>"></script>
    <script src="<c:url value="/js/modules/message.js"/>"></script>
    <script src="<c:url value="/js/modules/respond.js"/>"></script>
    <script src="<c:url value="/js/modules/compound-sp-edit.js"/>"></script>
    <script src="<c:url value="/js/modules/table.js"/>"></script>
    <script src="<c:url value="/js/modules/idp-status.js"/>"></script>
    <script src="<c:url value="/js/modules/app-detail.js"/>"></script>
    <script src="<c:url value="/js/modules/shop-admin.js"/>"></script>
    <script src="<c:url value="/js/modules/sp-services.js"/>"></script>
    <script src="<c:url value="/js/modules/taxonomy.js"/>"></script>
    <script src="<c:url value="/js/modules/csp-taxonomy.js"/>"></script>
    <script src="<c:url value="/js/tools/html5shiv.js"/>"></script>
    <script src="<c:url value="/js/tools/ios-orientationchange-fix.js"/>"></script>
    <script src="<c:url value="/js/tools/sizewatcher.js"/>"></script>
    <%-- 
      Reminder: if you change this list in any way, remember to update the corresponding list in the POM (for the minify-plugin.
    --%> 
  </c:when>
  <c:otherwise>
    <spring:eval expression="@environment.getProperty('git.build.id')" var="buildTimestamp"/>
    <script src="<c:url value="/js/script.min.js"/>?t=${buildTimestamp}"></script>
  </c:otherwise>
</c:choose>
<script>
  var contextPath = "${pageContext.request.contextPath}";
</script>
</body>
</html>
