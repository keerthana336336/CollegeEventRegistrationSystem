<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:if test="${not empty sessionScope.flashMessage}">
  <div class="toast-stack" role="status" aria-live="polite">
    <div class="toast ${sessionScope.flashType == 'error' ? 'toast-error' : 'toast-success'}">
      <span><c:out value="${sessionScope.flashMessage}"/></span>
      <button class="toast-close" type="button" aria-label="Dismiss message">&times;</button>
    </div>
  </div>
  <c:remove var="flashMessage" scope="session"/>
  <c:remove var="flashType" scope="session"/>
</c:if>
