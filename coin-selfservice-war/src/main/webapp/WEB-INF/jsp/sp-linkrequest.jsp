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

<%--@elvariable id="sp" type="nl.surfnet.coin.selfservice.domain.ServiceProvider"--%>

<c:choose>
  <c:when test="${empty sp.name}"><c:set var="spname" value="${sp.id}"/></c:when>
  <c:otherwise><c:set var="spname" value="${sp.name}"/></c:otherwise>
</c:choose>

<jsp:include page="header.jsp">
  <jsp:param name="activeSection" value="linked-sps"/>
  <jsp:param name="title" value="${spname}"/>
</jsp:include>


<div class="row">
  <div class="span8">
    <section>

      <h2><spring:message code="jsp.sp_linkrequest.pagetitle"/></h2>

      <div class="content">

        <form:form cssClass="form form-horizontal" commandName="linkrequest">
          <fieldset>


            <div class="control-group <form:errors path="emailAddress">error</form:errors>">
            <label class="control-label"><spring:message code="jsp.sp_linkrequest.emailaddressfield"/></label>

            <div class="controls">
              <form:input path="emailAddress" cssClass="input-xlarge"/>
              <form:errors path="emailAddress">
                <p class="help-block"><form:errors path="emailAddress"/></p>
              </form:errors>
            </div>
      </div>
      <div class="control-group <form:errors path="notes">error</form:errors>">
      <label class="control-label"><spring:message code="jsp.sp_linkrequest.notesfield"/></label>

      <div class="controls">
        <form:textarea path="notes" cssClass="input-xlarge" rows="10"/>
        <form:errors path="notes">
          <p class="help-block"><form:errors path="notes"/></p>
        </form:errors>
      </div>
  </div>

  <div class="actions">
    <button type="submit" class="btn btn-primary"><spring:message
        code="jsp.sp_linkrequest.buttonsubmit"/></button>
    <a href="#"><spring:message code="jsp.sp_linkrequest.buttoncancel"/></a>
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

<jsp:include page="footer.jsp"/>