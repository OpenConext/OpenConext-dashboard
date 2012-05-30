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

<jsp:include page="header.jsp">
  <jsp:param name="activeSection" value="linked-sps"/>
  <jsp:param name="title" value="${sp.name}"/>
</jsp:include>

<section>

  <h2><c:out value="${sp.name}"/></h2>

  <div class="row">
    <c:if test="${not empty sp.logoUrl}">
      <div class="span2">
        <div class="content">
          <p>
            <img alt="" style="float:left" src="<c:out value="${sp.logoUrl}"/>"/>
          </p>
        </div>
      </div>
    </c:if>
    <%-- bit ugly: if this span does not exist, the right column is pushed to the left --%>
    <div class="span6">
      <div class="content">
        <c:if test="${not empty sp.description}">
          <p>
            <c:out value="${sp.description}"/>
          </p>
        </c:if>
      </div>
    </div>
    <div class="span4">
      <div class="content">
        <ul class="unstyled">
          <li>
            <a href="<c:out value="${sp.homeUrl}"/>"><spring:message code="jsp.sp_detail.moreinfo"/></a> <i class="icon-external-link"></i>
          </li>
          <li>
            <a href="<c:out value="${sp.homeUrl}"/>"><spring:message code="jsp.sp_detail.website"/></a> <i class="icon-external-link"></i>
          </li>
          <c:forEach items="${sp.contactPersons}" var="cp">
            <li>
              <ul class="unstyled">
                <li><c:out value="${cp.name}"/></li>
                <c:if test="${not empty cp.contactPersonType}">
                  <li><em>(<c:out value="${cp.contactPersonType}"/>)</em></li>
                </c:if>
                <c:if test="${not empty cp.emailAddress}">
                  <li><a href="mailto:<c:out value="${cp.emailAddress}"/>"><c:out value="${cp.emailAddress}"/></a> <i class="icon-external-link"></i></li>
                </c:if>
                <c:if test="${not empty cp.telephoneNumber}">
                  <li><c:out value="${cp.telephoneNumber}"/></li>
                </c:if>
              </ul>
            </li>
          </c:forEach>
        </ul>
      </div>
    </div>
  </div>
</section>


<jsp:include page="footer.jsp"/>