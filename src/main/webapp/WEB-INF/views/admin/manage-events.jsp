<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp"><jsp:param name="title" value="Manage events"/></jsp:include>
<div class="container dash">
  <jsp:include page="/WEB-INF/views/partials/admin-sidebar.jsp"><jsp:param name="active" value="events"/></jsp:include>
  <main id="main" class="dash-main">
    <h1>Manage events</h1>
    <p class="dash-sub">Add, edit or remove events. Past events stay listed so you can still export their attendance.</p>

    <form class="toolbar" method="get" action="${ctx}/admin/events" role="search">
      <div class="search">
        <label class="visually-hidden" for="q">Search events</label>
        <input id="q" type="search" name="q" value="<c:out value='${q}'/>" placeholder="Search by title, venue or organizer">
      </div>
      <div>
        <label class="visually-hidden" for="category">Category</label>
        <select id="category" name="category">
          <option value="">All categories</option>
          <c:forEach var="cat" items="${categories}"><option value="${cat}" ${selectedCategory == cat ? 'selected' : ''}><c:out value="${cat}"/></option></c:forEach>
        </select>
      </div>
      <button class="btn btn-outline" type="submit">Filter</button>
      <span class="spacer"></span>
      <a class="btn btn-primary" href="${ctx}/admin/events/form">Add event</a>
    </form>

    <section class="panel panel-flush">
      <c:choose>
        <c:when test="${not empty events}">
          <div class="table-wrap">
            <table class="data">
              <thead><tr><th>Event</th><th>When</th><th>Venue</th><th>Seats</th><th>Status</th><th>Actions</th></tr></thead>
              <tbody>
                <c:forEach var="v" items="${events}">
                  <tr>
                    <td class="title-cell">
                      <a href="${ctx}/event?id=<c:out value='${v.event.id}'/>"><c:out value="${v.event.title}"/></a>
                      <span class="sub"><c:out value="${v.event.category}"/> by <c:out value="${v.event.organizer}"/></span>
                    </td>
                    <td><c:out value="${v.dateDisplay}"/><span class="sub"><c:out value="${v.timeDisplay}"/></span></td>
                    <td><c:out value="${v.event.venue}"/></td>
                    <td>${v.registered} / ${v.event.capacity}<span class="sub">${v.remaining} left</span></td>
                    <td><span class="badge ${v.statusClass}"><c:out value="${v.statusLabel}"/></span></td>
                    <td>
                      <div class="actions">
                        <a class="btn btn-outline btn-sm" href="${ctx}/admin/registrations?eventId=<c:out value='${v.event.id}'/>">Registrations</a>
                        <a class="btn btn-outline btn-sm" href="${ctx}/admin/events/form?id=<c:out value='${v.event.id}'/>">Edit</a>
                        <form class="inline-form" method="post" action="${ctx}/admin/events/delete"
                              data-confirm-title="Delete this event?"
                              data-confirm="<c:out value='${v.event.title}'/> and its ${v.registered} active registration(s) will be permanently deleted."
                              data-confirm-button="Delete event">
                          <input type="hidden" name="_csrf" value="${csrfToken}">
                          <input type="hidden" name="id" value="<c:out value='${v.event.id}'/>">
                          <button class="btn btn-danger-outline btn-sm" type="submit">Delete</button>
                        </form>
                      </div>
                    </td>
                  </tr>
                </c:forEach>
              </tbody>
            </table>
          </div>
        </c:when>
        <c:otherwise>
          <div class="empty"><h3>No events found</h3><p>Change the filters or add a new event.</p><a class="btn btn-primary" href="${ctx}/admin/events/form">Add event</a></div>
        </c:otherwise>
      </c:choose>
    </section>
  </main>
</div>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
