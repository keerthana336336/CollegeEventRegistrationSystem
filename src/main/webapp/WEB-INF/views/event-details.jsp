<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp">
  <jsp:param name="title" value="${ev.event.title}"/>
  <jsp:param name="active" value="events"/>
</jsp:include>
<main id="main">
  <div class="detail-hero">
    <div class="poster ${ev.categoryClass}" aria-hidden="true">
      <span class="poster-cat"><c:out value="${ev.event.category}"/></span>
      <div class="poster-date"><b><c:out value="${ev.day}"/></b><span><c:out value="${ev.month}"/></span></div>
    </div>
  </div>
  <div class="container detail-grid">
    <article>
      <p class="small"><a href="${ctx}/events">All events</a></p>
      <div class="detail-title">
        <h1 style="margin:0;font-size:clamp(1.7rem,3.4vw,2.5rem)"><c:out value="${ev.event.title}"/></h1>
        <span class="badge ${ev.statusClass}"><c:out value="${ev.statusLabel}"/></span>
      </div>
      <dl class="facts">
        <div><dt>Date</dt><dd><c:out value="${ev.dateDisplay}"/></dd></div>
        <div><dt>Time</dt><dd><c:out value="${ev.timeDisplay}"/></dd></div>
        <div><dt>Venue</dt><dd><c:out value="${ev.event.venue}"/></dd></div>
        <div><dt>Organizer</dt><dd><c:out value="${ev.event.organizer}"/></dd></div>
        <div><dt>Category</dt><dd><c:out value="${ev.event.category}"/></dd></div>
        <div><dt>Register by</dt><dd><c:out value="${ev.deadlineDisplay}"/></dd></div>
      </dl>
      <h2>About this event</h2>
      <p class="description"><c:out value="${ev.event.description}"/></p>
    </article>

    <aside class="action-card" aria-label="Registration">
      <h3>Registration</h3>
      <div class="seats ${ev.full ? 'is-full' : ''}">
        <div class="seat-bar" role="presentation"><span style="width:${ev.percentFilled}%"></span></div>
        <small>${ev.registered} registered, ${ev.remaining} of ${ev.event.capacity} seats left</small>
      </div>

      <c:choose>
        <c:when test="${sessionScope.userRole == 'ADMIN'}">
          <p class="muted small">You are signed in as an administrator. Students register from their own accounts.</p>
          <a class="btn btn-outline btn-block" href="${ctx}/admin/events/form?id=<c:out value='${ev.event.id}'/>">Edit this event</a>
          <p style="margin-top:.6rem"><a class="btn btn-ghost btn-block" href="${ctx}/admin/registrations?eventId=<c:out value='${ev.event.id}'/>">View registrations</a></p>
        </c:when>
        <c:when test="${empty sessionScope.userId}">
          <c:if test="${ev.open}">
            <p class="muted">Log in with your student account to reserve a seat.</p>
            <c:url var="loginUrl" value="/login"><c:param name="next" value="/event?id=${ev.event.id}"/></c:url>
            <a class="btn btn-primary btn-block" href="${loginUrl}">Log in to register</a>
            <p class="small muted" style="margin-top:.8rem">New here? <a href="${ctx}/register">Create an account</a></p>
          </c:if>
          <c:if test="${not ev.open}"><p class="muted">Registration is not available: <strong><c:out value="${ev.statusLabel}"/></strong>.</p></c:if>
        </c:when>
        <c:when test="${ev.userRegistered}">
          <p><span class="badge badge-violet">You are registered</span></p>
          <p class="muted small">Need to cancel? You can do it from My registrations until the deadline.</p>
          <a class="btn btn-outline btn-block" href="${ctx}/student/registrations">My registrations</a>
        </c:when>
        <c:when test="${ev.open}">
          <form method="post" action="${ctx}/student/event-register">
            <input type="hidden" name="_csrf" value="${csrfToken}">
            <input type="hidden" name="eventId" value="<c:out value='${ev.event.id}'/>">
            <button class="btn btn-primary btn-block" type="submit">Register for this event</button>
          </form>
          <p class="small muted" style="margin-top:.8rem">Registration closes <c:out value="${ev.deadlineDisplay}"/>.</p>
        </c:when>
        <c:otherwise>
          <button class="btn btn-primary btn-block" type="button" disabled>Registration unavailable</button>
          <p class="muted small" style="margin-top:.8rem">
            <c:choose>
              <c:when test="${ev.ended}">This event has already ended.</c:when>
              <c:when test="${ev.full}">All seats are taken.</c:when>
              <c:otherwise>The registration deadline has passed.</c:otherwise>
            </c:choose>
          </p>
        </c:otherwise>
      </c:choose>
    </aside>
  </div>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
