<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp">
  <jsp:param name="title" value="Log in"/>
  <jsp:param name="active" value="login"/>
</jsp:include>
<main id="main" class="auth">
  <section class="auth-side">
    <h1>Welcome back</h1>
    <p>Log in to see your registrations, reserve new seats and update your profile.</p>
  </section>
  <section class="auth-main">
    <div class="auth-card">
      <h2>Student login</h2>
      <p class="muted">New to the College Event Registration System? <a href="${ctx}/register">Create an account</a></p>
      <c:if test="${not empty formError}"><div class="alert alert-error" role="alert"><c:out value="${formError}"/></div></c:if>
      <form method="post" action="${ctx}/login" class="js-validate" novalidate>
        <input type="hidden" name="_csrf" value="${csrfToken}">
        <input type="hidden" name="next" value="<c:out value='${next}'/>">
        <div class="field">
          <label for="email">E-mail</label>
          <input id="email" name="email" type="email" required autocomplete="username" value="<c:out value='${email}'/>">
        </div>
        <div class="field">
          <label for="password">Password</label>
          <input id="password" name="password" type="password" required autocomplete="current-password">
        </div>
        <button class="btn btn-primary btn-block" type="submit">Log in</button>
      </form>
      <p class="switch">Event organizer? <a href="${ctx}/admin/login">Admin login</a></p>
    </div>
  </section>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
