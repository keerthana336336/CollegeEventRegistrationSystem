<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="editing" value="${not empty form.id}"/>
<jsp:include page="/WEB-INF/views/partials/header.jsp">
  <jsp:param name="title" value="${editing ? 'Edit event' : 'Add event'}"/>
</jsp:include>
<div class="container dash">
  <jsp:include page="/WEB-INF/views/partials/admin-sidebar.jsp"><jsp:param name="active" value="${editing ? 'events' : 'add'}"/></jsp:include>
  <main id="main" class="dash-main">
    <h1><c:out value="${editing ? 'Edit event' : 'Add a new event'}"/></h1>
    <p class="dash-sub">Students see this information on the event page. Times use the server's local time zone.</p>

    <c:if test="${not empty errors}"><div class="alert alert-error" role="alert">Please fix the highlighted fields and save again.</div></c:if>

    <section class="panel">
      <form method="post" action="${ctx}/admin/events/save" class="js-validate" novalidate>
        <input type="hidden" name="_csrf" value="${csrfToken}">
        <input type="hidden" name="id" value="<c:out value='${form.id}'/>">
        <div class="form-grid">
          <div class="field full ${not empty errors.title ? 'has-error' : ''}">
            <label for="title">Title</label>
            <input id="title" name="title" type="text" required minlength="3" maxlength="120" value="<c:out value='${form.title}'/>">
            <c:if test="${not empty errors.title}"><div class="field-error"><c:out value="${errors.title}"/></div></c:if>
          </div>
          <div class="field full ${not empty errors.description ? 'has-error' : ''}">
            <label for="description">Description</label>
            <textarea id="description" name="description" required minlength="10" maxlength="2000"><c:out value="${form.description}"/></textarea>
            <c:if test="${not empty errors.description}"><div class="field-error"><c:out value="${errors.description}"/></div></c:if>
          </div>
          <div class="field ${not empty errors.category ? 'has-error' : ''}">
            <label for="category">Category</label>
            <select id="category" name="category" required>
              <option value="">Choose category</option>
              <c:forEach var="cat" items="${categories}"><option value="${cat}" ${form.category == cat ? 'selected' : ''}><c:out value="${cat}"/></option></c:forEach>
            </select>
            <c:if test="${not empty errors.category}"><div class="field-error"><c:out value="${errors.category}"/></div></c:if>
          </div>
          <div class="field ${not empty errors.capacity ? 'has-error' : ''}">
            <label for="capacity">Capacity (seats)</label>
            <input id="capacity" name="capacity" type="number" required min="1" max="100000" value="${form.capacity}">
            <c:if test="${not empty errors.capacity}"><div class="field-error"><c:out value="${errors.capacity}"/></div></c:if>
          </div>
          <div class="field ${not empty errors.date ? 'has-error' : ''}">
            <label for="date">Date</label>
            <input id="date" name="date" type="date" required value="<c:out value='${form.date}'/>">
            <c:if test="${not empty errors.date}"><div class="field-error"><c:out value="${errors.date}"/></div></c:if>
          </div>
          <div class="field ${not empty errors.registrationDeadline ? 'has-error' : ''}">
            <label for="registrationDeadline">Registration deadline</label>
            <input id="registrationDeadline" name="registrationDeadline" type="datetime-local" required
                   data-before-event="#date,#startTime" value="<c:out value='${form.registrationDeadline}'/>">
            <c:if test="${not empty errors.registrationDeadline}"><div class="field-error"><c:out value="${errors.registrationDeadline}"/></div></c:if>
          </div>
          <div class="field ${not empty errors.startTime ? 'has-error' : ''}">
            <label for="startTime">Start time</label>
            <input id="startTime" name="startTime" type="time" required value="<c:out value='${form.startTime}'/>">
            <c:if test="${not empty errors.startTime}"><div class="field-error"><c:out value="${errors.startTime}"/></div></c:if>
          </div>
          <div class="field ${not empty errors.endTime ? 'has-error' : ''}">
            <label for="endTime">End time</label>
            <input id="endTime" name="endTime" type="time" required data-after="#startTime" value="<c:out value='${form.endTime}'/>">
            <c:if test="${not empty errors.endTime}"><div class="field-error"><c:out value="${errors.endTime}"/></div></c:if>
          </div>
          <div class="field ${not empty errors.venue ? 'has-error' : ''}">
            <label for="venue">Venue</label>
            <input id="venue" name="venue" type="text" required minlength="2" maxlength="120" value="<c:out value='${form.venue}'/>">
            <c:if test="${not empty errors.venue}"><div class="field-error"><c:out value="${errors.venue}"/></div></c:if>
          </div>
          <div class="field ${not empty errors.organizer ? 'has-error' : ''}">
            <label for="organizer">Organizer</label>
            <input id="organizer" name="organizer" type="text" required minlength="2" maxlength="100" value="<c:out value='${form.organizer}'/>">
            <c:if test="${not empty errors.organizer}"><div class="field-error"><c:out value="${errors.organizer}"/></div></c:if>
          </div>
        </div>
        <div class="form-actions">
          <button class="btn btn-primary" type="submit"><c:out value="${editing ? 'Save changes' : 'Publish event'}"/></button>
          <a class="btn btn-ghost" href="${ctx}/admin/events">Cancel</a>
        </div>
      </form>
    </section>
  </main>
</div>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
