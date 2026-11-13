<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp">
  <jsp:param name="title" value="Create account"/>
  <jsp:param name="active" value="register"/>
</jsp:include>
<main id="main" class="auth">
  <section class="auth-side">
    <h1>Join the College Event Registration System</h1>
    <p>One student account lets you register for any event, see your tickets and cancel if plans change.</p>
    <ul>
      <li>Seats are held for you the moment you register</li>
      <li>See what is full before you plan your week</li>
      <li>Your attendance is recorded by the organizers</li>
    </ul>
  </section>
  <section class="auth-main">
    <div class="auth-card">
      <h2>Create your student account</h2>
      <p class="muted">Already registered? <a href="${ctx}/login">Log in</a></p>
      <form method="post" action="${ctx}/register" class="js-validate" novalidate>
        <input type="hidden" name="_csrf" value="${csrfToken}">
        <div class="form-grid">
          <div class="field full ${not empty errors.name ? 'has-error' : ''}">
            <label for="name">Full name</label>
            <input id="name" name="name" type="text" required minlength="2" maxlength="80" autocomplete="name" value="<c:out value='${form.name}'/>">
            <c:if test="${not empty errors.name}"><div class="field-error"><c:out value="${errors.name}"/></div></c:if>
          </div>
          <div class="field full ${not empty errors.email ? 'has-error' : ''}">
            <label for="email">College e-mail</label>
            <input id="email" name="email" type="email" required maxlength="120" autocomplete="email" value="<c:out value='${form.email}'/>">
            <c:if test="${not empty errors.email}"><div class="field-error"><c:out value="${errors.email}"/></div></c:if>
          </div>
          <div class="field ${not empty errors.studentId ? 'has-error' : ''}">
            <label for="studentId">Student ID</label>
            <input id="studentId" name="studentId" type="text" required minlength="3" maxlength="20"
                   pattern="[A-Za-z0-9_\-\/]{3,20}" data-pattern-message="Use letters, numbers, - or / only."
                   value="<c:out value='${form.studentId}'/>">
            <c:if test="${not empty errors.studentId}"><div class="field-error"><c:out value="${errors.studentId}"/></div></c:if>
          </div>
          <div class="field ${not empty errors.year ? 'has-error' : ''}">
            <label for="year">Year of study</label>
            <select id="year" name="year" required>
              <option value="">Choose year</option>
              <c:forEach var="y" items="${years}"><option value="${y}" ${form.year == y ? 'selected' : ''}>Year ${y}</option></c:forEach>
            </select>
            <c:if test="${not empty errors.year}"><div class="field-error"><c:out value="${errors.year}"/></div></c:if>
          </div>
          <div class="field full ${not empty errors.department ? 'has-error' : ''}">
            <label for="department">Department</label>
            <select id="department" name="department" required>
              <option value="">Choose department</option>
              <c:forEach var="d" items="${departments}"><option value="${d}" ${form.department == d ? 'selected' : ''}><c:out value="${d}"/></option></c:forEach>
            </select>
            <c:if test="${not empty errors.department}"><div class="field-error"><c:out value="${errors.department}"/></div></c:if>
          </div>
          <div class="field ${not empty errors.password ? 'has-error' : ''}">
            <label for="password">Password</label>
            <input id="password" name="password" type="password" required minlength="8" maxlength="100" autocomplete="new-password" data-password data-strength="#pw-meter">
            <div class="strength" aria-hidden="true"><span id="pw-meter"></span></div>
            <div class="hint">At least 8 characters with a letter and a number.</div>
            <c:if test="${not empty errors.password}"><div class="field-error"><c:out value="${errors.password}"/></div></c:if>
          </div>
          <div class="field ${not empty errors.confirmPassword ? 'has-error' : ''}">
            <label for="confirmPassword">Confirm password</label>
            <input id="confirmPassword" name="confirmPassword" type="password" required autocomplete="new-password" data-match="#password">
            <c:if test="${not empty errors.confirmPassword}"><div class="field-error"><c:out value="${errors.confirmPassword}"/></div></c:if>
          </div>
        </div>
        <button class="btn btn-primary btn-block" type="submit">Create account</button>
      </form>
    </div>
  </section>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
