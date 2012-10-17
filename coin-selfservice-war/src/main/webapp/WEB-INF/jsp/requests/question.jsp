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

<jsp:include page="../head.jsp">
  <jsp:param name="title" value="${spname}"/>
</jsp:include>

  <div class="column-center content-holder">

    <section>

      <h1><spring:message code="jsp.sp_question.pagetitle" arguments="${spname}"/></h1>

      <div class="content">

        
        <form:form cssClass="form form-horizontal" id="question" commandName="question">
          <fieldset>

            <div class="control-group">
              <label class="control-label"><spring:message code="jsp.sp_question.applicantname"/></label>

              <div class="controls">
                <output><sec:authentication property="principal.displayName" scope="request"
              htmlEscape="true" /></output>
              </div>
            </div>

            <div class="control-group">
              <label class="control-label"><spring:message code="jsp.sp_question.idp"/></label>

              <div class="controls">
                <output><tags:providername provider="${selectedidp}"/></output>
              </div>
            </div>

            <div class="control-group ">
              <label class="control-label">Subject:</label>

              <div class="controls">
                <input id="subject" name="subject" class="input-xlarge" type="text" value=""/>
                
              </div>
            </div>
            <div class="control-group ">
              <label class="control-label">Question:</label>

              <div class="controls">
                <textarea id="body" name="body" class="input-xlarge" rows="10"></textarea>
                
              </div>
            </div>

            <div class="actions">
              <button type="submit" class="btn btn-primary">Send</button>
              <a class="btn" href="<c:url value="/idpadmin/sp/detail.shtml">
                <c:param name="spEntityId" value="${sp.id}" />
              </c:url>"><spring:message code="jsp.sp_question.buttoncancel"/></a>
            </div>

          </fieldset>

        </form:form>

      </div>

    </section>
            </div>




<jsp:include page="../foot.jsp"/>