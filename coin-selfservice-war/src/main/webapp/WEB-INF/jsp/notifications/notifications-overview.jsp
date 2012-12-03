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

          <h1><spring:message code="jsp.notifications.title"/></h1>
          <div class="data-table-wrapper">
          <ul>
                <c:forEach items="${notifications.messages}" var="notificationMessage">
                  <li>
                      <c:choose>
                        <c:when test="${not notificationMessage.read}">
                          <!-- set message as read -->${notificationMessage.setMessageRead}
                          <div class="unread"><spring:message code="${notificationMessage.messageKey}"/></div>
                        </c:when>
                        <c:otherwise>
                          <div class="read"><spring:message code="${notificationMessage.messageKey}"/></div>
                        </c:otherwise>
                      </c:choose>
                      <ul>
	                      <c:forEach items="${notificationMessage.arguments}" var="cspArgument">
                           <li>
                             <c:choose>
                              <c:when test="${empty cspArgument.serviceDescriptionNl}">
		                            ${cspArgument.serviceProviderEntityId}
  		                        </c:when>
	                            <c:otherwise>
                                ${cspArgument.serviceDescriptionNl}
	                            </c:otherwise>
	                          </c:choose>
                          </li>
	                      </c:forEach>
                      </ul>
                  </li>
                </c:forEach>
          </ul>
          </div>
        </section>
      </div>

<jsp:include page="../foot.jsp"/>