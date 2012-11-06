<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="input" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../include.jsp" %>
<%--
  Copyright 2012 SURFnet bv, The Netherlands

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not <spring:message code="jsp.compound_sp_select_source"/> file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  --%>

<%--@elvariable id="compoundSp" type="nl.surfnet.coin.selfservice.domain.CompoundServiceProvider"--%>


<c:set var="title">
  <tags:providername provider="${compoundSp.sp}"/>
</c:set>

<jsp:include page="../head.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>

<div class="column-center content-holder">
<section>

  <h1>${title}
    <spring:url value="/app-detail.shtml" var="detailUrl" htmlEscape="true">
      <spring:param name="compoundSpId" value="${compoundSp.id}" />
      <spring:param name="refreshCache" value="true"/>
    </spring:url>
    <a href="${detailUrl}">
      <i class="icon-eye-open" rel="tooltip" title="<spring:message code="jsp.compoundsp_detail.previewlink" />"></i>
    </a>
  </h1>

  <div class="content">
      <div class="accordion" id="fieldaccordion">
<c:forEach items="${compoundSp.fields}" var="field">
  <spring:message var="fieldTitle" code="jsp.compoundSp.${field.key}" />
  <c:set var="fieldId" value="f-${field.id}" />

      <div class="accordion-group ${field.unset == true ? 'error' : ''}">
        <div class="accordion-heading">
          <a class="accordion-toggle" data-toggle="collapse" data-parent="#fieldaccordion" href="#${fieldId}-body">
            ${fieldTitle}
          </a>
        </div>
        <div id="${fieldId}-body" class="accordion-body collapse">
          <div class="accordion-inner">
            <ul class="nav nav-tabs">
              <li ${field.source=='LMNG' ? 'class="active source-selected"' : ''}><a data-toggle="tab" class="sourceTab" href="#form${fieldId}-lmng"><spring:message code="jsp.compound_sp_surfmarket"/></a></li>
              <li ${field.source=='SURFCONEXT' ? 'class="active source-selected"' : ''}><a data-toggle="tab" class="sourceTab" href="#form${fieldId}-surfconext"><spring:message code="jsp.compound_sp_surfconext"/></a></li>
              <li ${field.source=='DISTRIBUTIONCHANNEL' ? 'class="active source-selected"' : ''}><a data-toggle="tab" class="sourceTab" href="#form${fieldId}-distributionchannel"><spring:message code="jsp.compound_sp_distributionchannel"/></a></li>
            </ul>
            <div class="tab-content">

            <form class="tab-pane ${field.source=='LMNG' ? 'active' : ''}" id="form${fieldId}-lmng">
              <p>${compoundSp.lmngFieldValues[field.key]}</p>
              <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>"/>
              <input type="hidden" name="source" value="LMNG" />
              <input type="hidden" name="fieldId" value="${field.id}" />
              <button name="usethis" value="usethis" class="btn btn-primary btn-small"><spring:message code="jsp.compound_sp_select_source"/></button>
            </form>
            <form class="tab-pane ${field.source=='SURFCONEXT' ? 'active' : ''}" id="form${fieldId}-surfconext">
              <p>${compoundSp.surfConextFieldValues[field.key]}</p>
              <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>"/>
              <input type="hidden" name="source" value="SURFCONEXT" />
              <input type="hidden" name="fieldId" value="${field.id}" />
              <button name="usethis" value="usethis" class="btn btn-primary btn-small"><spring:message code="jsp.compound_sp_select_source"/></button>
            </form>
            <form class="tab-pane ${field.source=='DISTRIBUTIONCHANNEL' ? 'active' : ''}" id="form${fieldId}-distributionchannel">
              <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>"/>
              <input type="hidden" name="source" value="DISTRIBUTIONCHANNEL" />
              <input type="hidden" name="fieldId" value="${field.id}" />
              <textarea name="value">${compoundSp.distributionFieldValues[field.key]}</textarea>
              <div class="form-actions">
                <button name="usethis" value="usethis" class="btn btn-small"><spring:message code="jsp.compound_sp_select_source"/></button>
                <button name="save" value="save" class="btn btn-primary btn-small"><spring:message code="jsp.compound_sp_save"/></button>
              </div>
            </form>
            </div>
          </div>
        </div>
      </div>
</c:forEach>

<%-- Images --%>
<c:forEach items="${compoundSp.fieldImages}" var="field">
  <spring:message var="fieldTitle" code="jsp.compoundSp.${field.key}" />
  <c:set var="fieldId" value="fieldimage-${field.id}" />

  <div class="accordion-group">
    <div class="accordion-heading">
      <a class="accordion-toggle" data-toggle="collapse" data-parent="#fieldaccordion" href="#${fieldId}-body">
          ${fieldTitle}
      </a>
    </div>
    <div id="${fieldId}-body" class="accordion-body collapse">
      <div class="accordion-inner">
        <ul class="nav nav-tabs">
          <li ${field.source=='LMNG' ? 'class="active source-selected"' : ''}><a data-toggle="tab" class="sourceTab" href="#form${fieldId}-lmng"><spring:message code="jsp.compound_sp_surfmarket"/></a></li>
          <li ${field.source=='SURFCONEXT' ? 'class="active source-selected"' : ''}><a data-toggle="tab" class="sourceTab" href="#form${fieldId}-surfconext"><spring:message code="jsp.compound_sp_surfconext"/></a></li>
          <li ${field.source=='DISTRIBUTIONCHANNEL' ? 'class="active source-selected"' : ''}><a data-toggle="tab" class="sourceTab" href="#form${fieldId}-distributionchannel"><spring:message code="jsp.compound_sp_distributionchannel"/></a></li>
        </ul>
        <div class="tab-content">

          <form class="tab-pane ${field.source=='LMNG' ? 'active' : ''}" id="form${fieldId}-lmng">
            <c:if test="${!empty compoundSp.lmngFieldValues[field.key]}">
              <img src="<spring:url value="${compoundSp.lmngFieldValues[field.key]}" />"/>
            </c:if>
            <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>"/>
            <input type="hidden" name="source" value="LMNG" />
            <input type="hidden" name="fieldId" value="${field.id}" />
            <button name="usethis" value="usethis-image" class="btn btn-primary btn-small"><spring:message code="jsp.compound_sp_select_source"/></button>
          </form>
          <form class="tab-pane ${field.source=='SURFCONEXT' ? 'active' : ''}" id="form${fieldId}-surfconext">
            <c:if test="${!empty compoundSp.surfConextFieldValues[field.key]}">
              <img src="<spring:url value="${compoundSp.surfConextFieldValues[field.key]}" />"/>
            </c:if>
            <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>"/>
            <input type="hidden" name="source" value="SURFCONEXT" />
            <input type="hidden" name="fieldId" value="${field.id}" />
            <button name="usethis" value="usethis-image" class="btn btn-primary btn-small"><spring:message code="jsp.compound_sp_select_source"/></button>
          </form>

          <form class="tab-pane imageuploadform ${field.source=='DISTRIBUTIONCHANNEL' ? 'active' : ''}" id="form${fieldId}-distributionchannel"> 
            <c:if test="${!empty compoundSp.distributionFieldValues[field.key]}">
              <img src="<spring:url value="${compoundSp.distributionFieldValues[field.key]}" />"/>
            </c:if>

            <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>"/>
            <input type="hidden" name="source" value="DISTRIBUTIONCHANNEL" />
            <input type="hidden" name="fieldId" value="${field.id}" />
            <span id='filename'></span><br/>
            <a href='#' class='attachlink'><spring:message code="jsp.compound_sp_select_image"/></a><br/>
            <input class="fileinput" id="upload-${fieldId}" type="file" name="file" data-url="upload.shtml" style="opacity: 0; filter:alpha(opacity: 0);"><br/>
            <div class="form-actions">
              <button name="usethis" value="usethis-image" class="btn btn-small"><spring:message code="jsp.compound_sp_select_source"/></button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</c:forEach>
<%-- End Images --%>
<%-- Begin screenshots --%>

  <div class="accordion-group">
    <div class="accordion-heading">
      <a class="accordion-toggle" data-toggle="collapse" data-parent="#fieldaccordion" href="#screenshots-body">
          <spring:message code="jsp.compound_sp_screenshots"/>
      </a>
    </div>
    <div id="screenshots-body" class="accordion-body collapse">
      <div class="accordion-inner">
        <ul class="nav nav-tabs">
          <li class="source-selected"><a data-toggle="tab" class="sourceTab" href="#form-screenshots-distributionchannel"><spring:message code="jsp.compound_sp_distributionchannel"/></a></li>
        </ul>
        <div class="tab-content">
	      <div class="screenshot-contents">
	        <c:forEach items="${compoundSp.screenShotsImages}" var="screenShotImage">
	          <div class="screenshot-content">
				<img src="<spring:url value="${screenShotImage.fileUrl}" />"/>
	           	<a id="screenshot-remove-${screenShotImage.id}" href="#">&times;</a>
	          </div>	
	          </c:forEach>
	        </div>
          <form class="tab-pane active imageuploadform" id="form-screenshots-distributionchannel">
            <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>"/>
          	<input type="hidden" name="compoundServiceProviderId" value="${compoundSp.id}" />
          	<span id='filename'></span><br/>
          	<a href='#' class='attachlink btn btn-primary btn-small'><spring:message code="jsp.compound_sp_add_image"/></a><br/>
          	<input class="fileinput" id="upload-screenshot" type="file" name="file" data-url="upload-screenshot.shtml" style="opacity: 0; filter:alpha(opacity: 0);"><br/>
          </form>
        </div>
      </div>
    </div>
  </div>


</div>


  </div>
</section>

</div>
</div>

<jsp:include page="../foot.jsp">
  <jsp:param name="datatables" value="false"/>
</jsp:include>
