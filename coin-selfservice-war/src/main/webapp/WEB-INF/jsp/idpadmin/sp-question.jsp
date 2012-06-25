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

<%--@elvariable id="sp" type="nl.surfnet.coin.selfservice.domain.ServiceProvider"--%>

<c:set var="spname"><tags:providername provider="${sp}"/></c:set>

<jsp:include page="../header.jsp">
  <jsp:param name="title" value="${spname}"/>
</jsp:include>

<sec:authentication property="principal" scope="request" htmlEscape="true" var="principal"/>

<div class="row">
  <div class="span8">
    <section>

      <h2><spring:message code="jsp.sp_question.pagetitle" arguments="${spname}"/></h2>

      <div class="content">

        <c:if test="${not empty jiraError}">
          <div class="alert alert-error">
            <spring:message code="jsp.sp_question.jiraError" arguments="${jiraError}"/>
          </div>
        </c:if>

        <form:form cssClass="form form-horizontal" commandName="question">
          <fieldset>

            <div class="control-group">
              <label class="control-label"><spring:message code="jsp.sp_question.applicantname"/></label>

              <div class="controls">
                <output>${principal.displayName}</output>
              </div>
            </div>

            <div class="control-group">
              <label class="control-label"><spring:message code="jsp.sp_question.idp"/></label>

              <div class="controls">
                <output><tags:providername provider="${selectedidp}"/></output>
              </div>
            </div>

            <div class="control-group <form:errors path="subject">error</form:errors>">
              <label class="control-label"><spring:message code="jsp.sp_question.subjectfield"/></label>

              <div class="controls">
                <form:input path="subject" cssClass="input-xlarge"/>
                <form:errors path="subject">
                  <p class="help-block"><form:errors path="subject"/></p>
                </form:errors>
              </div>
            </div>
            <div class="control-group <form:errors path="body">error</form:errors>">
              <label class="control-label"><spring:message code="jsp.sp_question.bodyfield"/></label>

              <div class="controls">
                <form:textarea path="body" cssClass="input-xlarge" rows="10"/>
                <form:errors path="body">
                  <p class="help-block"><form:errors path="body"/></p>
                </form:errors>
              </div>
            </div>

            <div class="actions">
              <button type="submit" class="btn btn-primary"><spring:message
                  code="jsp.sp_question.buttonsubmit"/></button>
              <a href="<c:url value="/idpadmin/sp/detail.shtml">
                <c:param name="spEntityId" value="${sp.id}" />
              </c:url>"><spring:message code="jsp.sp_question.buttoncancel"/></a>
            </div>

          </fieldset>

        </form:form>

      </div>

    </section>
  </div>

  <div class="span4">

    <section>
      <h2><spring:message code="jsp.sp_question.helptitle"/></h2>

      <div class="content">
        <p><spring:message code="jsp.sp_question.helpparagraph"/></p>

      </div>
    </section>

  </div>
</div>

<jsp:include page="../footer.jsp"/>