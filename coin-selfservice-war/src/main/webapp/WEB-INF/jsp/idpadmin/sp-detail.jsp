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

<c:choose>
  <c:when test="${empty sp.name}"><c:set var="spname" value="${sp.id}"/></c:when>
  <c:otherwise><c:set var="spname" value="${sp.name}"/></c:otherwise>
</c:choose>

<jsp:include page="../header.jsp">
  <jsp:param name="activeSection" value="linked-sps"/>
  <jsp:param name="title" value="${spname}"/>
</jsp:include>

<section>

  <h2><c:out value="${spname}"/></h2>

  <div class="row">
    <c:if test="${not empty sp.logoUrl}">
      <div class="span2">
        <div class="content">
          <p>
            <c:set var="logo"><img alt="" style="float:left" src="<c:out value="${sp.logoUrl}"/>"/>
            </c:set>
            <c:choose>
              <c:when test="${not empty sp.homeUrl}">
                <a href="<c:out value="${sp.homeUrl}"/>" target="_blank">${logo}</a>
              </c:when>
              <c:otherwise>${logo}</c:otherwise>
            </c:choose>
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
        <c:set var="sp" value="${sp}" scope="request" />
        <jsp:include page="../arp.jsp" />

        <div>
          <a class="btn btn-primary" href="<c:url value="/idpadmin/sp/question.shtml">
                <c:param name="spEntityId" value="${sp.id}" />
              </c:url>"
             title="<spring:message code="jsp.sp_detail.askquestion"/>"><spring:message code="jsp.sp_detail.askquestion"/>
          </a>
          <c:choose>
            <c:when test="${not sp.linked}">
              <a class="btn btn-primary" href="<c:url value="/idpadmin/sp/linkrequest.shtml">
                <c:param name="spEntityId" value="${sp.id}" />
              </c:url>"
                 title="<spring:message code="jsp.sp_detail.requestlink"/>"><spring:message code="jsp.sp_detail.requestlink"/>
              </a>
            </c:when>
            <c:when test="${sp.linked}">
              <a class="btn btn-primary" href="<c:url value="/idpadmin/sp/unlinkrequest.shtml">
                <c:param name="spEntityId" value="${sp.id}" />
              </c:url>"
                 title="<spring:message code="jsp.sp_detail.requestunlink"/>"><spring:message
                  code="jsp.sp_detail.requestunlink"/>
              </a>
            </c:when>
          </c:choose>

        </div>


      </div>
    </div>
    <div class="span4">
      <div class="content">
        <c:if test="${fn:length(sp.contactPersons) gt 0}">
          <h3><spring:message code="jsp.sp_detail.contact"/></h3>
          <c:forEach items="${sp.contactPersons}" var="cp">
            <ul class="unstyled">
              <li><c:out value="${cp.name}"/>
                <c:if test="${not empty cp.contactPersonType}">
                  <em>(<c:out value="${cp.contactPersonType}"/>)</em>
                </c:if>
              </li>
              <c:if test="${not empty cp.emailAddress}">
                <li><a href="mailto:<c:out value="${cp.emailAddress}"/>"><c:out value="${cp.emailAddress}"/></a> <i
                    class="icon-envelope"></i></li>
              </c:if>
              <c:if test="${not empty cp.telephoneNumber}">
                <li><c:out value="${cp.telephoneNumber}"/></li>
              </c:if>
            </ul>
          </c:forEach>
        </c:if>
        <h3><spring:message code="jsp.sp_detail.moreinfo"/></h3>
        <ul class="unstyled">
          <c:if test="${not empty sp.homeUrl}">
            <li>
              <a href="<c:out value="${sp.homeUrl}"/>" target="_blank"><spring:message
                  code="jsp.sp_detail.website"/></a> <i class="icon-external-link"></i>
            </li>
          </c:if>
          <c:if test="${not empty sp.eulaURL}">
            <li>
              <a href="<c:out value="${sp.eulaURL}"/>" target="_blank"><spring:message code="jsp.sp_detail.eula"/></a>
              <i class="icon-external-link"></i>
            </li>
          </c:if>
        </ul>
      </div>
    </div>
  </div>
  <div class="row">
    <div class="span12">
      <div class="content">
        <div id="chart"></div>
      </div>
    </div>
  </div>
</section>


<jsp:include page="../footer.jsp">
  <jsp:param name="chart" value="${sp.linked eq true}"/>
</jsp:include>
