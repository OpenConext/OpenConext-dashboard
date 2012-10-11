<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="input" uri="http://www.springframework.org/tags/form" %>
<%@ include file="../include.jsp" %>
<%--
  Copyright 2012 SURFnet bv, The Netherlands

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not <spring:message code="jsp.compound_sp_select_source"/> file except in compliance with the License.
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
              <input type="hidden" name="source" value="LMNG" />
              <input type="hidden" name="fieldId" value="${field.id}" />
              <button name="usethis" value="usethis" class="btn btn-primary"><spring:message code="jsp.compound_sp_select_source"/></button>
            </form>
            <form class="tab-pane" id="form${fieldId}-surfconext">
              <p>${compoundSp.surfConextFieldValues[field.key]}</p>
              <input type="hidden" name="source" value="SURFCONEXT" />
              <input type="hidden" name="fieldId" value="${field.id}" />
              <button name="usethis" value="usethis" class="btn btn-primary"><spring:message code="jsp.compound_sp_select_source"/></button>
            </form>
            <form class="tab-pane" id="form${fieldId}-distributionchannel">
              <input type="hidden" name="source" value="DISTRIBUTIONCHANNEL" />
              <input type="hidden" name="fieldId" value="${field.id}" />
              <textarea name="value">${compoundSp.distributionFieldValues[field.key]}</textarea>
              <div class="form-actions">
                <button name="usethis" value="usethis" class="btn"><spring:message code="jsp.compound_sp_select_source"/></button>
                <button name="save" value="save" class="btn btn-primary"><spring:message code="jsp.compound_sp_save"/></button>
              </div>
            </form>
            </div>
          </div>
        </div>
      </div>
</c:forEach>


<%-- Images --%>
<c:forEach items="${compoundSp.fieldImages}" var="field">
  <spring:message var="fieldTitle" code="jsp.compoundSp.${field.key}" />
  <c:set var="fieldId" value="fieldimage-${field.id}" />



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
            <c:if test="${compoundSp.lmngFieldValues[field.key]}">
              <img src="${compoundSp.lmngFieldValues[field.key]}">
            </c:if>
            <input type="hidden" name="source" value="LMNG" />
            <input type="hidden" name="fieldId" value="${field.id}" />
            <button name="usethis" value="usethis" class="btn btn-primary">Use this</button>
          </form>
          <form class="tab-pane" id="form${fieldId}-surfconext">
            <c:if test="${compoundSp.surfConextFieldValues[field.key]}">
              <img src="${compoundSp.surfConextFieldValues[field.key]}">
            </c:if>
            <input type="hidden" name="source" value="SURFCONEXT" />
            <input type="hidden" name="fieldId" value="${field.id}" />
            <button name="usethis" value="usethis" class="btn btn-primary">Use this</button>
          </form>
          <form class="tab-pane imageuploadform" id="form${fieldId}-distributionchannel">
            <input type="hidden" name="source" value="DISTRIBUTIONCHANNEL" />
            <input type="hidden" name="fieldId" value="${field.id}" />
            <span id='filename'></span><br/>
            <a href='#' class='attachlink'>Add a file</a><br/>
            <input class="fileinput" id="upload-${fieldId}" type="file" name="file" data-url="upload" multiple style="opacity: 0; filter:alpha(opacity: 0);"><br/>
            <input type='submit' value='Upload' id='submit'/>
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

<script type="text/javascript">

  // TODO: move to proper external JS file. Probably together with transition to new style?

  var alertDiv = function(msg) {
    return $("<div />").addClass("alert").html(msg)
      .append("<button type='button'>x</button>").attr("data-dismiss", "alert").addClass("close")
  }

  $("button[name='usethis'],button[name='save']").click(function(e) {
	  e.preventDefault();
	  postForm(this);	
  });
  
  var postForm = function(button) {
	  var form = $(button).closest('form');
	  var formData = form.serialize();
	  //to include the pressed button in the formData
 	  formData = formData + "&" + button.name + "=" + button.value ;
	  console.log(formData);
	  $.ajax(
		      "compoundSp-update.shtml",
		      {
		        data: formData,
		        type: "post",
		        failure: function(msg) {
		          $(form).prepend(alertDiv("Failure saving data. Details: " + msg));
		        },
		        success: function(result) {
		          console.log("post success: " + result);
		          $(form).prepend(alertDiv("Successfully saved. TODO: message bundle"));

		        }
		      });
  }

  /*
  Begin File upload plugin
   */

  $(function() {
    fileUploadInit();
  });

  function fileUploadInit() {
    $('input:button').button();
    $('#submit').button();

    $('form.imageuploadform').submit(function(event) {
      event.preventDefault();
    });

    $('#reset').click(function() {
      clearForm();
      dialog('Success', 'Fields have been cleared!');
    });

    $('input.fileinput').fileupload({
      dataType: 'json',
      done: function (e, data) {
        $.each(data.result, function (index, file) {
          $('body').data('filelist').push(file);
          $('#filename').append(formatFileDisplay(file));
          $('#attach').empty().append('Add another file');
        });
      }
    });

    // Technique borrowed from http://stackoverflow.com/questions/1944267/how-to-change-the-button-text-of-input-type-file
    // http://stackoverflow.com/questions/210643/in-javascript-can-i-make-a-click-event-fire-programmatically-for-a-file-input
    $(".attachlink").click(function () {
      $(this).closest(".imageuploadform").find("input.fileinput").click();
    });

    $('body').data('filelist', new Array());
  }

  function formatFileDisplay(file) {
    var size = '<span style="font-style:italic">'+(file.size/1000).toFixed(2)+'K</span>';
    return file.name + ' ('+ size +')<br/>';
  }

  function getFilelist() {
    var files = $('body').data('filelist');
    var filenames = '';
    for (var i=0; i<files.length; i<i++) {
      var suffix = (i==files.length-1) ? '' : ',';
      filenames += files[i].name + suffix;
    }
    return filenames;
  }

  function dialog(title, text) {
    $('#msgbox').text(text);
    $('#msgbox').dialog(
      {	title: title,
        modal: true,
        buttons: {"Ok": function()  {
          $(this).dialog("close");}
        }
      });
  }

  function clearForm() {
    $('#owner').val('');
    $('#description').val('');
    $('#filename').empty();
    $('.attachlink').empty().append('Add a file');
    $('body').data('filelist', new Array());
  }

  /*
   End File upload plugin
   */


</script>
