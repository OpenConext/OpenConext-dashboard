<%@ include file="/WEB-INF/jsp/include.jsp" %>
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

<%@attribute name="attributeKey" rtexprvalue="true" required="true"
             description="Key of the attribute, e.g. urn:mace:dir:attribute-def:displayName" %>
<%--@elvariable id="personAttributeLabels" type="java.util.Map<nl.surfnet.coin.selfservice.domain.PersonAttributeLabel>"--%>
<c:set var="labels" value="${personAttributeLabels[attributeKey]}"/>
<c:choose>

  <c:when test="${empty labels}">
    <c:out value="${attributeKey}"/>
  </c:when>

  <c:otherwise>
    <%--@elvariable id="locale" type="java.util.Locale"--%>
    <c:set var="name" value="${labels.names[locale.language]}"/>
    <c:set var="description" value="${labels.descriptions[locale.language]}"/>

    <c:choose>
      <c:when test="${empty description}">
        <c:out value="${name}"/>
      </c:when>
      <c:otherwise>
        <a href="#" rel="tooltip" data-type="info" data-original-title="<c:out value="${description}"/>"><c:out value="${name}"/></a>
      </c:otherwise>
    </c:choose>


  </c:otherwise>
</c:choose>
