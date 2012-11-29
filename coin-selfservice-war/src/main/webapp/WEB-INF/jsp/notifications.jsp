<%@ include file="include.jsp" %>
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
<jsp:include page="head.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>


      <div class="column-center content-holder">
        <section class="data-table-holder">

          <h1><spring:message code="jsp.notifications.title"/></h1>
          <div class="data-table-wrapper">
            <table class="table table-bordered table-striped table-above-pagination table-with-statuses table-sortable">
              <thead>
                <tr>
                  <th><spring:message code="jsp.notifications.new"/></th>
                  <th><spring:message code="jsp.notifications.message"/></th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                <c:forEach items="${notifications.messages}" var="notificationMessage">
                  <tr>
                    <td>
	                    <c:choose>
	                      <c:when test="${not notificationMessage.read}">
	                        <spring:message code="jsp.notifications.new"/>
	                        <!-- set message as read -->${notificationMessage.setMessageRead}
	                      </c:when>
	                      <c:otherwise>
                          <spring:message code="jsp.notifications.read"/>
	                      </c:otherwise>
	                    </c:choose>
                    </td>
                    <td>
                      <spring:message code="${notificationMessage.messageKey}" arguments="${notificationMessage.arguments}"/>
                    </td>
                    <td>
                      <c:if test="${not empty notificationMessage.correspondingServiceProvider and not empty notificationMessage.correspondingServiceProvider.appStoreLogo}">
                        <img src="<c:url value="${notificationMessage.correspondingServiceProvider.appStoreLogo}"/>"/>
                      </c:if>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </section>
      </div>

<jsp:include page="foot.jsp"/>