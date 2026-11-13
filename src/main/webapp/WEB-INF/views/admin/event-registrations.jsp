<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp"><jsp:param name="title" value="Registrations"/></jsp:include>
<div class="container dash">
  <jsp:include page="/WEB-INF/views/partials/admin-sidebar.jsp"><jsp:param name="active" value="registrations"/></jsp:include>
  <main id="main" class="dash-main">
    <h1>Event registrations</h1>
    <p class="dash-sub">See who signed up, mark attendance on the day and download the list.</p>

    <form class="toolbar" method="get" action="${ctx}/admin/registrations" role="search">
      <div>
        <label class="visually-hidden" for="eventId">Event</label>
        <select id="eventId" name="eventId">
          <option value="">All events</option>
          <c:forEach var="e" items="${allEvents}">
            <option value="${e.id}" ${eventId == e.id ? 'selected' : ''}><c:out value="${e.title}"/> (<c:out value="${e.date}"/>)</option>
          </c:forEach>
        </select>
      </div>
      <div class="search">
        <label class="visually-hidden" for="q">Search registrations</label>
        <input id="q" type="search" name="q" value="<c:out value='${q}'/>" placeholder="Search by name, e-mail, student ID or department">
      </div>
      <button class="btn btn-primary" type="submit">Search</button>
      <c:url var="exportUrl" value="/admin/registrations/export">
        <c:param name="eventId" value="${eventId}"/>
        <c:param name="q" value="${q}"/>
      </c:url>
      <a class="btn btn-outline" href="${exportUrl}">Export CSV</a>
    </form>

    <c:if test="${not empty selectedEvent}">
      <div class="summary-strip">
        <strong><c:out value="${selectedEvent.event.title}"/></strong>
        <span>${selectedEvent.registered} registered of ${selectedEvent.event.capacity}</span>
        <span>${selectedEvent.remaining} seats left</span>
        <span class="badge ${selectedEvent.statusClass}"><c:out value="${selectedEvent.statusLabel}"/></span>
      </div>
    </c:if>

    <section class="panel panel-flush">
      <c:choose>
        <c:when test="${not empty regs}">
          <div class="table-wrap">
            <table class="data">
              <thead>
                <tr><th>Student</th><th>Department</th><c:if test="${empty selectedEvent}"><th>Event</th></c:if><th>Status</th><th>Registered</th><th>Attendance</th></tr>
              </thead>
              <tbody>
                <c:forEach var="r" items="${regs}">
                  <tr>
                    <td class="title-cell">
                      <c:out value="${r.student.name}"/>
                      <span class="sub"><c:out value="${r.student.studentId}"/> / <c:out value="${r.student.email}"/></span>
                    </td>
                    <td><c:out value="${r.student.department}"/><span class="sub">Year <c:out value="${r.student.year}"/></span></td>
                    <c:if test="${empty selectedEvent}"><td><c:out value="${r.event.title}"/><span class="sub"><c:out value="${r.dateDisplay}"/></span></td></c:if>
                    <td><span class="badge ${r.statusClass}"><c:out value="${r.statusLabel}"/></span></td>
                    <td><c:out value="${r.registeredAtDisplay}"/></td>
                    <td>
                      <c:choose>
                        <c:when test="${not r.registration.active}"><span class="muted small">Not applicable</span></c:when>
                        <c:otherwise>
                          <form class="inline-form" method="post" action="${ctx}/admin/registrations/attendance">
                            <input type="hidden" name="_csrf" value="${csrfToken}">
                            <input type="hidden" name="registrationId" value="<c:out value='${r.registration.id}'/>">
                            <input type="hidden" name="eventId" value="<c:out value='${eventId}'/>">
                            <input type="hidden" name="q" value="<c:out value='${q}'/>">
                            <c:choose>
                              <c:when test="${r.registration.attended}">
                                <input type="hidden" name="attended" value="false">
                                <button class="btn btn-outline btn-sm" type="submit">Undo present</button>
                              </c:when>
                              <c:otherwise>
                                <input type="hidden" name="attended" value="true">
                                <button class="btn btn-primary btn-sm" type="submit">Mark present</button>
                              </c:otherwise>
                            </c:choose>
                          </form>
                        </c:otherwise>
                      </c:choose>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </c:when>
        <c:otherwise>
          <div class="empty"><h3>No registrations found</h3><p>Pick another event or clear the search text.</p></div>
        </c:otherwise>
      </c:choose>
    </section>
  </main>
</div>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
