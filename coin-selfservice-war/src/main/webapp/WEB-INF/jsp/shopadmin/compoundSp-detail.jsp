<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="input" uri="http://www.springframework.org/tags/form" %>
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

<%--@elvariable id="compoundSp" type="nl.surfnet.coin.selfservice.domain.CompoundServiceProvider"--%>


<c:set var="title">
  <tags:providername provider="${compoundSp.sp}"/>
</c:set>

<jsp:include page="../header.jsp">
  <jsp:param name="title" value="${title}"/>
</jsp:include>

<section>

  <h2>${title}</h2>

  <div class="content">
    ${compoundSp}



    <!--- test -->
      <div class="accordion" id="fieldaccordion">

<c:forEach items="${compoundSp.fields}" var="field">
  <spring:message var="fieldTitle" code="jsp.compoundSp.${field.key}" />
  <c:set var="fieldId" value="f-${field.id}" />

      <div class="accordion-group">
        <div class="accordion-heading">
          <a class="accordion-toggle" data-toggle="collapse" data-parent="#fieldaccordion" href="#${fieldId}-body">
            ${fieldTitle}
          </a>
        </div>
        <div id="${fieldId}-body" class="accordion-body collapse">
          <div class="accordion-inner">
            <ul class="nav nav-tabs">
              <li class="active"><a data-toggle="tab" class="sourceTab" href="#form${fieldId}-lmng">SURFMarket</a></li>
              <li><a data-toggle="tab" class="sourceTab" href="#form${fieldId}-surfconext">SURFConext</a></li>
              <li><a data-toggle="tab" class="sourceTab" href="#form${fieldId}-distributionchannel">Distributiekanaal</a></li>
            </ul>
            <div class="tab-content">

            <form class="tab-pane active" id="form${fieldId}-lmng">
              <p>${compoundSp.lmngFieldValues[field.key]}</p>
              <button name="usethis" value="usethis" class="btn btn-primary">Use this</button>
            </form>
            <form class="tab-pane" id="form${fieldId}-surfconext">
              <p>${compoundSp.surfConextFieldValues[field.key]}</p>
              <button name="usethis" value="usethis" class="btn btn-primary">Use this</button>
            </form>
            <form class="tab-pane" id="form${fieldId}-distributionchannel">
              <input type="hidden" name="compoundServiceProviderId" value="${compoundSp.id}" />
              <textarea>${compoundSp.distributionFieldValues[field.key]}</textarea>

              <div class="form-actions">
                <button name="usethis" value="usethis" class="btn">Use this</button>
                <button name="save" value="save" class="btn btn-primary">Save</button>
              </div>
            </form>
            </div>
          </div>
        </div>
      </div>
</c:forEach>
    </div>



  </div>
</section>

<jsp:include page="../footer.jsp">
  <jsp:param name="datatables" value="false"/>
</jsp:include>