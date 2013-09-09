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

        <c:set var="sp" value="${sp}" scope="request" />

        <p><spring:message code="jsp.sp_unlinkrequest.intro" arguments="${service.name}"/></p>

        <form:form cssClass="form form-horizontal" commandName="unlinkrequest">
          <fieldset>
            <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>"/>
            <input type="hidden" name="serviceProviderId" value="<c:out value='${service.spEntityId}'/>"/>
            <input type="hidden" name="serviceName" value="<c:out value='${service.name}'/>"/>
            <input type="hidden" name="serviceId" value="<c:out value='${service.id}'/>"/>

            <div class="control-group <form:errors path="agree">error</form:errors>">
               <form:label path="agree" cssClass="checkbox">
                 <form:checkbox path="agree" id="agree" cssClass="required"/>
                 <spring:message code="jsp.sp_unlinkrequest.agreefield" arguments="${service.name}"/></form:label>
                <form:errors path="agree">
                  <p class="help-block"><form:errors path="agree"/></p>
                </form:errors>
            </div>

            <div class="control-group <form:errors path="notes">error</form:errors>">
              <form:label path="notes" cssClass="control-label"><spring:message
                  code="jsp.sp_unlinkrequest.notesfield"/></form:label>
              <div class="controls">
                <form:textarea path="notes" cssClass="input-xlarge" rows="3"/>
                <form:errors path="notes">
                  <p class="help-block"><form:errors path="notes"/></p>
                </form:errors>
              </div>
            </div>

            <%-- errors not specifically displayed otherwise are displayed here. For example, the super-user trying to submit this form --%>
            <form:errors  cssStyle="color: #cc0000" path="" />

            <div class="actions">
              <button type="submit" class="btn btn-primary btn-small">
                <spring:message code="jsp.sp_unlinkrequest.buttonsubmit"/>
              </button>
              <spring:url value="../app-detail.shtml" var="detailUrl" htmlEscape="true">
                <spring:param name="serviceId" value="${service.id}" />
              </spring:url>
              <a class="btn btn-small" href="${detailUrl}"><spring:message code="jsp.sp_unlinkrequest.buttoncancel"/></a>
            </div>

          </fieldset>

        </form:form>

      </div>

    </section>
  </div>
</div>

<jsp:include page="../foot.jsp"/>