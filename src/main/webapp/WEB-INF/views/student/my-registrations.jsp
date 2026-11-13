<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp"><jsp:param name="title" value="My registrations"/></jsp:include>
<div class="container dash">
  <jsp:include page="/WEB-INF/views/partials/student-sidebar.jsp"><jsp:param name="active" value="registrations"/></jsp:include>
  <main id="main" class="dash-main">
    <h1>My registrations</h1>
    <p class="dash-sub">Every event you have signed up for, with its current status.</p>

    <div class="tabs" aria-label="Filter by status">
      <a href="${ctx}/student/registrations" class="${status == 'all' ? 'active' : ''}">All</a>
      <a href="${ctx}/student/registrations?status=registered" class="${status == 'registered' ? 'active' : ''}">Registered</a>
      <a href="${ctx}/student/registrations?status=attended" class="${status == 'attended' ? 'active' : ''}">Attended</a>
      <a href="${ctx}/student/registrations?status=cancelled" class="${status == 'cancelled' ? 'active' : ''}">Cancelled</a>
    </div>

    <section class="panel panel-flush">
      <c:choose>
        <c:when test="${not empty regs}">
          <div class="table-wrap">
            <table class="data">
              <thead>
                <tr><th>Event</th><th>When</th><th>Venue</th><th>Status</th><th>Registered on</th><th><span class="visually-hidden">Actions</span></th></tr>
              </thead>
              <tbody>
                <c:forEach var="r" items="${regs}">
                  <tr>
                    <td class="title-cell">
                      <a href="${ctx}/event?id=<c:out value='${r.event.id}'/>"><c:out value="${r.event.title}"/></a>
                      <span class="sub"><c:out value="${r.event.category}"/></span>
                    </td>
                    <td><c:out value="${r.dateDisplay}"/><span class="sub"><c:out value="${r.timeDisplay}"/></span></td>
                    <td><c:out value="${r.event.venue}"/></td>
                    <td><span class="badge ${r.statusClass}"><c:out value="${r.statusLabel}"/></span></td>
                    <td><c:out value="${r.registeredAtDisplay}"/></td>
                    <td>
                      <div class="actions">
                        <a class="btn btn-outline btn-sm" href="${ctx}/event?id=<c:out value='${r.event.id}'/>">View</a>
                        <c:if test="${r.canCancel}">
                          <form class="inline-form" method="post" action="${ctx}/student/event-cancel"
                                data-confirm-title="Cancel registration?"
                                data-confirm="You will give up your seat for <c:out value='${r.event.title}'/>. You can register again later if seats are still available."
                                data-confirm-button="Cancel registration">
                            <input type="hidden" name="_csrf" value="${csrfToken}">
                            <input type="hidden" name="registrationId" value="<c:out value='${r.registration.id}'/>">
                            <button class="btn btn-danger-outline btn-sm" type="submit">Cancel</button>
                          </form>
                        </c:if>
                      </div>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </c:when>
        <c:otherwise>
          <div class="empty">
            <h3>No registrations here</h3>
            <p><c:choose>
              <c:when test="${status == 'all'}">You have not registered for any event yet.</c:when>
              <c:otherwise>None of your registrations have this status.</c:otherwise>
            </c:choose></p>
            <a class="btn btn-primary" href="${ctx}/events">Browse events</a>
          </div>
        </c:otherwise>
      </c:choose>
    </section>
  </main>
</div>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
