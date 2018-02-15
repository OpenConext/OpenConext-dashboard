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

<%--@elvariable id="compoundSp" type="csa.domain.CompoundServiceProvider"--%>


<jsp:include page="../head.jsp">
  <jsp:param name="title" value="${compoundSp.titleEn}"/>
</jsp:include>

<div class="column-center content-holder no-right-left">

<section>

  <h1>${compoundSp.titleEn}
    <spring:url value="/app-detail.shtml" var="detailUrl" htmlEscape="true">
      <spring:param name="serviceProviderEntityId" value="${compoundSp.serviceProviderEntityId}" />
    </spring:url>
    <i class="inlinehelp icon-question-sign" data-title="${title}" data-placement="bottom" data-content="<spring:message htmlEscape="true" code="jsp.compoundsp_detail.title.help" />"></i>
  </h1>

  <div class="content">
      <div class="accordion" id="fieldaccordion">
<c:forEach items="${compoundSp.fields}" var="field">
  <spring:message var="fieldTitle" code="jsp.compoundSp.${field.key}" />
  <c:set var="fieldId" value="f-${field.id}" />

      <div class="accordion-group ${field.unset == true ? 'error' : ''}">
        <div class="accordion-heading">
        <c:set var="source-description" value="${field.source}"/>
          <a class="accordion-toggle" data-toggle="collapse" data-parent="#fieldaccordion" href="#${fieldId}-body">
            ${fieldTitle} <span><spring:message code="${field.source}"/></span>
          </a>
        </div>
        <div id="${fieldId}-body" class="accordion-body collapse">
          <div class="accordion-inner">
            <ul class="nav nav-tabs">
              <c:if test="${field.availableInSurfConext}">
                <li ${field.source=='SURFCONEXT' ? 'class="active source-selected"' : ''}><a data-toggle="tab" class="sourceTab" href="#form${fieldId}-surfconext"><spring:message code="jsp.compound_sp_surfconext"/></a></li>
              </c:if>              
              <li ${field.source=='DISTRIBUTIONCHANNEL' ? 'class="active source-selected"' : ''}><a data-toggle="tab" class="sourceTab" href="#form${fieldId}-distributionchannel"><spring:message code="jsp.compound_sp_distributionchannel"/></a></li>
            </ul>
            <div class="tab-content">
            <c:if test="${field.availableInSurfConext}">
              <form class="tab-pane ${field.source=='SURFCONEXT' ? 'active' : ''}" id="form${fieldId}-surfconext">
                <p>${compoundSp.surfConextFieldValues[field.key]}</p>
                <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>"/>
                <input type="hidden" name="source" value="SURFCONEXT" />
                <input type="hidden" name="fieldId" value="${field.id}" />
                <button name="usethis" value="usethis" class="btn btn-primary btn-small"><spring:message code="jsp.compound_sp_select_source"/></button>
                <p class="pull-right">${field.technicalOriginSurfConext}</p>
              </form>
            </c:if>
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
          ${fieldTitle} <span><spring:message code="${field.source}"/></span>
      </a>
    </div>
    <div id="${fieldId}-body" class="accordion-body collapse">
      <div class="accordion-inner">
        <ul class="nav nav-tabs">
          <c:if test="${field.availableInSurfConext}">
            <li ${field.source=='SURFCONEXT' ? 'class="active source-selected"' : ''}><a data-toggle="tab" class="sourceTab" href="#form${fieldId}-surfconext"><spring:message code="jsp.compound_sp_surfconext"/></a></li>
          </c:if>
          <li ${field.source=='DISTRIBUTIONCHANNEL' ? 'class="active source-selected"' : ''}><a data-toggle="tab" class="sourceTab" href="#form${fieldId}-distributionchannel"><spring:message code="jsp.compound_sp_distributionchannel"/></a></li>
        </ul>
        <div class="tab-content">
          <c:if test="${field.availableInSurfConext}">
            <form class="tab-pane ${field.source=='SURFCONEXT' ? 'active' : ''}" id="form${fieldId}-surfconext">
              <c:if test="${!empty compoundSp.surfConextFieldValues[field.key]}">
                <img src="<spring:url value="${compoundSp.surfConextFieldValues[field.key]}" />"/>
              </c:if>
              <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>"/>
              <input type="hidden" name="source" value="SURFCONEXT" />
              <input type="hidden" name="fieldId" value="${field.id}" />
              <button name="usethis" value="usethis-image" class="btn btn-primary btn-small"><spring:message code="jsp.compound_sp_select_source"/></button>
              <p class="pull-right">${field.technicalOriginSurfConext}</p>
            </form>
          </c:if>
          <form class="tab-pane imageuploadform ${field.source=='DISTRIBUTIONCHANNEL' ? 'active' : ''}" id="form${fieldId}-distributionchannel"> 
            <c:if test="${!empty compoundSp.distributionFieldValues[field.key]}">
              <img src="<spring:url value="${compoundSp.distributionFieldValues[field.key]}" />"/>
            </c:if>

            <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>"/>
            <input type="hidden" name="source" value="DISTRIBUTIONCHANNEL" />
            <input type="hidden" name="fieldId" value="${field.id}" />

            <div class="form-actions">
              <span class="btn btn-small fileinput-button btn-primary">
                <span><spring:message code="jsp.compound_sp_select_image"/></span>
                <input class="fileinput" id="upload-${fieldId}" type="file" name="file" data-url="upload.shtml">
              </span>
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
          <spring:message code="jsp.compound_sp_screenshots"/><span><spring:message code="jsp.compound_sp_distributionchannel"/></span>
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

            <span class="btn btn-small fileinput-button btn-primary">
              <span><spring:message code="jsp.compound_sp_add_image"/></span>
              <input class="fileinput" id="upload-screenshot" type="file" name="file" data-url="upload-screenshot.shtml">
            </span>

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

<jsp:include page="../foot.jsp"/>
