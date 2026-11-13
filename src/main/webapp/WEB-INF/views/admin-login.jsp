<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp">
  <jsp:param name="title" value="Admin login"/>
</jsp:include>
<main id="main" class="auth auth-admin">
  <section class="auth-side">
    <h1>Event administration</h1>
    <p>Publish events, watch seats fill up, mark attendance and export registration lists.</p>
    <p>This page is for authorised staff only. Students log in on the <a href="${ctx}/login" style="color:#fff">student login page</a>.</p>
  </section>
  <section class="auth-main">
    <div class="auth-card">
      <h2>Admin login</h2>
      <c:if test="${not empty formError}"><div class="alert alert-error" role="alert"><c:out value="${formError}"/></div></c:if>
      <form method="post" action="${ctx}/admin/login" class="js-validate" novalidate>
        <input type="hidden" name="_csrf" value="${csrfToken}">
        <div class="field">
          <label for="email">Admin e-mail</label>
          <input id="email" name="email" type="email" required readonly autocomplete="username" value="<c:out value='${email}'/>">
        </div>
        <div class="field">
          <label for="password">Password</label>
          <input id="password" name="password" type="password" required autocomplete="current-password">
        </div>
        <button class="btn btn-primary btn-block" type="submit">Log in as admin</button>
      </form>
    </div>
  </section>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
