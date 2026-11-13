<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp"><jsp:param name="title" value="My profile"/></jsp:include>
<div class="container dash">
  <jsp:include page="/WEB-INF/views/partials/student-sidebar.jsp"><jsp:param name="active" value="profile"/></jsp:include>
  <main id="main" class="dash-main">
    <h1>My profile</h1>
    <p class="dash-sub">Keep your details current so organizers can find you on the attendance list.</p>

    <section class="panel">
      <h2>Your details</h2>
      <form method="post" action="${ctx}/student/profile" class="js-validate" novalidate>
        <input type="hidden" name="_csrf" value="${csrfToken}">
        <input type="hidden" name="action" value="details">
        <div class="form-grid">
          <div class="field full ${not empty errors.name ? 'has-error' : ''}">
            <label for="name">Full name</label>
            <input id="name" name="name" type="text" required minlength="2" maxlength="80" value="<c:out value='${profile.name}'/>">
            <c:if test="${not empty errors.name}"><div class="field-error"><c:out value="${errors.name}"/></div></c:if>
          </div>
          <div class="field full">
            <label for="email">E-mail</label>
            <input id="email" type="email" value="<c:out value='${profile.email}'/>" readonly>
            <div class="hint">Your e-mail is your login and cannot be changed here.</div>
          </div>
          <div class="field ${not empty errors.studentId ? 'has-error' : ''}">
            <label for="studentId">Student ID</label>
            <input id="studentId" name="studentId" type="text" required minlength="3" maxlength="20" pattern="[A-Za-z0-9_\-\/]{3,20}"
                   data-pattern-message="Use letters, numbers, - or / only." value="<c:out value='${profile.studentId}'/>">
            <c:if test="${not empty errors.studentId}"><div class="field-error"><c:out value="${errors.studentId}"/></div></c:if>
          </div>
          <div class="field ${not empty errors.year ? 'has-error' : ''}">
            <label for="year">Year of study</label>
            <select id="year" name="year" required>
              <c:forEach var="y" items="${years}"><option value="${y}" ${profile.year == y ? 'selected' : ''}>Year ${y}</option></c:forEach>
            </select>
            <c:if test="${not empty errors.year}"><div class="field-error"><c:out value="${errors.year}"/></div></c:if>
          </div>
          <div class="field full ${not empty errors.department ? 'has-error' : ''}">
            <label for="department">Department</label>
            <select id="department" name="department" required>
              <c:forEach var="d" items="${departments}"><option value="${d}" ${profile.department == d ? 'selected' : ''}><c:out value="${d}"/></option></c:forEach>
            </select>
            <c:if test="${not empty errors.department}"><div class="field-error"><c:out value="${errors.department}"/></div></c:if>
          </div>
        </div>
        <button class="btn btn-primary" type="submit">Save changes</button>
      </form>
    </section>

    <section class="panel">
      <h2>Change password</h2>
      <form method="post" action="${ctx}/student/profile" class="js-validate" novalidate>
        <input type="hidden" name="_csrf" value="${csrfToken}">
        <input type="hidden" name="action" value="password">
        <div class="form-grid">
          <div class="field full ${not empty errors.currentPassword ? 'has-error' : ''}">
            <label for="currentPassword">Current password</label>
            <input id="currentPassword" name="currentPassword" type="password" required autocomplete="current-password">
            <c:if test="${not empty errors.currentPassword}"><div class="field-error"><c:out value="${errors.currentPassword}"/></div></c:if>
          </div>
          <div class="field ${not empty errors.newPassword ? 'has-error' : ''}">
            <label for="newPassword">New password</label>
            <input id="newPassword" name="newPassword" type="password" required minlength="8" maxlength="100" autocomplete="new-password" data-password data-strength="#pw-meter">
            <div class="strength" aria-hidden="true"><span id="pw-meter"></span></div>
            <div class="hint">At least 8 characters with a letter and a number.</div>
            <c:if test="${not empty errors.newPassword}"><div class="field-error"><c:out value="${errors.newPassword}"/></div></c:if>
          </div>
          <div class="field ${not empty errors.confirmNewPassword ? 'has-error' : ''}">
            <label for="confirmNewPassword">Confirm new password</label>
            <input id="confirmNewPassword" name="confirmNewPassword" type="password" required autocomplete="new-password" data-match="#newPassword">
            <c:if test="${not empty errors.confirmNewPassword}"><div class="field-error"><c:out value="${errors.confirmNewPassword}"/></div></c:if>
          </div>
        </div>
        <button class="btn btn-outline" type="submit">Update password</button>
      </form>
    </section>
  </main>
</div>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
