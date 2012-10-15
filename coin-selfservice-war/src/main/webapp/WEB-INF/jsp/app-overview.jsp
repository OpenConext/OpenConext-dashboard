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
      <div class="column-center content-holder app-grid-holder">
        <h1 class="hidden-phone">Mijn apps</h1>
        <section>
          <ul class="app-grid" data-search-placeholder="Search in applications">
            <li>
              <h2><a href="app-detail-page.html">Prezi</a></h2>
              <img src="../images/logos/prezi.png" alt="">
              <p class="desc">Presos</p>
              <p class="open-app"><a href="app-detail-page.html">Open app</a></p>
            </li>
            <li>
              <h2><a href="app-detail-page.html">SkyDrive</a></h2>
              <img src="../images/logos/skydrive.png" alt="">
              <p class="desc">Offline opslag</p>
              <p class="open-app"><a href="app-detail-page.html">Open app</a></p>
            </li>
            <li>
              <h2><a href="app-detail-page.html">Alfresco</a></h2>
              <img src="../images/logos/alfresco.png" alt="">
              <p class="desc">Document management</p>
              <p class="open-app"><a href="app-detail-page.html">Open app</a></p>
            </li>
            <li>
              <h2><a href="app-detail-page.html">Drillster</a></h2>
              <img src="../images/logos/drillster.png" alt="">
              <p class="desc">Online learning</p>
              <p class="open-app"><a href="app-detail-page.html">Open app</a></p>
            </li>
            <li>
              <h2><a href="app-detail-page.html">Strato</a></h2>
              <img src="../images/logos/strato.png" alt="">
              <p class="desc">Websites en hosting</p>
              <p class="open-app"><a href="app-detail-page.html">Open app</a></p>
            </li>
            <li>
              <h2><a href="app-detail-page.html">Google Apps</a></h2>
              <img src="../images/logos/google-apps.png" alt="">
              <p class="desc">Diverse apps</p>
              <p class="open-app"><a href="app-detail-page.html">Open app</a></p>
            </li>
            <li>
              <h2><a href="app-detail-page.html">Veoh</a></h2>
              <img src="../images/logos/veoh.png" alt="">
              <p class="desc">Videos</p>
              <p class="open-app"><a href="app-detail-page.html">Open app</a></p>
            </li>
            <li>
              <h2><a href="app-detail-page.html">Greencloud</a></h2>
              <img src="../images/logos/greencloud.png" alt="">
              <p class="desc">Greencloud</p>
              <p class="open-app"><a href="app-detail-page.html">Open app</a></p>
            </li>
          </ul>
        </section>
      </div><!-- .span9.content-holder -->

    </div>
<jsp:include page="foot.jsp"/>