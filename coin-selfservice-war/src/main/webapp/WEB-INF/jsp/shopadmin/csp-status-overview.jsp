<%@ include file="../include.jsp" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
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
<spring:message var="title" code="jsp.home.title"/>
<jsp:include page="../head.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>

    <div class="column-center content-holder">
        <section class="data-table-holder">
          <h1><spring:message code="jsp.cspstatus.title"/></h1>
          <div class="data-table-wrapper">
  
            <div>
              <form:form id="selectIdpForm" method="post" action="selectIdp.shtml" class="selectIdpForm">
                <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>"/>
                                
	              <select id="filteredIdpId" name="filteredIdpId" onChange="document.getElementById('selectIdpForm').submit();" class="select2-narrow">
	                <c:forEach items="${allIdps}" var="idp">
	                  <c:choose>
	                    <c:when test="${idp.id == filteredIdp}">
                        <option selected="selected" value="${idp.id}"><tags:providername provider="${idp}" />
	                    </c:when>
	                    <c:otherwise>
	                      <option value="${idp.id}"><tags:providername provider="${idp}" />
	                    </c:otherwise>
	                  </c:choose>
	                  
	                  </option>
	                </c:forEach>
	              </select>
              </form:form>
            </div>
  
            <table class="table table-bordered table-striped table-above-pagination table-with-statuses table-sortable" id="csp-statusses">
              <thead>
                <tr>
                  <th class="html sorting_asc"><spring:message code="jsp.cspstatus.csp.name"/></th>
                  <th><spring:message code="jsp.cspstatus.csp.lmnglink"/></th>
                  <th><spring:message code="jsp.cspstatus.csp.haslicense"/></th>
                  <th><spring:message code="jsp.cspstatus.csp.grouplicense"/></th>
                  <th><spring:message code="jsp.cspstatus.csp.license.expire"/></th>
                  <th><spring:message code="jsp.cspstatus.csp.islinked"/></th>
                </tr>
              </thead>
              <tbody>
                <c:forEach items="${compoundSps}" var="compoundSp">
			            <spring:url value="/app-detail.shtml" var="detailUrl" htmlEscape="true">
			              <spring:param name="compoundSpId" value="${compoundSp.id}" />
			            </spring:url>
			            
			            <c:set var="serviceDescription"><tags:providername provider="${compoundSp.sp}" /></c:set>
			            
                  <tr>
                    <td title="${serviceDescription} - ${compoundSp.sp.id}">
                      <a href="${detailUrl}"><tags:providername provider="${compoundSp.sp}" /></a>
                    </td>
                    <td>
                      <c:choose>
                        <c:when test="${compoundSp.articleAvailable}">
                          <i class="icon-ok"/>
                        </c:when>
                        <c:otherwise>
                          <i class="icon-remove icon-greyed-out"/>
                        </c:otherwise>
                      </c:choose>
                    </td>
                    <td>
                      <c:choose>
                        <c:when test="${compoundSp.licenseAvailable}">
                          <i class="icon-ok"/>
                        </c:when>
                        <c:otherwise>
                          <i class="icon-remove icon-greyed-out"/>
                        </c:otherwise>
                      </c:choose>
                    </td>
                    <td>
                      <c:if test="${not empty compoundSp.license}">
	                      <c:choose>
	                        <c:when test="${compoundSp.license.groupLicense}">
	                          <i class="icon-ok"/>
	                        </c:when>
	                        <c:otherwise>
	                          <i class="icon-remove icon-greyed-out"/>
	                        </c:otherwise>
	                      </c:choose>
	                    </c:if>
                    </td>
                    <td>
                      <c:if test="${not empty compoundSp.license}">
                        <fmt:formatDate pattern="dd-MM-yyyy" value="${compoundSp.license.endDate}"/>
                      </c:if>
                    </td>
                    <td>
                      <c:choose>
                        <c:when test="${not compoundSp.sp.linked}">
                          <i class="icon-ok"/>
                        </c:when>
                        <c:otherwise>
                          <i class="icon-remove icon-greyed-out"/>
                        </c:otherwise>
                      </c:choose>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </section>
      </div>

<jsp:include page="../foot.jsp"/>
<script>
  $('#filteredIdpId').select2();
</script>