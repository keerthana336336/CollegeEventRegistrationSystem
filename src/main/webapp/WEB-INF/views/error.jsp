<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="code" value="${requestScope['javax.servlet.error.status_code']}"/>
<c:set var="msg" value="${requestScope['javax.servlet.error.message']}"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp">
  <jsp:param name="title" value="Something went wrong"/>
</jsp:include>
<main id="main" class="container error-page">
  <div class="code"><c:out value="${empty code ? 500 : code}"/></div>
  <h1>
    <c:choose>
      <c:when test="${code == 404}">We could not find that page</c:when>
      <c:when test="${code == 403}">That is not allowed</c:when>
      <c:otherwise>Something went wrong on our side</c:otherwise>
    </c:choose>
  </h1>
  <c:if test="${(code == 403 or code == 404) and not empty msg}"><p><c:out value="${msg}"/></p></c:if>
  <c:if test="${code != 403 and code != 404}"><p>The problem was logged. Please go back and try again in a moment.</p></c:if>
  <p><a class="btn btn-primary" href="${ctx}/home">Back to home</a></p>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
