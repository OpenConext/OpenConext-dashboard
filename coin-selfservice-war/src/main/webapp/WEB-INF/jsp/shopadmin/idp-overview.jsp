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

<%--@elvariable id="sps" type="java.util.List<csa.domain.ServiceProvider>"--%>
<spring:message var="title" code="jsp.allidplmng.title"/>

    <jsp:include page="../head.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>

<div class="column-center content-holder no-right-left">

  <section class="data-table-holder">

  <a class="more-down-less-up" href="#"><h1>${title}
    <i class="icon-arrow-down in-active"></i>
    <i class="icon-arrow-up active"></i>
    <i class="inlinehelp icon-question-sign" data-placement="bottom" data-title="${title}" data-content="<spring:message htmlEscape="true" code="jsp.allidplmng.title.help" />"></i>
  </h1></a>
  <div class="data-table-wrapper">

      <c:set var="searchPlaceholder"><spring:message code="jsp.search.placeholder.idp" /></c:set>
      <table id="idp_overview_table" class="table table-bordered table-striped table-above-pagination table-sortable"
              data-search-placeholder="${searchPlaceholder}">
        <thead>
        <tr>
          <th>
            <spring:message code="jsp.lmng_binding_overview.name"/>
            <i class="inlinehelp icon-question-sign" data-title="<spring:message htmlEscape="true" code="jsp.lmng_binding_overview.name" />" data-content="<spring:message htmlEscape="true" code="jsp.allidplmng.name.help" />"></i>
          </th>
          <th>
            <spring:message code="jsp.lmng_binding_overview.lmngid"/>
            <i class="inlinehelp icon-question-sign" data-title="<spring:message htmlEscape="true" code="jsp.lmng_binding_overview.lmngid" />" data-content="<spring:message htmlEscape="true" code="jsp.allidplmng.lmngid.help" />"></i>
          </th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${bindings}" var="binding" varStatus="status">
          <c:if test="${not empty binding.identityProvider.institutionId}">
            <tr>
              <td title="${binding.identityProvider.id} - ${binding.identityProvider.institutionId} - ${fn:substring(binding.identityProvider.descriptions[locale.language], 0, 40)}">
                <a id="row${status.index}"></a>
                <tags:providername provider="${binding.identityProvider}"/>
              </td>
              <td class="text-overflow">
              <form:form id="form-lmng-id-edit-${status.index}" method="post" action="save-idplmng.shtml#row${status.index}" class="lmng-id-edit">
                <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>"/>
                <input type="hidden" name="index" value="${status.index}"/>
                <input type="hidden" id="idpId-${status.index}" value="${binding.identityProvider.institutionId}" name="idpIdentifier"/>
                <input id="lmngId-${status.index}" value="${binding.lmngIdentifier}" class="lmngIdentifier" type="text" size="40" name="lmngIdentifier"/>
                <c:set var="confirmationMessage" scope="request"><spring:message code="jsp.lmng_binding_overview.confirm" /></c:set>
                <c:set var="clearButtonTitle" scope="request"><spring:message code="jsp.lmng_binding_overview.clearbutton" /></c:set>
                <c:set var="submitButtonTitle" scope="request"><spring:message code="jsp.lmng_binding_overview.submitbutton" /></c:set>
                <div class="btn-group">
                  <button id="clear-${status.index}" type="submit" value="clear" title="${clearButtonTitle}" name="clearbutton" onclick="return confirm('${confirmationMessage}');" class="btn btn-small">
                    <i class="icon-ban-circle"></i>
                  </button>
                  <button id="submit-${status.index}" type="submit" value="submit" title="${submitButtonTitle}" name="submitbutton" class="btn btn-small">
                    <i class="icon-ok"></i>
                  </button>
                </div>
                <c:if test="${(status.index eq messageIndex) && (not empty errorMessage)}"><p class="error"><spring:message code="${errorMessage}" /></p></c:if>
                <c:if test="${(status.index eq messageIndex) && (not empty infoMessage)}"><p class="info"><spring:message code="jsp.lmng_binding_overview.new.idp.guid" /><c:out value="${infoMessage}" /></p></c:if>
          </form:form>
              </td>
            </tr>
          </c:if>
        </c:forEach>

        </tbody>
      </table>

    </div>
  </section>

<section class="data-table-holder">

    <spring:message var="title" code="jsp.allidplmng.accounts"/>
    <a class="more-down-less-up" href="#"><h1>
      ${title}
        <i class="icon-arrow-down in-active"></i>
        <i class="icon-arrow-up active"></i>
        <i class="inlinehelp icon-question-sign" data-title="${title}" data-content="<spring:message htmlEscape="true" code="jsp.allidlmng.accounts.title.help" />"></i>
    </h1>
    </a>
    <div class="data-table-wrapper">

    <c:set var="searchPlaceholderAccount"><spring:message code="jsp.search.placeholder.account" /></c:set>
    <table id="institutions_overview_table" class="table table-bordered table-striped table-above-pagination table-sortable"
            data-search-placeholder="${searchPlaceholder}">
      <thead>
      <tr>
        <th>
          <spring:message code="jsp.lmng_binding_overview.name"/>
          <i class="inlinehelp icon-question-sign" data-title="<spring:message htmlEscape="true" code="jsp.lmng_binding_overview.name" />" data-content="<spring:message htmlEscape="true" code="jsp.allidplmng.accounts.name.help" />"></i>
        </th>
        <th>
          <spring:message code="jsp.lmng_binding_overview.lmngid"/>
          <i class="inlinehelp icon-question-sign" data-title="<spring:message htmlEscape="true" code="jsp.lmng_binding_overview.lmngid" />" data-content="<spring:message htmlEscape="true" code="jsp.allidplmng.accounts.lmngid.help" />"></i>
        </th>
      </tr>
      </thead>
      <tbody>
      <c:forEach items="${accounts}" var="account" varStatus="status">
          <tr>
            <td>${account.name}</td>
            <td>${account.guid}</td>
          </tr>
      </c:forEach>

      </tbody>
    </table>

  </div>
</section>
</div>

<jsp:include page="../foot.jsp"/>
