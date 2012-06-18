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

<c:choose>
  <c:when test="${empty sp.name}"><c:set var="spname" value="${sp.id}"/></c:when>
  <c:otherwise><c:set var="spname" value="${sp.name}"/></c:otherwise>
</c:choose>

<jsp:include page="../header.jsp">
  <jsp:param name="activeSection" value="linked-sps"/>
  <jsp:param name="title" value="${spname}"/>
</jsp:include>

<sec:authentication property="principal" scope="request" htmlEscape="true" var="principal"/>

<div class="row">
  <div class="span8">
    <section>

      <h2><spring:message code="jsp.sp_linkrequest.pagetitle" arguments="${sp.name}"/></h2>

      <div class="content">

        <c:if test="${not empty jiraError}">
          <div class="alert alert-error">
            <spring:message code="jsp.sp_linkrequest.jiraError" arguments="${jiraError}"/>
          </div>
        </c:if>

        <form:form cssClass="form form-horizontal" commandName="linkrequest">
          <fieldset>
            <input type="hidden" name="confirmed" value="true" />
            <div class="message">
              <spring:message code="jsp.sp_linkrequestconfirm.message" />
            </div>
            <div class="actions">
              <button type="submit" class="btn btn-primary"><spring:message
                  code="jsp.sp_linkrequestconfirm.buttonsubmit"/></button>
              <a href="<c:url value="/idpadmin/sp/detail.shtml">
                <c:param name="spEntityId" value="${sp.id}" />
              </c:url>"><spring:message code="jsp.sp_linkrequestconfirm.buttoncancel"/></a>
            </div>

          </fieldset>

        </form:form>

      </div>

    </section>
  </div>

  <div class="span4">

    <section>
      <h2><spring:message code="jsp.sp_linkrequest.helptitle"/></h2>

      <div class="content">
        <p><spring:message code="jsp.sp_linkrequest.helpparagraph"/></p>

      </div>
    </section>

  </div>
</div>

<jsp:include page="../footer.jsp"/>