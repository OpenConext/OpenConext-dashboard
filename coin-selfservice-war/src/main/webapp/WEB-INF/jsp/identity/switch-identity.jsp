<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="../include.jsp"%>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags"%>


<div class="modal hide fade">
  <div class="modal-header">
    <a class="close" data-dismiss="modal">&times;</a>
    <p class="switch-identity-header">
        <tags:context-specific messageKey="jsp.identity.switch.header" isDashBoard="${isDashBoard}"/>
    </p>
  </div>
  <spring:url var="postSwitchIdentity" value="/identity/do-switch.shtml" />
  <form:form method="post" action="${postSwitchIdentity}">
    <div class="modal-body modal-no-overflow">
        <input type="hidden" name="tokencheck" value="<c:out value='${tokencheck}'/>" />
        <p><spring:message code="jsp.identity.institution" /> </p>
        <form:select id="referenceIdentityProviders" path="institutionName" items="${referenceIdentityProviders}" />
        <p><spring:message code="jsp.identity.role" /> </p>
        <select id="referenceRoles" name="role">
          <c:forEach items="${referenceRoles}" var="role">
            <option value="${role}" <c:if test="${command.role eq role}">selected="selected"</c:if>>
                <spring:message code="jsp.role.information.key.${role}"/>
            </option>
          </c:forEach>
        </select>
    </div>

    <div class="modal-footer">
      <button class="btn btn-primary btn-small" type="submit" name="submit" value="true">Switch</button>
      <button class="btn btn-primary-alt btn-small" type="submit" name="reset" value="true">Reset</button>
      <a class="btn btn-small" data-dismiss="modal">Cancel</a>
    </div>
  </form:form>

  </div>