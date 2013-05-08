<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ include file="../include.jsp" %>
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

<%--@elvariable id="sps" type="java.util.List<nl.surfnet.coin.selfservice.domain.ServiceProvider>"--%>
<spring:message var="title" code="jsp.allsplmng.title"/>

<jsp:include page="../head.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>

<div class="column-center content-holder">

  <section class="data-table-holder">

    <h1>${title}<i class="inlinehelp icon-question-sign" data-title="${title}" data-placement="bottom" data-content="<spring:message htmlEscape="true" code="jsp.lmng_binding_overview.title.help" />"></i></h1>
    <div class="data-table-wrapper">
      <c:set var="searchPlaceholder"><spring:message code="jsp.search.placeholder.sp" /></c:set>
      <table id="sp_overview_table" class="table table-bordered table-striped table-above-pagination table-sortable" data-search-placeholder="${searchPlaceholder}">
        <thead>
        <tr>
          <th>
            <spring:message code="jsp.lmng_binding_overview.name"/>
          </th>
          <th>
            <spring:message code="jsp.lmng_binding_overview.idponly"/>
            <i class="inlinehelp icon-question-sign" data-title="<spring:message htmlEscape="true" code="jsp.lmng_binding_overview.idponly" />" data-content="<spring:message htmlEscape="true" code="jsp.lmng_binding_overview.idponly.help" />"></i>
          </th>
          <th>
            <spring:message code="jsp.lmng_binding_overview.idphide"/>
            <i class="inlinehelp icon-question-sign" data-title="<spring:message htmlEscape="true" code="jsp.lmng_binding_overview.idphide" />" data-content="<spring:message htmlEscape="true" code="jsp.lmng_binding_overview.idphide.help" />"></i>
          </th>
          <th>
            <spring:message code="jsp.lmng_binding_overview.idphide_protected"/>
            <i class="inlinehelp icon-question-sign" data-title="<spring:message htmlEscape="true" code="jsp.lmng_binding_overview.idphide_protected" />" data-content="<spring:message htmlEscape="true" code="jsp.lmng_binding_overview.idphide_protected.help" />"></i>
          </th>
          <th>
            <spring:message code="jsp.lmng_binding_overview.lmngid"/>
            <i class="inlinehelp icon-question-sign" data-title="<spring:message htmlEscape="true" code="jsp.lmng_binding_overview.lmngid" />" data-content="<spring:message htmlEscape="true" code="jsp.lmng_binding_overview.lmngid.help" />"></i>
          </th>
          <th>
            <spring:message code="jsp.lmng_binding_overview.detail"/>
            <i class="inlinehelp icon-question-sign" data-title="<spring:message htmlEscape="true" code="jsp.lmng_binding_overview.detail" />" data-content="<spring:message htmlEscape="true" code="jsp.lmng_binding_overview.detail.help" />"></i>
          </th>
          <th>
            <spring:message code="jsp.lmng_binding_overview.facets"/>
            <i class="inlinehelp icon-question-sign" data-title="<spring:message htmlEscape="true" code="jsp.lmng_binding_overview.facets" />" data-content="<spring:message htmlEscape="true" code="jsp.lmng_binding_overview.facets.help" />"></i>
          </th>
        </tr>
        </thead>
        <tbody>

        <c:set var="confirmationMessage" scope="request"><spring:message code="jsp.lmng_binding_overview.confirm" /></c:set>
        <c:set var="clearButtonTitle" scope="request"><spring:message code="jsp.lmng_binding_overview.clearbutton" /></c:set>
        <c:set var="submitButtonTitle" scope="request"><spring:message code="jsp.lmng_binding_overview.submitbutton" /></c:set>

        <c:forEach items="${bindings}" var="binding" varStatus="status">
          <c:if test="${not empty binding.serviceProvider.id}">
            <spring:url value="compoundSp-detail.shtml" var="detailUrl" htmlEscape="true">
              <spring:param name="spEntityId" value="${binding.serviceProvider.id}" />
            </spring:url>
            <spring:url value="service-taxonomy-configuration.shtml" var="facetsUrl" htmlEscape="true">
              <spring:param name="spEntityId" value="${binding.serviceProvider.id}" />
            </spring:url>
            <tr>
              <td title="${binding.serviceProvider.id} - ${fn:substring(binding.serviceProvider.descriptions[locale.language], 0, 40)}">
                <a id="row${status.index}" />
                <a href="${detailUrl}">
              		<tags:providername provider="${binding.serviceProvider}"/>
              	</a>
              </td>
              <td class="center">
                ${binding.serviceProvider.idpVisibleOnly == true ? "<i class='icon-ok'> </i>" : "<i class='icon-remove icon-greyed-out'> </i>"}
              </td>
              <td class="center">
                <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>"/>
                <c:set var="checked" value="${binding.compoundServiceProvider.hideInPublicShowroom}"></c:set>
                <input type="checkbox" name="hideInPublicShowroom" value="${checked}" data-compound-service-provider-id="${binding.compoundServiceProvider.id}" ${checked ? 'checked' : ''}>
              </td>
              <td class="center">
                <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>"/>
                <c:set var="checked" value="${binding.compoundServiceProvider.hideInProtectedShowroom}"></c:set>
                <input type="checkbox" name="hideInProtectedShowroom" value="${checked}" data-compound-service-provider-id="${binding.compoundServiceProvider.id}" ${checked ? 'checked' : ''}>
              </td>
              <td>
  			  <form:form id="form-${status.index}" method="post" action="save-splmng.shtml#row${status.index}" style="margin:0" cssClass="lmng-id-edit">
                <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>"/>
                <input type="hidden" name="index" value="${status.index}"/>
              	<input type="hidden" id="spId-${status.index}" value="${binding.serviceProvider.id}" name="spIdentifier"/>
              	<input id="lmngId-${status.index}" value="${binding.lmngIdentifier}" class="lmngIdentifier" type="text" name="lmngIdentifier"/>
                <div class="btn-group">
                	<button class="btn btn-small" id="clear-${status.index}" type="submit" value="clear" title="${clearButtonTitle}" name="clearbutton" onclick="return confirm('${confirmationMessage}');">
                    <i class="icon-ban-circle"></i>
                  </button>
                	<button class="btn btn-small" id="submit-${status.index}" type="submit" value="submit" title="${submitButtonTitle}" name="submitbutton">
                    <i class="icon-ok"></i>
                  </button>
                </div>
                <c:if test="${(status.index eq messageIndex) && (not empty errorMessage)}"><p class="error"><spring:message code="${errorMessage}" /></p></c:if>
                <c:if test="${(status.index eq messageIndex) && (not empty infoMessage)}"><p class="info"><spring:message code="jsp.lmng_binding_overview.new.sp.guid" /><c:out value="${infoMessage}" /></p></c:if>
  			  </form:form>
              </td>
              <td>
                <a href="${detailUrl}"><spring:message code="jsp.lmng_binding_overview.data_decision" /></a>
              </td>
              <td>
                <a href="${facetsUrl}"><spring:message code="jsp.lmng_binding_overview.facets" /></a>
              </td>
            </tr>
          </c:if>
        </c:forEach>

        </tbody>
      </table>

    </div>
  </section>

</div>

<jsp:include page="../foot.jsp"/>