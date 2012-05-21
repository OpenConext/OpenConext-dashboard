<%--
  ~ Copyright 2012 SURFnet bv, The Netherlands
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~      http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<jsp:include page="header.jsp">
    <jsp:param name="activeSection" value="linked-sps" />
</jsp:include>

<section>

  <h2>Linked Service Providers</h2>

  <div class="content">

    This is a list of Service Providers that have been linked to your Identity Provider:

    <div class="modal modal-relative">
      <div class="modal-body">
      ${sps}
      </div>
    </div>

    <table class="table table-bordered table-striped table-above-pagination">
      <thead>
      <tr>
        <th>Id</th>
        <th>Description</th>
        <th>Since</th>
        <th class="cw55 center">Enabled</th>
        <th class="cw55 center">Active</th>
        <th class="cw55 small center">Actions</th>
      </tr>
      </thead>
      <tbody>
      <tr>
        <td>gapps</td>
        <td>Google apps for Education</td>
        <td>05/21/2012</td>
        <td class="center"><i class="icon-ok"></i></td>
        <td class="center"><i class="icon-ok"></i></td>
        <td class="center">
          <a href="#" rel="tooltip" data-type="info" title="Edit"><i class="icon-pencil"></i></a>
        </td>
      </tr>
      </tbody>
    </table>


  </div>
</section>


<jsp:include page="footer.jsp" />