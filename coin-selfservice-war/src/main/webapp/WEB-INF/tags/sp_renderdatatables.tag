<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%@attribute name="columnFilter" type="java.lang.String"
             description="Configuration of the columnfilter extension for jQuery datatables" %>
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

<script>

  $(document).ready(function () {
    var oTable = $('#sp_overview_table').dataTable({
      "bPaginate":false,
      "bLengthChange":false,
      "bSort":false,
      "bAutoWidth":false,
      "oLanguage":{
        "sSearch":"_INPUT_",
        "sZeroRecords":"<spring:message code="datatables.sZeroRecords"/>",
        "sInfo":"<spring:message code="datatables.sInfo"/>",
        "sInfoEmpty":"<spring:message code="datatables.sInfoEmpty"/>",
        "sInfoFiltered":"<spring:message code="datatables.sInfoFiltered"/>"
      }
    });

    $('#sp_overview_table').dataTable().columnFilter(${columnFilter});

  });

</script>