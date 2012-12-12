<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="include.jsp"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>

<div class="modal hide fade">
  <div class="modal-header">
    <a class="close" data-dismiss="modal">&times;</a>
    <c:set var="spname">
      <tags:providername provider="${compoundSp.sp}" />
    </c:set>
    <p class="recommendation-header">
      <spring:message code="jsp.app_recommendation.header" arguments="${spname}" />
    </p>
  </div>
  <div class="modal-body">
    <!-- nav class="email-filter">
      <div class="show"-->
    <p>You can send a recommendation to one or more people. Either select people from you teams or type in the email</p>
    <input type="hidden" id="email-select2" style="width:70%"/>
    <p>(Optional) Send a personal message with your recommendation</p>
    <textarea class="recommendation-textinput" placeholder="Enter your message" rows="8"> 
    </textarea>
    <!-- /div>
    </nav-->
  </div>
  <div class="modal-footer">
    <a class="btn btn-primary">Recommend</a> 
    <a class="btn" data-dismiss="modal">Close</a>
  </div>
</div>