<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="include.jsp"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>


<div class="modal hide fade">
  <div class="modal-header">
    <a class="close" data-dismiss="modal">&times;</a>
    <p class="recommendation-header">
      <spring:message code="jsp.app_recommendation.header" arguments="${service.name}" />
    </p>
  </div>
  <div class="modal-body">
    <form id="recommend-form">
      <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>" /> 
      <input type="hidden" name="serviceId" value="${service.id}" />
      <spring:url context="${pageContext.request.contextPath}" var="detailAppLink" value="app-detail.shtml">
        <spring:param name="serviceId" value="${service.id}" />
      </spring:url>
      <input type="hidden" name="detailAppStoreLink" value="${detailAppLink}" />
      <p><spring:message code="jsp.app_recommendation.email_selection_text" arguments="${service.name}" />
      </p>
      <div class="error hide">
      </div>
      <c:set var="toShortInput">
        <spring:message code="jsp.app_recommendation.format_input_too_short" />
      </c:set>
      <c:set var="toManyInput">
        <spring:message code="jsp.app_recommendation.format_input_too_many" />
      </c:set>
      <input type="hidden" name="emailSelect2" id="email-select2" data-max-selection-size="${maxRecommendationEmails}" 
      data-format-input-too-short="${toShortInput}" data-format-selection-too-big="${toManyInput}"/>
      <p>
        <spring:message code="jsp.app_recommendation.note_text" />
      </p>
      <textarea name="recommendPersonalNote" class="recommendation-text-input" rows="7"/> 
    </form>
  </div>

  <div class="modal-footer">
    <spring:url var="postRecommendApp" value="/do-app-recommend.shtml" />
    <a id="recommend-link" class="btn btn-primary" data-post-url="${postRecommendApp}"><spring:message code="jsp.app_recommendation.recommend_app_submit" /></a>
    <a class="btn" data-dismiss="modal" id="close-recommend-modal"><spring:message code="jsp.app_recommendation.recommend_app_close" /></a>
  </div>
</div>