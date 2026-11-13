<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cc" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp"><jsp:param name="title" value="Dashboard"/></jsp:include>
<div class="container dash">
  <jsp:include page="/WEB-INF/views/partials/student-sidebar.jsp"><jsp:param name="active" value="dashboard"/></jsp:include>
  <main id="main" class="dash-main">
    <h1>Hi, <c:out value="${sessionScope.userName}"/></h1>
    <p class="dash-sub"><c:out value="${profile.department}"/>, year <c:out value="${profile.year}"/>. Student ID <c:out value="${profile.studentId}"/>.</p>

    <div class="stats">
      <div class="stat"><strong>${activeCount}</strong><span>Active registrations</span></div>
      <div class="stat"><strong>${upcomingCount}</strong><span>Upcoming for you</span></div>
      <div class="stat"><strong>${attendedCount}</strong><span>Events attended</span></div>
    </div>

    <section class="panel">
      <h2>Your next events</h2>
      <c:choose>
        <c:when test="${not empty upcomingRegs}">
          <ul class="next-list">
            <c:forEach var="r" items="${upcomingRegs}">
              <li>
                <div class="date-chip"><b><c:out value="${r.day}"/></b><span><c:out value="${r.month}"/></span></div>
                <div class="info">
                  <a href="${ctx}/event?id=<c:out value='${r.event.id}'/>"><c:out value="${r.event.title}"/></a>
                  <span class="sub"><c:out value="${r.timeDisplay}"/> at <c:out value="${r.event.venue}"/></span>
                </div>
                <span class="badge ${r.statusClass}"><c:out value="${r.statusLabel}"/></span>
              </li>
            </c:forEach>
          </ul>
          <p style="margin:1rem 0 0"><a href="${ctx}/student/registrations">See all my registrations</a></p>
        </c:when>
        <c:otherwise>
          <div class="empty">
            <h3>Nothing booked yet</h3>
            <p>Pick an event below and reserve your first seat.</p>
            <a class="btn btn-primary" href="${ctx}/events">Browse events</a>
          </div>
        </c:otherwise>
      </c:choose>
    </section>

    <c:if test="${not empty suggestions}">
      <h2>Open for registration</h2>
      <div class="card-grid" style="margin-bottom:2rem">
        <c:forEach var="v" items="${suggestions}"><cc:eventCard view="${v}"/></c:forEach>
      </div>
    </c:if>
  </main>
</div>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
