<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
</jsp:include>

<section>

  <h2>${sp.name}</h2>

  <div class="row">
  <div class="span2">
    <div class="content">
      <p>
        <img alt="${sp.name}" style="float:left" src="${sp.logoUrl}"/>
      </p>
    </div>
  </div>
  <div class="span6">
    <div class="content">
      <p>
        ${sp.description}
      </p>
    </div>
  </div>
    <div class="span4">
      <div class="content">
        <ul class="unstyled">
          <li>
            <a href="${sp.homeUrl}">More information</a> <i class="icon-external-link"></i>
          </li>
          <li>
            <a href="${sp.homeUrl}">Website</a> <i class="icon-external-link"></i>
          </li>
          <c:forEach items="${sp.contactPersons}" var="cp">
            <li>
              <ul class="unstyled">
                <li>${cp.name}</li>
                <c:if test="${not empty cp.contactPersonType}">
                  <li><em>(${cp.contactPersonType})</em></li>
                </c:if>
                <c:if test="${not empty cp.emailAddress}">
                  <li><a href="mailto:${cp.emailAddress}">${cp.emailAddress}</a> <i class="icon-external-link"></i></li>
                </c:if>
                <c:if test="${not empty cp.telephoneNumber}">
                  <li>${cp.telephoneNumber}</li>
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