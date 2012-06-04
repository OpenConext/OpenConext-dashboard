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

<h2><spring:message code="jsp.sp_question.pagetitle" /></h2>

<div class="content">

<form class="form form-horizontal" method="post" action="<c:url value="/sp/question.shtml">
                <c:param name="spEntityId" value="${sp.id}" />
              </c:url>">

<fieldset>


<div class="control-group">
  <label class="control-label"><spring:message code="jsp.sp_question.questionfield" /></label>

  <div class="controls">
    <textarea class="input-xlarge" rows="10"></textarea>
  </div>
</div>

<div class="actions">
  <button type="submit" class="btn btn-primary"><spring:message code="jsp.sp_question.buttonsubmit" /></button>
  <a href="#"><spring:message code="jsp.sp_question.buttoncancel" /></a>
</div>

</fieldset>

</form>

</div>

</section>
</div>

<div class="span4">

  <section>
    <h2><spring:message code="jsp.sp_question.helptitle" /></h2>

    <div class="content">
      <p><spring:message code="jsp.sp_question.helpparagraph" /></p>

    </div>
  </section>

</div>
</div>

<jsp:include page="footer.jsp"/>