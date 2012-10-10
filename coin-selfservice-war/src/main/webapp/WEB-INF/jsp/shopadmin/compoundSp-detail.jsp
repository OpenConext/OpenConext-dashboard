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
      <div class="accordion" id="fieldaccordiontest">


      <div class="accordion-group">
        <div class="accordion-heading">
          <a class="accordion-toggle" data-toggle="collapse" data-parent="#fieldaccordiontest" href="#f-1-body">
            Titel
          </a>
        </div>
        <div id="f-1-body" class="accordion-body collapse">
          <div class="accordion-inner">
            <ul class="nav nav-tabs">
              <li class="active">
                <a href="#">SURFMarket</a>
              </li>
              <li><a href="#">SURFConext</a></li>
              <li><a href="#">Distributiekanaal</a></li>
            </ul>
          </div>
        </div>
      </div>
      <div class="accordion-group">
        <div class="accordion-heading">
          <a class="accordion-toggle" data-toggle="collapse" data-parent="#fieldaccordiontest" href="#f-2-body">
            Titel
          </a>
        </div>

        <div id="f-2-body" class="accordion-body collapse">
          <div class="accordion-inner">
            <ul class="nav nav-tabs">
              <li class="active">
                <a href="#">SURFMarket</a>
              </li>
              <li><a href="#">SURFConext</a></li>
              <li><a href="#">Distributiekanaal</a></li>
            </ul>
            <form class="source-lmng">
              ${field.}
            </form>
            <form class="source-surfconext">

            </form>
            <form class="source-distributionchannel">
              <input type="hidden" name="compoundServiceProviderId" value="${compoundSp.id}" />
              <textarea>${field.value}</textarea>
              <div class="form-actions">
              <button name="usethis" value="usethis" class="btn">Use this</button>
              <button name="save" value="save" class="btn btn-primary">Save</button>
              </div>
            </form>


          </div>
        </div>
      </div>
      </div>


      <!-- /test -->


      <div class="accordion" id="fieldaccordion">
        <c:forEach items="${compoundSp.fields}" var="field">
          <spring:message var="fieldTitle" message="jsp.compoundSpField.${field.key}" />
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
                  <li class="active"><a href="#">SURFMarket</a></li>
                  <li><a href="#">SURFConext</a></li>
                  <li><a href="#">Distributiekanaal</a></li>
                </ul>

                <form>
                  <input type="hidden" name="compoundServiceProviderId" value="${compoundSp.id}" />
                  <textarea>
                    ${field.value}
                  </textarea>
                </form>
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