<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ include file="../include.jsp" %>
<%--
  ~ Copyright 2012 SURFnet bv, The Netherlands
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<%--@elvariable id="sp" type="nl.surfnet.coin.selfservice.domain.ServiceProvider"--%>

<jsp:include page="../head.jsp">
  <jsp:param name="title" value="${service.name}"/>
</jsp:include>

<sec:authentication property="principal" scope="request" htmlEscape="true" var="principal"/>

  <div class="column-center content-holder no-right-left">
    <section>

      <h1><spring:message code="jsp.sp_unlinkrequest.pagetitle" arguments="${service.name}"/></h1>

      <div class="content">

        <c:if test="${not empty jiraError}">
          <div class="alert alert-error">
            <spring:message code="jsp.sp_unlinkrequest.jiraError" arguments="${jiraError}"/>
          </div>
        </c:if>

        <form:form cssClass="form form-horizontal" commandName="unlinkrequest">
          <fieldset>
            <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>"/>
            <input type="hidden" name="serviceProviderId" value="<c:out value='${unLinkRequest.serviceProviderId}'/>"/>
            <input type="hidden" name="serviceName" value="<c:out value='${unLinkRequest.serviceName}'/>"/>
            <input type="hidden" name="serviceId" value="<c:out value='${unLinkRequest.serviceId}'/>"/>
            <input type="hidden" name="confirmation" value="true" />
            <input type="hidden" name="notes" value="<c:out value='${unLinkRequest.notes}'/>"/>
            <p>
              <i class="icon-info-sign"></i>
              <spring:message code="jsp.sp_unlinkrequestconfirm.message" />
            </p>
            <div class="actions">
              <button type="submit" class="btn btn-primary btn-small"><spring:message
                  code="jsp.sp_unlinkrequestconfirm.buttonsubmit"/></button>
              <spring:url value="../app-detail.shtml" var="detailUrl" htmlEscape="true">
                <spring:param name="id" value="${service.id}" />
              </spring:url>
              <a class="btn btn-small" href="${detailUrl}"><spring:message code="jsp.sp_unlinkrequestconfirm.buttoncancel"/></a>
            </div>

          </fieldset>

        </form:form>

      </div>

    </section>
  </div>

<jsp:include page="../foot.jsp"/>