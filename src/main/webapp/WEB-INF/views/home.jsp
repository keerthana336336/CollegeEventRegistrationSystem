<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cc" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp">
  <jsp:param name="title" value="Home"/>
  <jsp:param name="active" value="home"/>
</jsp:include>
<main id="main">
  <section class="hero">
    <div class="container hero-grid">
      <div>
        <h1>Every campus event, one seat away.</h1>
        <p class="hero-lead">Hackathons, cultural nights, workshops and placement talks. Find what is on, reserve your seat before it fills up, and keep all your tickets in one place.</p>
        <form class="hero-search" method="get" action="${ctx}/events" role="search">
          <label class="visually-hidden" for="hero-q">Search events</label>
          <input id="hero-q" type="search" name="q" placeholder="Search hackathon, workshop, cricket...">
          <label class="visually-hidden" for="hero-cat">Category</label>
          <select id="hero-cat" name="category">
            <option value="">All categories</option>
            <c:forEach var="cat" items="${categories}"><option value="${cat}"><c:out value="${cat}"/></option></c:forEach>
          </select>
          <button class="btn btn-violet" type="submit">Find events</button>
        </form>
        <p class="hero-note">${registrationCount} seats booked by ${studentCount} students so far.</p>
      </div>

      <c:choose>
        <c:when test="${not empty nextEvent}">
          <aside class="ticket" aria-label="Next event">
            <div class="ticket-main">
              <span class="ticket-label">Next up: <c:out value="${nextEvent.event.category}"/></span>
              <h2><a href="${ctx}/event?id=<c:out value='${nextEvent.event.id}'/>" style="color:inherit;text-decoration:none"><c:out value="${nextEvent.event.title}"/></a></h2>
              <ul class="ticket-lines">
                <li><c:out value="${nextEvent.dateDisplay}"/></li>
                <li><c:out value="${nextEvent.timeDisplay}"/></li>
                <li><c:out value="${nextEvent.event.venue}"/></li>
              </ul>
            </div>
            <div class="ticket-stub">
              <div class="ticket-seats">
                <strong>${nextEvent.remaining}</strong>
                <span>seats left</span>
              </div>
              <a class="btn btn-primary btn-sm" href="${ctx}/event?id=<c:out value='${nextEvent.event.id}'/>">Reserve a seat</a>
            </div>
          </aside>
        </c:when>
        <c:otherwise>
          <aside class="ticket ticket-empty" aria-label="No events">
            <div class="ticket-main">
              <span class="ticket-label">Nothing scheduled yet</span>
              <h2>New events are on the way</h2>
              <p class="muted">Create an account now and you will be ready when registration opens.</p>
            </div>
            <div class="ticket-stub">
              <span class="muted small">Check back soon</span>
              <a class="btn btn-primary btn-sm" href="${ctx}/register">Create account</a>
            </div>
          </aside>
        </c:otherwise>
      </c:choose>
    </div>
  </section>

  <section class="section">
    <div class="container">
      <div class="section-head">
        <div>
          <h2>Coming up on campus</h2>
          <p>The next events open for registration.</p>
        </div>
        <a class="btn btn-outline" href="${ctx}/events">See all events</a>
      </div>
      <c:choose>
        <c:when test="${not empty featured}">
          <div class="card-grid">
            <c:forEach var="v" items="${featured}"><cc:eventCard view="${v}"/></c:forEach>
          </div>
        </c:when>
        <c:otherwise>
          <div class="empty"><h3>No upcoming events</h3><p>When an administrator publishes an event it will appear here.</p></div>
        </c:otherwise>
      </c:choose>
    </div>
  </section>

  <section class="section section-alt">
    <div class="container">
      <div class="section-head"><h2>Browse by category</h2></div>
      <div class="chips">
        <c:forEach var="cat" items="${categories}">
          <a class="chip" href="${ctx}/events?category=<c:out value='${cat}'/>"><c:out value="${cat}"/></a>
        </c:forEach>
      </div>
    </div>
  </section>

  <section class="section">
    <div class="container">
      <div class="section-head"><h2>How registering works</h2></div>
      <ol class="steps">
        <li><h3>Create your account</h3><p>Sign up with your college e-mail and student ID. It takes under a minute.</p></li>
        <li><h3>Pick your events</h3><p>Check the date, venue and seats left, then register. You cannot be signed up twice and full events close automatically.</p></li>
        <li><h3>Show up</h3><p>Organizers mark attendance on the day. Changed your mind? Cancel before the deadline to free your seat for someone else.</p></li>
      </ol>
    </div>
  </section>

  <c:if test="${empty sessionScope.userId}">
    <section class="section" style="padding-top:0">
      <div class="container">
        <div class="cta-band">
          <div><h2>Ready to grab a seat?</h2><p>Free for every student of the college.</p></div>
          <a class="btn btn-light" href="${ctx}/register">Create your account</a>
        </div>
      </div>
    </section>
  </c:if>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
