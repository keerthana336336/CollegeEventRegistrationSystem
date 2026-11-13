<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp"><jsp:param name="title" value="Admin dashboard"/></jsp:include>
<div class="container dash">
  <jsp:include page="/WEB-INF/views/partials/admin-sidebar.jsp"><jsp:param name="active" value="dashboard"/></jsp:include>
  <main id="main" class="dash-main">
    <h1>Admin dashboard</h1>
    <p class="dash-sub">A quick look at how registrations are going.</p>

    <div class="stats">
      <div class="stat"><strong>${totalStudents}</strong><span>Students</span></div>
      <div class="stat"><strong>${totalEvents}</strong><span>Events</span></div>
      <div class="stat"><strong>${totalRegistrations}</strong><span>Active registrations</span></div>
      <div class="stat"><strong>${attendedCount}</strong><span>Marked attended</span></div>
      <div class="stat"><strong>${fullCount}</strong><span>Upcoming events full</span></div>
    </div>

    <div class="form-actions" style="margin-bottom:1.6rem">
      <a class="btn btn-primary" href="${ctx}/admin/events/form">Add event</a>
      <a class="btn btn-outline" href="${ctx}/admin/registrations">All registrations</a>
      <a class="btn btn-outline" href="${ctx}/admin/registrations/export">Export CSV</a>
    </div>

    <section class="panel panel-flush">
      <h2>Events and seats</h2>
      <c:choose>
        <c:when test="${not empty events}">
          <div class="table-wrap">
            <table class="data">
              <thead><tr><th>Event</th><th>Date</th><th>Registered</th><th>Seats left</th><th>Status</th><th></th></tr></thead>
              <tbody>
                <c:forEach var="v" items="${events}">
                  <tr>
                    <td class="title-cell">
                      <a href="${ctx}/admin/registrations?eventId=<c:out value='${v.event.id}'/>"><c:out value="${v.event.title}"/></a>
                      <span class="sub"><c:out value="${v.event.category}"/></span>
                    </td>
                    <td><c:out value="${v.dateDisplay}"/></td>
                    <td>${v.registered} / ${v.event.capacity}</td>
                    <td><c:choose><c:when test="${v.full}"><strong style="color:var(--danger)">Full</strong></c:when><c:otherwise>${v.remaining}</c:otherwise></c:choose></td>
                    <td><span class="badge ${v.statusClass}"><c:out value="${v.statusLabel}"/></span></td>
                    <td><a class="btn btn-outline btn-sm" href="${ctx}/admin/registrations?eventId=<c:out value='${v.event.id}'/>">Registrations</a></td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </c:when>
        <c:otherwise>
          <div class="empty"><h3>No events yet</h3><p>Add your first event so students can start registering.</p><a class="btn btn-primary" href="${ctx}/admin/events/form">Add event</a></div>
        </c:otherwise>
      </c:choose>
    </section>

    <section class="panel panel-flush">
      <h2>Latest registrations</h2>
      <c:choose>
        <c:when test="${not empty recent}">
          <div class="table-wrap">
            <table class="data">
              <thead><tr><th>Student</th><th>Event</th><th>Status</th><th>Registered</th></tr></thead>
              <tbody>
                <c:forEach var="r" items="${recent}">
                  <tr>
                    <td class="title-cell"><c:out value="${r.student.name}"/><span class="sub"><c:out value="${r.student.studentId}"/></span></td>
                    <td><c:out value="${r.event.title}"/></td>
                    <td><span class="badge ${r.statusClass}"><c:out value="${r.statusLabel}"/></span></td>
                    <td><c:out value="${r.registeredAtDisplay}"/></td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </c:when>
        <c:otherwise><div class="empty"><p>No one has registered yet.</p></div></c:otherwise>
      </c:choose>
    </section>
  </main>
</div>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
