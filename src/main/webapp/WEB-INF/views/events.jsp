<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="cc" tagdir="/WEB-INF/tags" %>
<jsp:include page="/WEB-INF/views/partials/header.jsp">
  <jsp:param name="title" value="Events"/>
  <jsp:param name="active" value="events"/>
</jsp:include>
<main id="main">
  <div class="page-head">
    <div class="container">
      <h1>Upcoming events</h1>
      <p>Search by name, venue or organizer, or narrow down by category.</p>
    </div>
  </div>
  <section class="section" style="padding-top:2rem">
    <div class="container">
      <form class="toolbar" method="get" action="${ctx}/events" role="search">
        <div class="search">
          <label class="visually-hidden" for="q">Search events</label>
          <input id="q" type="search" name="q" value="<c:out value='${q}'/>" placeholder="Search events">
        </div>
        <div>
          <label class="visually-hidden" for="category">Category</label>
          <select id="category" name="category">
            <option value="">All categories</option>
            <c:forEach var="cat" items="${categories}">
              <option value="${cat}" ${selectedCategory == cat ? 'selected' : ''}><c:out value="${cat}"/></option>
            </c:forEach>
          </select>
        </div>
        <button class="btn btn-primary" type="submit">Search</button>
        <c:if test="${not empty q or not empty selectedCategory}"><a class="btn btn-ghost" href="${ctx}/events">Clear filters</a></c:if>
      </form>

      <div class="chips" style="margin-bottom:1.6rem" aria-label="Quick category filters">
        <a class="chip ${empty selectedCategory ? 'active' : ''}" href="${ctx}/events">All</a>
        <c:forEach var="cat" items="${categories}">
          <a class="chip ${selectedCategory == cat ? 'active' : ''}" href="${ctx}/events?category=<c:out value='${cat}'/>"><c:out value="${cat}"/></a>
        </c:forEach>
      </div>

      <c:choose>
        <c:when test="${not empty events}">
          <p class="muted small">${events.size()} event(s) found</p>
          <div class="card-grid">
            <c:forEach var="v" items="${events}"><cc:eventCard view="${v}"/></c:forEach>
          </div>
        </c:when>
        <c:otherwise>
          <div class="empty">
            <h3>No events match your search</h3>
            <p>Try a different word or clear the filters to see everything that is coming up.</p>
            <a class="btn btn-outline" href="${ctx}/events">Show all events</a>
          </div>
        </c:otherwise>
      </c:choose>
    </div>
  </section>
</main>
<jsp:include page="/WEB-INF/views/partials/footer.jsp"/>
